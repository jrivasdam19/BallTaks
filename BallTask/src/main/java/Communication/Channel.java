package Communication;

import mainProject.Ball;
import mainProject.BallTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Channel implements Runnable {

    private BallTask ballTask;
    private Socket socket;
    private Thread channelThread;
    private boolean running;
    //private HealthConnection healthConnection = new HealthConnection(this);

    public Socket getSocket() {
        return socket;
    }

    public Channel(BallTask ballTask) {
        this.running = true;
        this.ballTask = ballTask;
        this.channelThread = new Thread(this);
        this.channelThread.start();
    }
    //enviar un acknowledge
    //enviar la bola y el thread de la bola tiene que morir
    //este thread detecta que llega información de la conexion
    //méotodo: identificar el comando, si es una bola detectaremos las carac. de la bola
    //primero identificamos el tipo de paquete, y después sacamos los datos.

    public boolean isOk() {
        return this.socket != null;
    }

    public synchronized void sendBall(Ball ball) {
        ObjectOutputStream outputStream =null;
        try {
            //this.createSocket();
            outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            outputStream.writeObject(ball);
            outputStream.close();
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

    public synchronized void assignSocket(Socket socket) {
        if (this.socket != null && !this.socket.equals(socket)) {
            this.socket = socket;
        }
    }

    public synchronized void quitSocket() {
        this.closeSocket(this.socket);
        this.socket = null;
    }

    private void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (this.running) {
            ObjectInputStream inputStream = null;
            while (!this.socket.isClosed() && this.isOk()) {
                try {
                    inputStream = new ObjectInputStream(this.socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (inputStream != null) {
                    this.receiveBall(inputStream);
                }
            }
        }
    }
}
