package Communication;

import mainProject.ControlPanel;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {

    private ControlPanel controlPanel;
    private String ip = "192.168.1.104";
    private int port;
    private Thread clientThread;
    private Socket clientSocket;

    public Client(ControlPanel controlPanel) {
        this.controlPanel=controlPanel;
        clientThread = new Thread(this);
        clientThread.start();
    }
    //------------------------------------------------------------------------------------------------------------------

    private void setUpConnection(){
        try {
            this.clientSocket = new Socket(this.ip,this.port);
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
        this.port = Integer.parseInt(this.controlPanel.getPort().getText());
        this.setUpConnection();
    }
}
