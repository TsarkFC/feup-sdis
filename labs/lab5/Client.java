import java.net.*;
import java.util.Arrays;
import java.io.*;
import javax.net.ssl.*;

// java SSLClient <host> <port> <oper> <opnd>* <cypher-suite>*
public class Client {
    // REGISTER <DNS name> <IP address>
    // LOOKUP <DNS name>
    private static String[] getRequest(String[] args) {
        String oper = args[2];
        String request = "";

        String[] cypherSuites;
        if (oper.equals("register")) {
            request = "REGISTER " + args[3] + " " + args[4];
            cypherSuites = Arrays.copyOfRange(args, 5, args.length);
        } else if (oper.equals("lookup")) {
            request = "LOOKUP " + args[3];
            cypherSuites = Arrays.copyOfRange(args, 4, args.length);
        } else {
            System.out.println("Invalid operation! (Can either be 'register' or 'lookup')");
            return null;
        }

        String[] concat = new String[cypherSuites.length + 1];
        concat[0] = request;
        System.arraycopy(cypherSuites, 0, concat, 1, cypherSuites.length);
        return concat;
    }

    // SSLClient: <oper> <opnd>* : <result>
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

        String[] requestResult = getRequest(args);
        String request = requestResult[0];
        String[] cypherSuites = Arrays.copyOfRange(requestResult, 1, requestResult.length);

        if (request == "")
            return;

        SSLSocket socket = null;
        SSLSocketFactory factory = null;
        factory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            socket = (SSLSocket) factory.createSocket(hostName, portNumber);
        } catch (IOException e) {
            System.out.println("Client: Failed to create SSLSocket");
            e.getMessage();
            return;
        }

        socket.setEnabledCipherSuites(cypherSuites);

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {

            out.println(request);
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