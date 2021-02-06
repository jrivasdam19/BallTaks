package Communication;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private int port = 8082;
    private Thread serverThread;
    private boolean running;
    private Channel channel;
    private ServerSocket serverSocket;

    public Server(Channel channel) {
        this.channel = channel;
        this.running = true;
        this.serverThread = new Thread(this);
        this.serverThread.start();
    }

    //------------------------------------------------------------------------------------------------------------------

    private void createConnection() {
        try {
            this.serverSocket = null;
            while (this.running) {
                this.serverSocket = new ServerSocket(this.port);
                Socket clientSocket = serverSocket.accept();
                new IdentifyConnection(clientSocket,this.channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        this.closeSocket(this.serverSocket);
        }
    }

    private void closeSocket(ServerSocket socket){
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.createConnection();
    }
}