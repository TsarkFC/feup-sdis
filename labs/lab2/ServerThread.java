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
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                try {
                    if (socket.isClosed()) {
                        System.out.println("closing timer tasks...");
                        timer.purge();
                        timer.cancel();
                        return;
                    }

                    socket.send(packet);
                    System.out.println("packet sent!");
                } catch (Exception e) {
                }
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
        System.out.println("Tasks scheduled!");
    }
}
