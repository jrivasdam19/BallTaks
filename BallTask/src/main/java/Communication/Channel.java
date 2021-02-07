package Communication;

import mainProject.Ball;
import mainProject.BallTask;
import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Channel implements Runnable {

    private BallTask ballTask;
    private Socket socket = null;
    private Thread channelThread;
    private boolean healthyChannel;
    private HealthConnection healthConnection;

    public Channel(BallTask ballTask) {
        this.ballTask = ballTask;
        this.healthyChannel = false;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Assign new socket and start Channel thread.
     *
     * @param socket
     */
    public synchronized void assignSocket(Socket socket) {
        if (!this.healthyChannel) {
            this.healthyChannel = true;
            this.socket = socket;
            this.channelThread = new Thread(this);
            this.channelThread.start();
            this.healthConnection = new HealthConnection(this);
        }
    }

    /**
     * Return the connection state of the channel.
     *
     * @return True if this connection is established or false otherwise
     */
    public boolean isOk() {
        return this.healthyChannel;
    }

    /**
     * Closes and removes established socket.
     */
    public synchronized void quitSocket() {
        try {
            this.socket.close();
            this.socket = null;
            this.healthyChannel = false;
            System.out.println("Socket removed");
        } catch (IOException e) {
            System.out.println("Problemas quitando el socket de Channel");
            e.printStackTrace();
        }
    }

    /**
     * Send an inputStream in order to verify if connection is OK.
     */
    public void sendAcknowledgment() {
        try {
            DataOutputStream outputStream = new DataOutputStream(this.socket.getOutputStream());
            outputStream.writeUTF("Can you hear me?");
            //outputStream.close();
            System.out.println("Acknowledgment enviado!");
        } catch (IOException e) {
            System.out.println("Problema enviando el acknowledgment");
            e.printStackTrace();
        }
    }

    /**
     * Send an String with the basic features in order to build a new Ball.
     *
     * @param ball Ball instance which characteristics are going to be sent.
     */
    public void sendBallFeatures(Ball ball) {
        try {
            DataOutputStream outputStream = new DataOutputStream(this.socket.getOutputStream());
            outputStream.writeUTF(this.buildStringFeatures(ball));
            //outputStream.close();
            System.out.println("Bola enviada!");
        } catch (IOException e) {
            this.healthyChannel = false;
            System.out.println("Problema en sendBallFeatures()");
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Stringify the ball 'x' position, 'y' position and the exit wall by which it went out.
     *
     * @param ball Ball instance that travel to another client pc.
     * @return String argument that contains ball features.
     */
    private String buildStringFeatures(Ball ball) {
        return "Sending ball" + "," +
                ball.getX() + "," +
                ball.getY() + "," +
                ball.getDx() + "," +
                ball.getDy() + "," +
                ball.getExitWall();
    }

    /**
     * Manage inputStreams received.
     */
    private void receiveAnswer() {
        try {
            DataInputStream inputStream = new DataInputStream(this.socket.getInputStream());
            String informationTaken = inputStream.readUTF();
            if (informationTaken == null) {
                System.out.println("Mensaje nulo en receiveAnswer()");
                this.healthyChannel = false;
            } else if (StringUtils.equals(informationTaken.split(",")[0], "Sending ball")) {
                this.ballTask.generateNewBall(this.ballTask.manageBallEntry(informationTaken));
                System.out.println("Bola regenerada");
            } else if (StringUtils.equals(informationTaken, "Can you hear me?")) {
                DataOutputStream outputStream = new DataOutputStream(this.socket.getOutputStream());
                outputStream.writeUTF("Yes");
                //outputStream.close();
                System.out.println("Contestando al acknowledgment");
            } else if (StringUtils.equals(informationTaken, "Yes")) {
                System.out.println("Recibido Yes!");
                this.healthConnection.setAcknowledgmentReceived(true);
            }
            //inputStream.close();
        } catch (IOException e) {
            System.out.println("Problemas en receiveAnswer");
            this.healthyChannel = false;
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        while (this.healthyChannel) {
            this.receiveAnswer();
        }
    }
}
