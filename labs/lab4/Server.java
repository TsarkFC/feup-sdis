import java.net.*;
import java.io.*;
import java.util.HashMap;

// java Server <srvc_port>
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

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));) {
            String inputLine;
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while ((inputLine = in.readLine()) != null) {
                    String[] request = inputLine.split(" ");
                    boolean register = requestReceived(request);
                    if (register)
                        out.println(register(request, dnsIp));
                    else
                        out.println(lookup(request, dnsIp));
                }
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}