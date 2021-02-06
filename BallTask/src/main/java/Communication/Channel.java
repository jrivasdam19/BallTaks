package Communication;

import mainProject.Ball;
import mainProject.BallTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Channel implements Runnable {

    private Server server;
    private Client client;
    private BallTask ballTask;
    private Socket socket;
    private Thread channelThread;
    private HealthConnection healthConnection;

    public Channel(Server server, Client client, BallTask ballTask) {
        this.healthConnection = new HealthConnection(this);
        this.ballTask = ballTask;
        this.server = server;
        this.client = client;
        this.createSocket();
        this.channelThread = new Thread(this);
        this.channelThread.start();
    }
    //enviar un acknowledge
    //enviar la bola y el thread de la bola tiene que morir
    //este thread detecta que llega información de la conexion
    //méotodo: identificar el comando, si es una bola detectaremos las carac. de la bola
    //primero identificamos el tipo de paquete, y después sacamos los datos.

    private void createSocket() {
        try {
            this.socket = new Socket(this.client.getIp(), this.client.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBall(Ball ball) {
        try {
            ObjectOutputStream outPutObject = new ObjectOutputStream(this.socket.getOutputStream());
            outPutObject.writeObject(ball);
            outPutObject.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveBall() {
        try {
            ObjectInputStream inPutObject = new ObjectInputStream(this.socket.getInputStream());
            if (inPutObject.readObject() instanceof Ball) {
                this.ballTask.generateNewBall((Ball) inPutObject.readObject());
            }
            inPutObject.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.receiveBall();
    }
}
