package mainProject;

import Communication.Channel;
import Communication.Client;
import Communication.Server;
import org.apache.commons.lang3.StringUtils;

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
    private ArrayList<Ball> ballList = new ArrayList<>();

    private static final int FRAME_WIDTH = 1000;
    private static final int FRAME_HEIGHT = 700;
    public static final int DELAY = 6;

    public BallTask() {
        this.blackHoleList.add(new BlackHole(140, 200, 300, 120));
        this.blackHoleList.add(new BlackHole(700, 80, 150, 300));
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
        if (!StringUtils.equals(str, "")) {
            if (this.controlPanel.isOpenedLeftEdge()) {
                if (StringUtils.equals(str, "Left")) {
                    ball.setExitWall(str);
                    System.out.println("enviando la bola por la izquierda");
                    this.manageBallExit(ball);
                }
            } else if (this.controlPanel.isOpenedRightEdge()) {
                if (StringUtils.equals(str, "Right")) {
                    ball.setExitWall(str);
                    System.out.println("enviando la bola por la derecha");
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
        double x = Double.parseDouble(informationTaken.split(",")[1]);
        double y = Double.parseDouble(informationTaken.split(",")[2]);
        double dx = Double.parseDouble(informationTaken.split(",")[3]);
        double dy = Double.parseDouble(informationTaken.split(",")[4]);
        switch (informationTaken.split(",")[5]) {
            case "Right":
                x = 0;
                break;
            case "Left":
                x = this.viewer.getBounds().getMaxX();
                break;
        }
        return Ball.createReceivedBall(x, y, dx, dy);
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * Passes ball instance to Channel class in order to be sent out. Also remove this instance from ballList and let
     * Thread execution finish.
     *
     * @param ball
     */
    private void manageBallExit(Ball ball) {
        this.channel.sendBallFeatures(ball);
        this.stadistics.eraseBall();
        ball.setLiveBall(false);
        this.ballList.remove(ball);
    }

    /**
     * Let all Threads execution finish.
     */
    private void killThreads() {
        for (Ball ball : this.ballList) {
            ball.setLiveBall(false);
        }
    }

    /**
     * Defines gridBagLayout constraints for ControlPanel to be added to Frame.
     *
     * @param container
     */
    private void addControlPaneToFrame(Container container) {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.VERTICAL;
        //c.insets=new Insets(0,0,0,10);
        container.add(this.controlPanel, c);
    }

    /**
     * Defines gridBagLayout constraints for Viewer to be added to Frame.
     *
     * @param container
     */
    private void addViewerToFrame(Container container) {
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.gridwidth = 10;
        c.gridheight = 10;
        container.add(this.viewer, c);
    }

    /**
     * Checks in which of rectangle limits intersect happens.
     *
     * @param ball
     * @param limits Square shape object bounds.
     * @return String with the intersect.
     */
    private String checkLimits(Ball ball, Rectangle limits) {
        String str = "";
        //borde izquierdo
        if (ball.getX() + 1 == limits.getMinX()) {
            if (ball.getY() + 1 > limits.getMinY() && ball.getY() + 1 < limits.getMaxY()) {
                str = "Left";
            }
        }
        //borde derecho
        else if (ball.getX() + 1 == limits.getMaxX()) {
            if (ball.getY() + 1 > limits.getMinY() && ball.getY() + 1 < limits.getMaxY()) {
                str = "Right";
            }
        }
        //borde superior
        else if (ball.getY() + 1 == limits.getMinY()) {
            if (ball.getX() + 1 > limits.getMinX() && ball.getX() + 1 < limits.getMaxX()) {
                str = "V";
            }
        }
        //borde inferior
        else if (ball.getY() + 1 == limits.getMaxY()) {
            if (ball.getX() + 1 > limits.getMinX() && ball.getX() + 1 < limits.getMaxX()) {
                str = "V";
            }
        }
        //vértices
        else if ((ball.getY() + 1 == limits.getMaxY() && ball.getX() + 1 == limits.getMaxX()) ||
                (ball.getY() + 1 == limits.getMinY() && ball.getX() + 1 == limits.getMinX()) ||
                (ball.getY() + 1 == limits.getMinY() && ball.getX() + 1 == limits.getMaxX()) ||
                (ball.getY() + 1 == limits.getMaxY() && ball.getX() + 1 == limits.getMinX())) {
            str = "D";
        }
        return str;
    }

    /**
     * Manages the creation of BallTask Frame
     */
    private void createFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setBounds(150, 10, FRAME_WIDTH, FRAME_HEIGHT);
        //this.setLayout(new GridBagLayout());
        this.setLayout(new BorderLayout());
        //Container container = this.getContentPane();
        this.add(this.controlPanel, BorderLayout.SOUTH);
        this.add(this.viewer, BorderLayout.CENTER);
        /*this.addControlPaneToFrame(container);
        this.addViewerToFrame(container);
        this.pack();*/
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
            case "D":
                ball.bounceDiagonally();
                break;
        }
    }

    /**
     * Manages ball intersect with BlackHole object.
     *
     * @param ball
     * @param blackHole
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

    /**
     * Set full BlackHoles attribute to false.
     */
    private void clearOutBlackHoles() {
        for (BlackHole blackHole : this.blackHoleList) {
            blackHole.setFull(false);
        }
    }

    private void interruptThreads() {
        for (Ball ball : this.ballList) {
            ball.getBALL_THREAD().interrupt();
        }
    }

    private synchronized void stopThreads() {
        for (Ball ball : this.ballList) {
            if (!ball.getBALL_THREAD().isInterrupted()) {
                try {
                    ball.getBALL_THREAD().wait();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
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
                this.interruptThreads();
                this.ballList.clear();
                this.killThreads();
                this.stadistics.eraseBalls();
                this.clearOutBlackHoles();
                break;
            case "Resume":
                this.controlPanel.enableButton(str);
                notifyAll();
                break;
            case "Stop":
                this.controlPanel.enableButton(str);
                this.stopThreads();
            case "Right side":
                this.controlPanel.changeBoxState(str);
                break;
            case "Left side":
                this.controlPanel.changeBoxState(str);
                break;
            default:
                System.out.println("Not Handled ActionListener in " + e);
                break;
        }
    }
}