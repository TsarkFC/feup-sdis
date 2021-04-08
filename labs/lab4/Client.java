import java.net.*;

import java.io.*;

// java Client <host_name> <port_number> <oper> <opnd> *
// Client: <oper> <opnd>* : <result>
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

    public static void main(String[] args) throws IOException {
        String hostName = args[0];
        Integer portNumber = Integer.parseInt(args[1]);
        String request = getRequest(args);
        if (request == "")
            return;

        try (Socket socket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            String fromUser = getRequest(args);
            out.println(fromUser);
            responseReceived(in.readLine(), args);

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}