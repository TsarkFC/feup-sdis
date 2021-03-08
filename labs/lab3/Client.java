import java.net.*;
import java.io.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// java Client <host_name> <remote_object_name> <oper> <opnd>*
// <oper> <opnd>*:: <out>
public class Client {
    // REGISTER <DNS name> <IP address>
    // LOOKUP <DNS name>
    private static String getRequest(String[] args) {
        String oper = args[2];
        String request = "";

        switch (oper) {
            case "register":
                if (args.length != 5) {
                    System.out.println("Usage: java Client <host> <remote_object_name> register <DNS name> <IP address>");
                    return "";
                }
                request = "REGISTER " + args[3] + " " + args[4];
                break;
            case "lookup":
                if (args.length != 4) {
                    System.out.println("Usage: java Client <host> <remote_object_name> lookup <DNS name>");
                    return "";
                }
                request = "LOOKUP " + args[3];
                break;
            default:
                System.out.println("Invalid operation! (Can either be 'register' or 'lookup')");
                break;
        }
        return request;
    }

    // Client: <oper> <opnd>* : <result>
    private static void responseReceived(String received, String[] args) {
        String oper = args[2];
        switch (oper) {
            case "register":
                System.out.println(args[2] + " " + args[3] + " " + args[4] + " :: " + received);
                break;
            case "lookup":
                System.out.println(args[2] + " " + args[3] + " :: " + received);
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        String host = args[0];
        String remote_object_name = args[1];
        String request = getRequest(args);
        if (request.equals(""))
            return;

        try {
            Registry registry = LocateRegistry.getRegistry(host);
            RemoteObject stub = (RemoteObject) registry.lookup(remote_object_name);
            String response = stub.processRequest(request);
            responseReceived(response, args);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}