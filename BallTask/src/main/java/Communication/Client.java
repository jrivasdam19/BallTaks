package Communication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {

    private String ip = "192.168.1.104";
    private int port = 8082;
    private Thread clientThread;
    private Socket clientSocket;
    private final int DELAY = 4000;
    private boolean running;
    private Channel channel;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public Client(Channel channel) {
        this.running = true;
        this.channel = channel;
        clientThread = new Thread(this);
        clientThread.start();
    }
    //------------------------------------------------------------------------------------------------------------------

    private void setUpConnection() {
        try {
            this.clientSocket = new Socket(this.ip, this.port);
            DataOutputStream outFlow = new DataOutputStream(this.clientSocket.getOutputStream());
            outFlow.writeUTF("BallTask");
            outFlow.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        while (this.running) {
            try {
                this.clientThread.sleep(this.DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.setUpConnection();
            if (this.clientSocket != null) {
                if (this.clientSocket != this.channel.getSocket()) {
                    this.channel.assignSocket(this.clientSocket);
                } else {
                    this.closeSocket(this.clientSocket);
                    this.clientSocket = null;
                }
            }
        }
    }
}
