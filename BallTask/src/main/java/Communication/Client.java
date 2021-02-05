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
            outFlow.writeUTF("mainProject.BallTask");
            outFlow.close();
            //ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            //objectOutputStream.writeObject(new mainProject.Ball());
            //socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        while (true) {
            //this.port = Integer.parseInt(this.controlPanel.getPort().getText());
            this.setUpConnection();
            try {
                this.clientThread.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
