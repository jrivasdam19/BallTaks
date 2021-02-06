package mainProject;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class Ball implements VisibleObject, Runnable, Serializable {

    private final Thread BALL_THREAD;
    public static BallTask ballTask;
    private final int SIZE_X = 15;
    private final int SIZE_Y = 15;
    private Color color = Color.BLACK;
    public boolean liveBall;
    private double x = 5;
    private double y = 5;
    private double dx = 1;
    private double dy = 1;
    private boolean insideBlackHole;

    public void setLiveBall(boolean liveBall) {
        this.liveBall = liveBall;
    }

    public Color getColor() {
        return color;
    }

    public boolean isInsideBlackHole() {
        return insideBlackHole;
    }

    public void setInsideBlackHole(boolean insideBlackHole) {
        this.insideBlackHole = insideBlackHole;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Thread getBALL_THREAD() {
        return BALL_THREAD;
    }

    public static BallTask getBallTask() {
        return ballTask;
    }

    public static void setBallTask(BallTask ballTask) {
        Ball.ballTask = ballTask;
    }

    public int getSIZE_X() {
        return SIZE_X;
    }

    public int getSIZE_Y() {
        return SIZE_Y;
    }

    public boolean isLiveBall() {
        return liveBall;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public Ball() {
        this.insideBlackHole = false;
        this.liveBall=true;
        this.BALL_THREAD = new Thread(this);
        this.BALL_THREAD.start();
    }


    //------------------------------------------------------------------------------------------------------------------

    private Ellipse2D.Double getShape(double x, double y, double width, double height) {
        return new Ellipse2D.Double(x, y, width, height);
    }

    //------------------------------------------------------------------------------------------------------------------

    public void bounceDiagonally() {
        this.dy = -(this.dy);
        this.dx = -(this.dx);
        this.keepMoving();
    }

    public void bounceHorizontally() {
        this.dx = -(this.dx);
        this.keepMoving();
    }

    public void bounceVertically() {
        this.dy = -(this.dy);
        this.keepMoving();
    }

    public void keepMoving() {
        this.x += this.dx;
        this.y += this.dy;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void paint(Graphics2D g) {
        g.setColor(this.color);
        g.fill(this.getShape(this.x, this.y, this.SIZE_X, this.SIZE_Y));
        g.setColor(Color.BLACK);
    }

    @Override
    public void run() {
        while (this.liveBall) {
            ballTask.defineIntersect(this);
            try {
                this.BALL_THREAD.sleep(BallTask.DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
