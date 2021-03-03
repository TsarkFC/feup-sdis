import java.net.*;
import java.io.*;

// multicast: <mcast_addr> <mcast_port>: <srvc_addr> <srvc_port>

// java Client <mcast_addr> <mcast_port> <oper> <opnd> *
public class Client {
    // REGISTER <DNS name> <IP address>
    // LOOKUP <DNS name>
    private static String getRequest(String[] args) {
        String oper = args[2];
        String request = "";

        if (oper.equals("register")) {
            if (args.length != 5) {
                System.out.println("Usage: java Client <host> <port> register <DNS name> <IP address>");
                return "";
            }
            request = "REGISTER " + args[3] + " " + args[4];
        } else if (oper.equals("lookup")) {
            if (args.length != 4) {
                System.out.println("Usage: java Client <host> <port> lookup <DNS name>");
                return "";
            }
            request = "LOOKUP " + args[3];
        } else if (oper.equals("end")) {
            if (args.length != 3) {
                System.out.println("Usage: java Client <host> <port> end");
                return "";
            }
            request = "END";
        } else {
            System.out.println("Invalid operation! (Can either be 'register' or 'lookup')");
        }
        return request;
    }

    // Client: <oper> <opnd>* : <result>
    private static void responseReceived(String received, String[] args) {
        String oper = args[2];
        if (oper.equals("register")) {
            System.out.println("Client: " + args[2] + " " + args[3] + " " + args[4] + " : " + received);
        } else if (oper.equals("lookup")) {
            System.out.println("Client: " + args[2] + " " + args[3] + " : " + received);
        } else if (oper.equals("end")) {
            System.out.println(received);
        }
    }

    // multicast: <mcast_addr> <mcast_port>: <srvc_addr> <srvc_port>
    private static void multicastLog(DatagramPacket packet){
        String data = new String(packet.getData());
        System.out.println("multicast: " + packet.getAddress() + " " + packet.getPort() + " " + data);
    }

    private static String[] getServerAddr(MulticastSocket socket) throws IOException{
        byte[] rbuf = new byte[256];
        DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
        socket.receive(packet);
        multicastLog(packet);

        String rcvd = new String(packet.getData(), 0, packet.getLength());
        return rcvd.split(" ");
    }

    private static void sendRequest(String[] server_addr, DatagramSocket socket, String request) throws IOException{
        InetAddress address = InetAddress.getByName(server_addr[0]);
        socket.setSoTimeout(3000);

        byte[] sbuf = request.getBytes();
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, Integer.parseInt(server_addr[1]));
        socket.send(packet);

        System.out.println("request sent...");
    }

    private static DatagramPacket getResponse(DatagramSocket socket) throws IOException{
        byte[] rbuf = new byte[256];
        DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);

        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            System.out.println("Time out!");
            socket.close();
            return null;
        }

        return packet;
    }

    public static void main(String[] args) throws IOException {
        Integer mcast_port = Integer.parseInt(args[1]);
        String request = getRequest(args);
        if (request == "")
            return;

        // join multicast group
        InetAddress mcast_addr = InetAddress.getByName(args[0]);
        MulticastSocket mcast_socket = new MulticastSocket(mcast_port);
        mcast_socket.joinGroup(mcast_addr);

        // get server address
        String[] server_addr = getServerAddr(mcast_socket);

        // send request
        DatagramSocket socket = new DatagramSocket();
        sendRequest(server_addr, socket, request);

        // get response
        DatagramPacket packet = getResponse(socket);

        // display response
        String received = new String(packet.getData(), 0, packet.getLength());
        responseReceived(received, args);
        socket.close();
    }
}