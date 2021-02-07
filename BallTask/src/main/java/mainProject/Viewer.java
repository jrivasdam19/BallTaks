package mainProject;

import mainProject.Ball;
import mainProject.BallTask;
import mainProject.BlackHole;

import java.awt.*;
import java.util.ArrayList;

public class Viewer extends Canvas implements Runnable {

    private Thread viewerThread;
    private boolean painting;
    private ArrayList<Ball> ballList;
    private ArrayList<BlackHole> blackHoleList;
    private static final int VIEWER_WIDTH = 700;
    private static final int VIEWER_HEIGH = 700;
    private Rectangle rectangle;

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Viewer(ArrayList<BlackHole> blackHoleList, ArrayList<Ball> ballList) {
        Dimension dimension = new Dimension(VIEWER_WIDTH, VIEWER_HEIGH);
        this.setPreferredSize(dimension);
        this.setVisible(true);
        this.rectangle = new Rectangle(0,0,VIEWER_WIDTH,VIEWER_HEIGH);
        this.ballList = ballList;
        this.blackHoleList = blackHoleList;
        this.viewerThread = new Thread(this);
        this.painting = true;
        this.viewerThread.start();
    }

    //------------------------------------------------------------------------------------------------------------------

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < this.ballList.size(); i++) this.ballList.get(i).paint(g2);
        for (int i = 0; i < this.blackHoleList.size(); i++) this.blackHoleList.get(i).paint(g2);
        //for (Ball ball : this.ballList) ball.paint(g2);
        //for (BlackHole blackHole : this.blackHoleList) blackHole.paint(g2);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        while (this.painting) {
            //Esto hace que se vuelva a pintar el fondo del mainProject.mainProject.Viewer.
            this.repaint();
            try {
                //Este sleep lo ponemos porque sino no se vería la bola de lo rápido que se pinta. Por tanto, el sleep,
                //tiene que ponerse tanto en este hilo como en el que se encarga del movimiento de la bola.
                this.viewerThread.sleep(BallTask.DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}