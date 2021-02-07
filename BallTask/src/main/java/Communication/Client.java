package Communication;

import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {

    private String ip = "192.168.1.27";
    private int port = 8085;
    private Thread clientThread;
    private Socket clientSocket = null;
    private final int DELAY = 4000;
    private boolean running;
    private Channel channel;

    public Client(Channel channel) {
        this.running = true;
        this.channel = channel;
        clientThread = new Thread(this);
        clientThread.start();
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Sets up and identifies connection with server.
     */
    private void setUpConnection() {
        this.clientSocket=null;
        try {
            if (!this.channel.isOk()) {
                this.clientSocket = new Socket(this.ip, this.port);
                DataOutputStream outFlow = new DataOutputStream(this.clientSocket.getOutputStream());
                outFlow.writeUTF("BallTask");
                //outFlow.close();
                DataInputStream inFlow = new DataInputStream(this.clientSocket.getInputStream());
                if (StringUtils.equals(inFlow.readUTF(), "Welcome!")) {
                    this.channel.assignSocket(this.clientSocket);
                    System.out.println("Conexión establecida");
                }
            }
        } catch (IOException e) {
            System.out.println("Problema en setUpConnection()");
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        while (this.running) {
            try {
                this.setUpConnection();
                this.clientThread.sleep(this.DELAY);
            } catch (InterruptedException e) {
                System.out.println("Problema en el run de client");
                e.printStackTrace();
            }

        }
    }
}
