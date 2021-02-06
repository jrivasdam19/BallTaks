package Communication;

public class HealthConnection implements Runnable {
    private Thread healthThread;
    private Channel channel;

    public HealthConnection(Channel channel){
        this.channel=channel;
        this.healthThread=new Thread(this);
        this.healthThread.start();
    }
    @Override
    public void run() {

    }
}
