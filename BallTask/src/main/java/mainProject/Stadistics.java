package mainProject;

public class Stadistics {

    private int totalBalls;
    private int ballsOutside;
    private int ballsInside;
    private int ballsWaiting;

    public int getTotalBalls() {
        return totalBalls;
    }

    public int getBallsOutside() {
        return ballsOutside;
    }

    public int getBallsInside() {
        return ballsInside;
    }

    public int getBallsWaiting() {
        return ballsWaiting;
    }

    public Stadistics() {
        this.ballsInside = 0;
        this.ballsOutside = 0;
        this.ballsWaiting = 0;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Adds one new ball to Balls Outside Stadistics field.
     */
    public void addNewBall(){
        this.ballsOutside++;
        this.calculateTotal();
    }

    /**
     * Removes one new ball from Balls Outside Stadistics field.
     */
    public void eraseBall(){
        this.ballsOutside--;
        this.calculateTotal();
    }

    /**
     * Adds one ball to Balls Outside Stadistics field.
     */
    public void addNewBallFromInside() {
        this.ballsInside--;
        this.ballsOutside++;
    }

    public void addNewBallFromOutside() {
        this.ballsOutside--;
        this.ballsInside++;
    }

    public void addNewBallFromWaiting() {
        this.ballsWaiting--;
        this.ballsInside++;
    }
    public void addNewBallWaiting(){
        this.ballsOutside--;
        this.ballsWaiting++;
    }

    public void eraseBalls(){
        this.ballsInside = 0;
        this.ballsOutside = 0;
        this.ballsWaiting = 0;
        this.calculateTotal();
    }

    private void calculateTotal() {
        this.totalBalls = this.ballsInside + this.ballsOutside + this.ballsWaiting;
    }
}