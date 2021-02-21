import java.net.*;
import java.io.*;
import java.util.HashMap;

// java Server <port number>
public class Server {
    //Server: <oper> <opnd>*
    private static void requestReceived(String[] request) {
        if (request[0] == "REGISTER") {
            System.out.println("Server: "+request[0]+" "+request[1]+" "+request[2]);
        } else if (request[0] == "LOOKUP") {
            System.out.println("Server: "+request[0]+" "+request[1]);
        }
    }

    public static void main(String[] args) throws IOException{
        if (args.length != 2) {
            System.out.println("Usage: java Echo <hostname> <string to echo>");
            return;
        }

        DatagramSocket socket = new DatagramSocket();
        HashMap<String, String> dnsIp = new HashMap<String, String>();
        Integer port = Integer.parseInt(args[0]);

        // get request
        byte[] rbuf = new byte[256];
        DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
        socket.receive(packet);

        // parse request
        String received = new String(packet.getData());
        String[] parsed = received.split(" ");
        requestReceived(parsed);

        // execute request
        String response = "";
        if (parsed[0] == "REGISTER") {
            dnsIp.put(parsed[1], parsed[2]);
            response = "entry added";
        } else if (parsed[0] == "LOOKUP") {
            response = dnsIp.get(parsed[1]);
        }

        // send response
        byte[] sbuf = response.getBytes();
        InetAddress address = packet.getAddress();
        packet = new DatagramPacket(sbuf, sbuf.length, address, port);
        socket.send(packet);
    }
}