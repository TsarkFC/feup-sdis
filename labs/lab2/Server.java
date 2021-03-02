import java.net.*;
import java.io.*;
import java.util.HashMap;

//multicast: <mcast_addr> <mcast_port>: <srvc_addr> <srvc_port>

// java Server <srvc_port> <mcast_addr> <mcast_port>
public class Server {
    // Server: <oper> <opnd>*
    private static void requestReceived(String[] request) {
        if (request[0].equals("REGISTER")) {
            System.out.println("Server: " + request[0] + " " + request[1] + " " + request[2]);
        } else if (request[0].equals("LOOKUP")) {
            System.out.println("Server: " + request[0] + " " + request[1]);
        }
    }

    private static String cleanBuff(String s) {
        String clean = "";
        for (char c : s.toCharArray()) {
            if ((int) c == 0)
                return clean;
            clean += c;
        }
        return clean;
    }

    private static String register(String[] parsed, HashMap<String, String> dnsIp) {
        String dns = cleanBuff(parsed[1]);
        String ip = cleanBuff(parsed[2]);
        if (dnsIp.get(dns) != null) {
            return "Entry already exists";
        }
        dnsIp.put(dns, ip);
        return "Number of DNS names registered: " + String.valueOf(dnsIp.size());
    }

    private static String lookup(String[] parsed, HashMap<String, String> dnsIp) {
        String dns = cleanBuff(parsed[1]);
        String response = dnsIp.get(dns);
        if (response == null) {
            return "No entry for DNS address: " + dns;
        }
        return dns + " " + response;
    }

    private static DatagramPacket setMulticastMsg(int srvc_port, String srvc_ip, int mcast_port, String mcast_ip,
            MulticastSocket socket) throws IOException {
        String message = srvc_ip + " " + String.valueOf(srvc_port);
        byte[] buf = message.getBytes();
        InetAddress group = InetAddress.getByName(mcast_ip); // "225.0.0.0"
        socket.joinGroup(group);

        return new DatagramPacket(buf, buf.length, group, mcast_port);
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port>");
            return;
        }

        // start thread
        Integer mcast_port = Integer.parseInt(args[2]);
        Integer srvc_port = Integer.parseInt(args[0]);
        String mcast_ip = args[1];
        String srvc_ip = "localhost";

        MulticastSocket mcast_socket = new MulticastSocket(mcast_port); // 4446
        ServerThread multicast = new ServerThread(setMulticastMsg(srvc_port, srvc_ip, mcast_port, mcast_ip, mcast_socket), mcast_socket);
        multicast.start();

        // open server
        InetAddress address = InetAddress.getByName(srvc_ip);
        DatagramSocket socket = new DatagramSocket(srvc_port, address);
        HashMap<String, String> dnsIp = new HashMap<String, String>();
        dnsIp.put("tsark.com", "128.0.0.0");
        System.out.println("server open at " + socket.getLocalSocketAddress());

        // run server
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
            packet = new DatagramPacket(sbuf, sbuf.length, address, packet.getPort());
            socket.send(packet);
        }
        
        multicast.interrupt();
        mcast_socket.close();
        socket.close();
    }
}