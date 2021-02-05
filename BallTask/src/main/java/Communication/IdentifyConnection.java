package Communication;

import org.apache.commons.lang3.StringUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

//se encarga de comprobar si el que se conecta es un mainProject.BallTask
public class IdentifyConnection {

    public IdentifyConnection() {

    }

    public boolean identifyBallTask(ServerSocket serverSocket) {
        boolean ballTaskConnection = false;
        try {
            Socket clientSocket = serverSocket.accept();
            String str = (new DataInputStream(clientSocket.getInputStream())).readUTF();
            if (StringUtils.equals("BallTask", str)) {
                System.out.println("cliente identificado!");
                ballTaskConnection = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ballTaskConnection;
    }
}