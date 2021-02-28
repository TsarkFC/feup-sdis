import java.io.IOException;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {
    protected DatagramPacket packet = null;
    protected MulticastSocket socket = null;

    ServerThread(DatagramPacket packet, MulticastSocket socket) throws IOException {
        this.packet = packet;
        this.socket = socket;
    }

    public void run() {
        System.out.println("Thread running");
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    socket.send(packet);
                    System.out.println("packet sent!");
                } catch (Exception e) {
                }
            }
        };
        
        System.out.println("Scheduled!");
        new Timer().scheduleAtFixedRate(task, 0, 1000);
        System.out.println("After schedule...");
        System.out.println("End of thread process");
    }
}
