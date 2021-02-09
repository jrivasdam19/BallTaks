package Communication;

public class HealthConnection implements Runnable {
    private Thread healthThread;
    private Channel channel;
    private boolean acknowledgmentReceived;
    private int communicationAttempts = 5;
    private final int DELAY = 1000;

    public void setAcknowledgmentReceived(boolean acknowledgmentReceived) {
        this.acknowledgmentReceived = acknowledgmentReceived;
    }

    public HealthConnection(Channel channel) {
        this.channel = channel;
        this.healthThread = new Thread(this);
        this.healthThread.start();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Send an acknowledgment in order to check communication state. If acknowledgment does not arrive in 5 retries
     * it will remove the socket of Channel class.
     */
    private void tryFeedback() {
        try {
            this.acknowledgmentReceived = false;
            this.channel.sendAcknowledgment();
            for (int i = 0; i < this.communicationAttempts && !this.acknowledgmentReceived; i++) {
                Thread.sleep(this.DELAY);
            }
            if (!this.acknowledgmentReceived) {
                this.channel.quitSocket();
            }
        } catch (InterruptedException e) {
            System.out.println("Problemas en tryFeedback");
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        try {
            while (this.channel.isOk()) {
                this.healthThread.sleep(this.DELAY);
                this.tryFeedback();
            }
        } catch (Exception e) {
            System.out.println("Error en el run del HealthChannel");
            e.printStackTrace();
        }
    }
}
