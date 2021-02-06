package Communication;

import mainProject.ControlPanel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {

    private ControlPanel controlPanel;
    private String ip = "192.168.1.104";
    private int port = 8082;
    private Thread clientThread;
    private Socket clientSocket;

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public Client(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        clientThread = new Thread(this);
        clientThread.start();
    }
    //------------------------------------------------------------------------------------------------------------------

    private void setUpConnection() {
        try {
            this.clientSocket = new Socket(this.ip, this.port);
            DataOutputStream outFlow = new DataOutputStream(this.clientSocket.getOutputStream());
            System.out.println("Soy en cliente");
            outFlow.writeUTF("BallTask");
            outFlow.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        this.setUpConnection();
    }
}
