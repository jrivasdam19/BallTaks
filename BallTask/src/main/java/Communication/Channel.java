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
    private ObjectOutputStream outPutBall;
    private ObjectInputStream inPutBall;
    private Socket socket;
    private Thread channelThread;
    private HealthConnection healthConnection;

    public Channel(Server server, Client client, BallTask ballTask) {
        this.healthConnection = new HealthConnection(this);
        this.ballTask = ballTask;
        this.server = server;
        this.client = client;
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
            this.outPutBall=new ObjectOutputStream(this.socket.getOutputStream());
            this.inPutBall=new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendBall(Ball ball) {
        try {
            //this.createSocket();
            this.outPutBall=new ObjectOutputStream(this.socket.getOutputStream());
            this.outPutBall.writeObject(ball);
            this.outPutBall.close();
            System.out.println("bola enviada!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveBall(ObjectInputStream inputStream) {
            try {
                if (inputStream.readObject() instanceof Ball) {
                    this.ballTask.generateNewBall((Ball) inputStream.readObject());
                }
                inputStream.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void run() {
        while (true) {
            if(!this.client.getClientSocket().isClosed() && this.client.getClientSocket()!=null){
                this.socket=this.client.getClientSocket();
                ObjectInputStream inputStream=null;
                try {
                    inputStream=new ObjectInputStream(this.socket.getInputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(inputStream!=null){
                    this.receiveBall(inputStream);
                }
            }
        }
    }
}
