package Communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private int port = 8085;
    private Thread serverThread;
    private boolean running;
    private Channel channel;
    private ServerSocket serverSocket = null;
    private Socket clientSocket;

    public Server(Channel channel) {
        this.channel = channel;
        this.running = true;
        this.serverThread = new Thread(this);
        this.serverThread.start();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Creates the a ServerSocket instance with the assigned port.
     */
    private void createConnection() {
        try {
            this.serverSocket = new ServerSocket(this.port);
        } catch (IOException ioException) {
            System.out.println("No se ha podido crear el ServerSocket");
            ioException.printStackTrace();
        }
    }

    /**
     * Put to listen in order to connect with the client.
     */
    private void startConnection() {
        try {
            if (!this.channel.isOk()) {
                this.clientSocket = this.serverSocket.accept();
                System.out.println("Creando conexión con: " + this.clientSocket.getInetAddress().getHostAddress());
                new IdentifyConnection(this.clientSocket, this.channel);
            }
        } catch (Exception e) {
            System.out.println("Problema en startConnection()");
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        this.createConnection();
        while (this.running) {
            this.startConnection();
        }
    }
}