package Communication;

import org.apache.commons.lang3.StringUtils;

//se encarga de comprobar si el que se conecta es un mainProject.BallTask
public class IdentifyConnection {

    private String message;

    public IdentifyConnection(){

    }

    public boolean identifyBallTask(String message){
        boolean ballTaskConnection=false;
        if(StringUtils.equals("mainProject.BallTask",message)){
            ballTaskConnection=true;
        }
        return ballTaskConnection;
    }
}