import java.net.*;
import java.io.*;

// java Client <host> <port> <oper> <opnd>*
// Client: <oper> <opnd>* : <result>
public class Client {
    //REGISTER <DNS name> <IP address>
    //LOOKUP <DNS name>
    private static String getRequest(String[] args) {
        String oper = args[3];
        String request = "";

        if (oper == "register") {
            if (args.length != 5) {
                System.out.println("Usage: java Client <host> <port> register <DNS name> <IP address>");
                return "";
            }
            request = "REGISTER" + args[4] + " " + args[5];
        } else if (oper == "lookup") {
            if (args.length != 4) {
                System.out.println("Usage: java Client <host> <port> lookup <DNS name>");
                return "";
            }
            request = "LOOKUP" + args[4];
        } else {
            System.out.println("Invalid operation! (Can either be 'register' or 'lookup')");
        }
        return request;
    }

    // Client: <oper> <opnd>* : <result>
    private static void responseReceived(String received, String[] args) {
        String oper = args[3];
        if (oper == "register") {
            System.out.println("Client: "+args[3]+" "+args[4]+" "+args[5]+" : "+received);
        } else if (oper == "lookup") {
            System.out.println("Client: "+args[3]+" "+args[4]+" : "+received);
        }
    }

    public static void main(String[] args) throws IOException{
        if (args.length != 2) {
            System.out.println("Usage: java Client <host> <port> <oper> <opnd>*");
            return;
        }

        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        String request = getRequest(args);
        if (request == "") return;

        // send request
        DatagramSocket socket = new DatagramSocket();
        byte[] sbuf = request.getBytes();
        InetAddress address = InetAddress.getByName(host);
        DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, port);
        socket.send(packet);

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