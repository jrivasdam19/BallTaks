package Communication;

import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class IdentifyConnection implements Runnable {

    private Socket clientSocket;
    private Channel channel;
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
                this.channel.assignSocket(this.clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.identifyBallTask();
    }
}