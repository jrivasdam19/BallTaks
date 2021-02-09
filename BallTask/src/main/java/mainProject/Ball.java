package mainProject;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Ball implements VisibleObject, Runnable {

    private final Thread BALL_THREAD;
    public static BallTask ballTask;
    private final int SIZE_X = 15;
    private final int SIZE_Y = 15;
    private Color color = Color.BLACK;
    public boolean liveBall;
    private int x = 5;
    private int y = 5;
    private int dx = 1;
    private int dy = 1;
    private static boolean paused;
    private boolean insideBlackHole;
    private String exitWall;

    public String getExitWall() {
        return exitWall;
    }

    public void setExitWall(String exitWall) {
        this.exitWall = exitWall;
    }

    public static void setPaused(boolean paused) {
        Ball.paused = paused;
    }

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

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Thread getBALL_THREAD() {
        return BALL_THREAD;
    }

    public Ball() {
        this.insideBlackHole = false;
        this.paused = false;
        this.liveBall = true;
        this.BALL_THREAD = new Thread(this);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Defines an horizontal bounce by switching horizontal ball coordinate.
     */
    public void bounceHorizontally() {
        this.dx = -(this.dx);
        this.keepMoving();
    }

    /**
     * Defines a vertical bounce by switching vertical ball coordinate.
     */
    public void bounceVertically() {
        this.dy = -(this.dy);
        this.keepMoving();
    }

    /**
     * Creates a new Ball instance with the given coordinates.
     *
     * @param x Horizontal coordinate.
     * @param y Vertical coordinate.
     * @return New Ball instance.
     */
    public static Ball createReceivedBall(int x, int y, int dx, int dy) {
        Ball ball = new Ball();
        ball.x = x;
        ball.y = y;
        ball.dx = dx;
        ball.dy = dy;
        return ball;
    }

    /**
     * Defines ball movement by adding 1 to vertical and horizontal ball coordinates.
     */
    public void keepMoving() {
        this.x += this.dx;
        this.y += this.dy;
    }

    /**
     * Begins execution of ball Thread.
     */
    public void startBall() {
        this.BALL_THREAD.start();
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Defines ball shape.
     *
     * @param x      Horizontal coordinate.
     * @param y      Vertical coordinate.
     * @param width  Ball expected width.
     * @param height Ball expected height.
     * @return Ellipse.
     */
    private Ellipse2D.Double getShape(int x, int y, int width, int height) {
        return new Ellipse2D.Double(x, y, width, height);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void paint(Graphics2D g) {
        g.setColor(this.color);
        g.fill(this.getShape(this.x, this.y, this.SIZE_X, this.SIZE_Y));
        //g.fillOval(this.x, this.y, this.SIZE_X, this.SIZE_Y);
        g.setColor(Color.BLACK);
    }

    @Override
    public void run() {
        while (this.liveBall) {
            if (!paused) ballTask.defineIntersect(this);
            try {
                this.BALL_THREAD.sleep(BallTask.DELAY);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
