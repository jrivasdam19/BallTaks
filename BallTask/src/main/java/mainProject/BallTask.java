package mainProject;

import Communication.Channel;
import Communication.Client;
import Communication.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BallTask extends JFrame implements ActionListener {

    private Viewer viewer;
    private ControlPanel controlPanel;
    private Stadistics stadistics;
    private Server server;
    private Client client;
    private Channel channel;
    private ArrayList<BlackHole> blackHoleList = new ArrayList<>();
    private ArrayList<Ball> ballsToSend = new ArrayList<>();
    private ArrayList<Ball> ballList = new ArrayList<>();

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;
    public static final int DELAY = 6;

    public BallTask() {
        this.blackHoleList.add(new BlackHole(140, 200, 300, 120));
        this.blackHoleList.add(new BlackHole(720, 80, 150, 300));
        this.viewer = new Viewer(this.blackHoleList, this.ballList);
        Ball.ballTask = this;
        this.stadistics = new Stadistics();
        this.controlPanel = new ControlPanel(this.ballList, this.stadistics, this);
        this.channel = new Channel(this);
        this.server = new Server(this.channel);
        this.client = new Client(this.channel);
        this.createFrame();
        this.setResizable(true);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Defines whether ball intersects with Viewer or BlackHoles limits.
     *
     * @param ball
     */
    public void defineIntersect(Ball ball) {
        boolean collision = false;
        String str = "";
        str = this.checkLimits(ball, this.viewer.getBounds());
        if (!str.equals("")) {
            if (this.controlPanel.isOpenedLeftEdge()) {
                if (str.equals("Left")) {
                    ball.setExitWall(str);
                    this.manageBallExit(ball);
                }
            } else if (this.controlPanel.isOpenedRightEdge()) {
                if (str.equals("Right")) {
                    ball.setExitWall(str);
                    this.manageBallExit(ball);
                }
            }
            this.defineBounce(ball, str);
            collision = true;
        } else {
            for (BlackHole blackHole : this.blackHoleList) {
                str = this.checkLimits(ball, blackHole.getRectangle2D().getBounds());
                if (!str.equals("")) {
                    collision = true;
                    this.manageBlackHoleIntersect(ball, blackHole);
                }
            }
        }
        if (!collision) {
            ball.keepMoving();
        }
    }

    /**
     * Creates, starts and adds a new threaded Ball instance into ballList ArrayList. It will display so into Stadistics.
     *
     * @param ball
     */
    public void generateNewBall(Ball ball) {
        this.ballList.add(ball);
        this.stadistics.addNewBall();
        ball.startBall();
    }

    /**
     * Generates a new Ball instance according to the exit wall.
     *
     * @param informationTaken String argument that contains ball features.
     * @return New Ball instance with preset features.
     */
    public Ball manageBallEntry(String informationTaken) {
        int x = Integer.parseInt(informationTaken.split(",")[1]);
        int y = Integer.parseInt(informationTaken.split(",")[2]);
        int dx = Integer.parseInt(informationTaken.split(",")[3]);
        int dy = Integer.parseInt(informationTaken.split(",")[4]);
        switch (informationTaken.split(",")[5]) {
            case "Right":
                x = 0;
                break;
            case "Left":
                x = (int) this.viewer.getBounds().getMaxX();
                break;
        }
        return Ball.createReceivedBall(x, y, dx, dy);
    }

    /**
     * Sends balls that were stopped and stored because of connection problems.
     */
    public void sendWaitingBalls() {
        if(!this.ballsToSend.isEmpty()){
            for (Ball ball : this.ballsToSend) this.channel.sendBallFeatures(ball);
            this.ballsToSend.clear();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Checks in which of rectangle limits intersect happens.
     *
     * @param ball   Ball class instance.
     * @param limits Square shape object bounds.
     * @return String with the intersect.
     */
    private String checkLimits(Ball ball, Rectangle limits) {
        String str = "";
        if (ball.getX() + ball.getDx() == limits.getMinX()) {
            if (ball.getY() + ball.getDy() > limits.getMinY() && ball.getY() + ball.getDy() < limits.getMaxY()) {
                str = "Left";
            }
        } else if (ball.getX() + ball.getDx() == limits.getMaxX()) {
            if (ball.getY() + ball.getDy() > limits.getMinY() && ball.getY() + ball.getDy() < limits.getMaxY()) {
                str = "Right";
                System.out.println("Ball Right side " + (ball.getX() + ball.getDx()));
                System.out.println("Viewer width: " + limits.getMaxX());
                System.out.println("Viewer bounds+getMaxX" + this.viewer.getBounds().getMaxX());
            }
        } else if (ball.getY() + ball.getDy() == limits.getMinY()) {
            if (ball.getX() + ball.getDx() > limits.getMinX() && ball.getX() + ball.getDx() < limits.getMaxX()) {
                str = "V";
            }
        } else if (ball.getY() + ball.getDy() == limits.getMaxY()) {
            if (ball.getX() + ball.getDx() > limits.getMinX() && ball.getX() + ball.getDx() < limits.getMaxX()) {
                str = "V";
            }
        }
        return str;
    }

    /**
     * Set full BlackHoles attribute to false.
     */
    private void clearOutBlackHoles() {
        for (BlackHole blackHole : this.blackHoleList) {
            blackHole.setFull(false);
        }
    }

    /**
     * Manages the creation of BallTask JFrame.
     */
    private void createFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(150, 10, FRAME_WIDTH, FRAME_HEIGHT);
        this.setLayout(new BorderLayout());
        this.add(this.controlPanel, BorderLayout.SOUTH);
        this.add(this.viewer, BorderLayout.CENTER);
    }

    /**
     * Defines with type of bounce ball should do.
     *
     * @param ball
     * @param str  String with the intersect.
     */
    private void defineBounce(Ball ball, String str) {
        switch (str) {
            case "Left":
                ball.bounceHorizontally();
                break;
            case "Right":
                ball.bounceHorizontally();
                break;
            case "V":
                ball.bounceVertically();
                break;
        }
    }

    /**
     * Lets user inserting ip direction and port number to connect with other players.
     */
    private void insertData() {
        String ip = JOptionPane.showInputDialog("Insert your friend IP!");
        int port = Integer.parseInt(JOptionPane.showInputDialog("Insert the number of the port"));
        this.client.setIp(ip);
        this.client.setPort(port);
        this.server.setPort(port);
        this.controlPanel.refreshJLabelText(ip, port);
    }

    /**
     * Let all balls Thread execution finish.
     */
    private void killBallsThread() {
        for (Ball ball : this.ballList) {
            ball.setLiveBall(false);
        }
    }

    /**
     * Passes ball instance to Channel class in order to be sent out. Also remove this instance from ballList and let
     * Thread execution finish.
     *
     * @param ball
     */
    private void manageBallExit(Ball ball) {
        this.stadistics.eraseBall();
        ball.setLiveBall(false);
        this.ballList.remove(ball);
        if (this.channel.isOk()) {
            this.channel.sendBallFeatures(ball);
        } else {
            this.ballsToSend.add(ball);
        }
    }

    /**
     * Manages ball intersect with BlackHole object.
     *
     * @param ball      Ball class instance.
     * @param blackHole BlackHole class instance.
     */
    private synchronized void manageBlackHoleIntersect(Ball ball, BlackHole blackHole) {
        if (blackHole.isFull() && !ball.isInsideBlackHole()) {
            if (ball.getColor() != Color.RED) {
                stadistics.addNewBallWaiting();
            }
            ball.setColor(Color.RED);
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (blackHole.isFull() && ball.isInsideBlackHole()) {
            ball.setColor(Color.BLACK);
            ball.setInsideBlackHole(false);
            blackHole.setFull(false);
            ball.keepMoving();
            stadistics.addNewBallFromInside();
            notifyAll();
        } else if (!blackHole.isFull() && !ball.isInsideBlackHole()) {
            if (ball.getColor() == Color.RED) {
                stadistics.addNewBallFromWaiting();
            } else {
                stadistics.addNewBallFromOutside();
            }
            blackHole.setFull(true);
            ball.setInsideBlackHole(true);
            ball.setColor(Color.GREEN);
            ball.keepMoving();
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        BallTask ballTask = new BallTask();
        ballTask.setVisible(true);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        String str = e.getActionCommand();
        switch (str) {
            case "Add New Ball":
                this.generateNewBall(new Ball());
                break;
            case "New Game":
                this.killBallsThread();
                this.ballList.clear();
                this.stadistics.eraseBalls();
                this.clearOutBlackHoles();
                break;
            case "Resume":
                this.controlPanel.enableButton(str);
                Ball.setPaused(false);
                break;
            case "Stop":
                this.controlPanel.enableButton(str);
                Ball.setPaused(true);
            case "Right side":
                this.controlPanel.changeBoxState(str);
                break;
            case "Left side":
                this.controlPanel.changeBoxState(str);
                break;
            case "Change IP & Port":
                this.insertData();
                break;
            default:
                System.out.println("Not Handled ActionListener in " + e);
                break;
        }
    }
}