import java.net.*;
import java.io.*;

// java Client <host> <port> <oper> <opnd>*
// Client: <oper> <opnd>* : <result>
public class Client {
    //REGISTER <DNS name> <IP address>
    //LOOKUP <DNS name>
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
        } else {
            System.out.println(oper.equals("register"));
            System.out.println("Invalid operation! (Can either be 'register' or 'lookup')");
        }
        return request;
    }

    // Client: <oper> <opnd>* : <result>
    private static void responseReceived(String received, String[] args) {
        String oper = args[2];
        if (oper.equals("register")) {
            System.out.println("Client: "+args[2]+" "+args[3]+" "+args[4]+" : "+received);
        } else if (oper.equals("lookup")) {
            System.out.println("Client: "+args[2]+" "+args[3]+" : "+received);
        }
    }

    public static void main(String[] args) throws IOException{
        if (args.length < 4) {
            System.out.println("Usage: java Client <host> <port> <oper> <opnd>*");
            return;
        }

        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        String request = getRequest(args);
        if (request == "") return;

        // send request
        InetAddress address = InetAddress.getByName(host);
        DatagramSocket socket = new DatagramSocket(8000, address);
        byte[] sbuf = request.getBytes();
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, port);
        socket.send(packet);

        System.out.println("request sent...");
        System.out.println("socket address: " + socket.getLocalSocketAddress());

        // get response
        byte[] rbuf = new byte[sbuf.length];
        packet = new DatagramPacket(rbuf, rbuf.length);
        socket.receive(packet);

        // display response
        String received = new String(packet.getData());
        responseReceived(received, args);
        socket.close();
    }
}