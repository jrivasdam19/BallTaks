package mainProject;

import java.awt.*;
import java.util.ArrayList;

public class Viewer extends Canvas implements Runnable {

    private Thread viewerThread;
    private boolean painting;
    private ArrayList<Ball> ballList;
    private ArrayList<BlackHole> blackHoleList;
    private static final int VIEWER_WIDTH = 700;
    private static final int VIEWER_HEIGH = 700;

    public Viewer(ArrayList<BlackHole> blackHoleList, ArrayList<Ball> ballList) {
        Dimension dimension = new Dimension(VIEWER_WIDTH, VIEWER_HEIGH);
        this.setPreferredSize(dimension);
        this.setVisible(true);
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
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        while (this.painting) {
            this.repaint();
            try {
                this.viewerThread.sleep(BallTask.DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}