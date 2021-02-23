import java.net.*;
import java.io.*;
import java.util.HashMap;
import java.util.Arrays;

// java Server <port number>
public class Server {
    //Server: <oper> <opnd>*
    private static void requestReceived(String[] request) {
        if (request[0].equals("REGISTER")) {
            System.out.println("Server: "+request[0]+" "+request[1]+" "+request[2]);
        } else if (request[0].equals("LOOKUP")) {
            System.out.println("Server: "+request[0]+" "+request[1]);
        }
    }

    private static String cleanBuff(String s) {
        String clean = "";
        for (char c : s.toCharArray()) {
            if ((int) c == 0) return clean;
            clean += c;
        }
        return clean;
    }

    public static void main(String[] args) throws IOException{
        if (args.length != 1) {
            System.out.println("Usage: java Server <port number>");
            return;
        }

        Integer port = Integer.parseInt(args[0]);
        InetAddress address = InetAddress.getByName("localhost");
        DatagramSocket socket = new DatagramSocket(port, address);
        HashMap<String, String> dnsIp = new HashMap<String, String>();
        dnsIp.put("tsark.com", "128.0.0.0");

        System.out.println("server open...");
        System.out.println("socket address: " + socket.getLocalSocketAddress());

        while (true) {
            // get request
            byte[] rbuf = new byte[256];
            DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
            socket.receive(packet);

            System.out.println("packet received...");

            // parse request
            String received = new String(packet.getData());
            String[] parsed = received.split(" ");
            requestReceived(parsed);

            // execute request
            String response = "";
            if (parsed[0].equals("REGISTER")) {
                dnsIp.put(parsed[1], parsed[2]);
                response = "entry added: " + parsed[1] + " " + parsed[2];
            } else if (parsed[0].equals("LOOKUP")) {
                String dns = cleanBuff(parsed[1]);
                response = dnsIp.get(dns);
                if (response == null) {
                    response = "No entry for DNS address: " + parsed[1];
                }
            }

            // send response
            byte[] sbuf = response.getBytes();
            address = packet.getAddress();
            port = packet.getPort();
            packet = new DatagramPacket(sbuf, sbuf.length, address, port);
            socket.send(packet);

            System.out.println("response sent to: " + address + ":" + port);
        }
    }
}