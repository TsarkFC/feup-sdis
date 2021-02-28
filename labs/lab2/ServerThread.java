import java.io.IOException;
import java.net.*;

public class ServerThread extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    protected int port = null;
    protected String ip = null;

    ServerThread(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }

    public void run() {

        try {
            socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("225.0.0.0");

            DatagramPacket packet = new DatagramPacket(buf, buf.length, group, 4446);
            socket.send(packet);
        } catch (Exception e) {

        }

        socket.close();
    }
}
