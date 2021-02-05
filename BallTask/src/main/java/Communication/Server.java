package Communication;

import mainProject.ControlPanel;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

//setSoTimeout(ms)
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

    //esto debe estar en el método run para que el hilo lo ejecute cuando se inicie
    private void createConnection() {
        try {
            //abrimos el puerto para escuchar
            this.serverSocket = new ServerSocket(this.port);
            //metemos all esto en un bucle infinito para que acepte todas las conexiones entrantes
            while (true) {
                if(identifyConnection.identifyBallTask(this.serverSocket)) {
                    //aceptamos todas las conexiones
                    this.clientSocket = this.serverSocket.accept();
                    //--------------DETECTAR CLIENTES ONLINE---------------
                    //aquí almacenamos la dirección del cliente
                    InetAddress ipDetected = clientSocket.getInetAddress();
                    this.clientIp = ipDetected.getHostAddress();
                    this.controlPanel.getNeighborIp().setName(this.clientIp);
                    //mainProject.Ball bolaRecibida=new mainProject.Ball();
                    //ObjectInputStream objectInputStream=new ObjectInputStream(clientSocket.getInputStream());
                    //bolaRecibida=(mainProject.Ball)objectInputStream.readObject();
                    //leemos lo que viene en el flujo de datos
                    DataInputStream inFlow = new DataInputStream(clientSocket.getInputStream());
                    //almacenamos el texto en una variable
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