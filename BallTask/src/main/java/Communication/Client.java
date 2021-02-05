package Communication;

import mainProject.ControlPanel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {

    private ControlPanel controlPanel;
    private String ip = "172.16.8.181";
    private int port = 8082;
    private Thread clientThread;
    private Socket clientSocket;

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
