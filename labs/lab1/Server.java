import java.net.*;
import java.io.*;
import java.util.HashMap;

// java Server <port number>
public class Server {
    // Server: <oper> <opnd>*
    private static void requestReceived(String[] request) {
        if (request[0].equals("REGISTER")) {
            System.out.println("Server: " + request[0] + " " + request[1] + " " + request[2]);
        } else if (request[0].equals("LOOKUP")) {
            System.out.println("Server: " + request[0] + " " + request[1]);
        }
    }

    private static String register(String[] parsed, HashMap<String, String> dnsIp) {
        String dns = parsed[1];
        String ip = parsed[2];
        if (dnsIp.get(dns) != null) {
            return "Entry already exists";
        }
        dnsIp.put(dns, ip);
        return "Number of DNS names registered: " + String.valueOf(dnsIp.size());
    }

    private static String lookup(String[] parsed, HashMap<String, String> dnsIp) {
        String dns = parsed[1];
        String response = dnsIp.get(dns);
        if (response == null) {
            return "No entry for DNS address: " + dns;
        }
        return dns + " " + response;
    }

    public static void main(String[] args) throws IOException {
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

        boolean quit = false;
        while (!quit) {
            // get request
            byte[] rbuf = new byte[256];
            DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
            socket.receive(packet);

            // parse request
            String received = new String(packet.getData(), 0, packet.getLength());
            String[] parsed = received.split(" ");
            requestReceived(parsed);

            // execute request
            String response = "";
            if (parsed[0].equals("REGISTER")) {
                response = register(parsed, dnsIp);
            } else if (parsed[0].equals("LOOKUP")) {
                response = lookup(parsed, dnsIp);
            } else if (parsed[0].equals("END")) {
                response = "Server closed...";
                quit = true;
            }

            // send response
            byte[] sbuf = response.getBytes();
            address = packet.getAddress();
            port = packet.getPort();
            packet = new DatagramPacket(sbuf, sbuf.length, address, port);
            socket.send(packet);
        }
        socket.close();
    }
}