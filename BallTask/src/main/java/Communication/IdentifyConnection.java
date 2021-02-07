package Communication;

import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class IdentifyConnection implements Runnable {

    private Socket clientSocket;
    private Channel channel;
    private boolean clientIdentified;
    private Thread t;

    public IdentifyConnection(Socket clientSocket, Channel channel) {
        this.clientSocket = clientSocket;
        this.channel = channel;
        t = new Thread(this);
        t.start();
    }

    private void identifyBallTask() {
        try {
            DataInputStream inputStream = new DataInputStream(this.clientSocket.getInputStream());
            if (StringUtils.equals(inputStream.readUTF(), "BallTask")) {
                System.out.println("detectado cliente BallTask");
                this.channel.assignSocket(this.clientSocket);
                DataOutputStream outputStream = new DataOutputStream(this.clientSocket.getOutputStream());
                outputStream.writeUTF("Welcome!");
                this.clientIdentified = true;
            } else {
                System.out.println("No es un cliente BallTask");
                this.clientIdentified = true;
            }
        } catch (IOException e) {
            System.out.println("Problema en identifyBalltask()");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.clientIdentified = false;
        while (!this.clientIdentified) {
            this.identifyBallTask();
        }
    }
}