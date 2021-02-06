package Communication;

import mainProject.ControlPanel;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private int port = 8082;
    private ControlPanel controlPanel;
    private IdentifyConnection identifyConnection;
    private Thread serverThread;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String clientIp;

    public Socket getClientSocket() {
        return clientSocket;
    }



    public Server(ControlPanel controlPanel) {
        this.controlPanel = controlPanel;
        this.identifyConnection = new IdentifyConnection();
        this.serverThread = new Thread(this);
        this.serverThread.start();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void createConnection() {
        try {

            this.serverSocket = new ServerSocket(this.port);
            while (true) {
                if(identifyConnection.identifyBallTask(this.serverSocket)) {
                    this.clientSocket = this.serverSocket.accept();
                    InetAddress ipDetected = clientSocket.getInetAddress();
                    this.clientIp = ipDetected.getHostAddress();
                    this.controlPanel.getNeighborIp().setName(this.clientIp);
                    DataInputStream inFlow = new DataInputStream(clientSocket.getInputStream());
                    System.out.println("Soy el server");
                    System.out.println(inFlow.readUTF());
                    clientSocket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
            this.createConnection();
    }
}