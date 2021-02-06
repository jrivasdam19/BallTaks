package Communication;

public class HealthConnection implements Runnable {
    private Thread healthThread;
    private Channel channel;
    private boolean running;
    private final int DELAY = 4000;

    public HealthConnection(Channel channel){
        this.running=true;
        this.channel=channel;
        this.healthThread=new Thread(this);
        this.healthThread.start();
    }
    @Override
    public void run() {
        while (this.running) {
            try {
                this.healthThread.sleep(this.DELAY);
                if(this.channel.isOk()){

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
