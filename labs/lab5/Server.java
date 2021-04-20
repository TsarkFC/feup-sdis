import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import javax.net.ssl.*;

// java SSLServer <port> <cypher-suite>*
public class Server {
    // Server: <oper> <opnd>*
    private static boolean requestReceived(String[] request) {
        if (request[0].equals("REGISTER")) {
            System.out.println("Server: " + request[0] + " " + request[1] + " " + request[2]);
            return true;
        } else if (request[0].equals("LOOKUP")) {
            System.out.println("Server: " + request[0] + " " + request[1]);
            return false;
        }
        return false;
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

        HashMap<String, String> dnsIp = new HashMap<String, String>();
        dnsIp.put("tsark.com", "128.0.0.0");

        int portNumber = Integer.parseInt(args[0]);
        String[] cypherSuites = Arrays.copyOfRange(args, 1, args.length);

        SSLServerSocket serverSocket = null;
        SSLServerSocketFactory factory = null;
        factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

        try {
            serverSocket = (SSLServerSocket) factory.createServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Server - Failed to create SSLServerSocket");
            e.getMessage();
            return;
        }

        // Require client authentication
        serverSocket.setNeedClientAuth(true);
        serverSocket.setEnabledCipherSuites(cypherSuites);

        String inputLine;
        while (true) {
            SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            while ((inputLine = in.readLine()) != null) {
                String[] request = inputLine.split(" ");
                boolean register = requestReceived(request);
                if (register)
                    out.println(register(request, dnsIp));
                else
                    out.println(lookup(request, dnsIp));
            }
        }
    }
}