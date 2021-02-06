package mainProject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ControlPanel extends JPanel implements Runnable {

    private JButton addBall, newGame, resume, stop;
    private JLabel neighborIp;
    private JTextField port;
    private boolean openedLeftEdge, openedRightEdge;
    private JCheckBox leftSide, rightSide;
    private JTable statsTable;
    private ArrayList<Ball> ballList;
    private Stadistics stadistics;
    private BallTask ballTask;

    public JLabel getNeighborIp() {
        return neighborIp;
    }

    private Thread controlThread;
    private final int DELAY = 4;

    public boolean isOpenedLeftEdge() {
        return openedLeftEdge;
    }

    public void setOpenedLeftEdge(boolean openedLeftEdge) {
        this.openedLeftEdge = openedLeftEdge;
    }

    public boolean isOpenedRightEdge() {
        return openedRightEdge;
    }

    public void setOpenedRightEdge(boolean openedRightEdge) {
        this.openedRightEdge = openedRightEdge;
    }

    public JTextField getPort() {
        return port;
    }

    public ControlPanel(ArrayList<Ball> ballList, Stadistics stadistics, BallTask ballTask) {
        this.openedLeftEdge = false;
        this.openedRightEdge = false;
        this.ballList = ballList;
        this.stadistics = stadistics;
        this.ballTask = ballTask;
        this.controlThread = new Thread(this);
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.orange);
        this.createPanel();
        this.controlThread.start();
    }
    //------------------------------------------------------------------------------------------------------------------

    //------------------------------------------------------------------------------------------------------------------

    private void addComponentsToPanel() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 7;
        c.gridheight = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 5, 10, 5);
        this.add(this.statsTable, c);

        c.gridx = 7;
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridheight = 1;
        this.add(this.addBall, c);

        c.gridy = 1;
        this.add(this.newGame, c);

        c.gridx = 10;
        c.gridy = 0;
        this.add(this.resume, c);

        c.gridy = 1;
        this.add(this.stop, c);

        c.gridx = 13;
        c.gridy = 0;
        c.gridwidth = 6;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(this.neighborIp, c);

        c.gridy = 1;
        this.add(this.port, c);

        c.gridx = 19;
        c.gridy = 0;
        c.gridwidth = 2;
        this.add(this.leftSide, c);

        c.gridy = 1;
        this.add(this.rightSide, c);
    }

    private void createButtons() {
        this.addBall = new JButton("Add New Ball");
        this.addBall.addActionListener(this.ballTask);
        this.newGame = new JButton("New Game");
        this.newGame.addActionListener(this.ballTask);
        this.resume = new JButton("Resume");
        this.resume.addActionListener(this.ballTask);
        this.stop = new JButton("Stop");
        this.stop.addActionListener(this.ballTask);
    }

    private void createCheckBoxes() {
        this.leftSide = new JCheckBox("Left side");
        this.leftSide.addActionListener(this.ballTask);
        this.rightSide = new JCheckBox("Right side");
        this.rightSide.addActionListener(this.ballTask);
    }

    private void createTextFields() {
        this.neighborIp = new JLabel("Add your neighbor IP here!");
        this.port = new JTextField("Add your rendezvous port here!");
    }

    public void changeBoxState(String str) {
        switch (str) {
            case "Right side":
                this.openedRightEdge = true;
                this.openedLeftEdge = false;
                this.leftSide.setSelected(false);
                break;
            case "Left side":
                this.openedLeftEdge = true;
                this.openedRightEdge = false;
                this.rightSide.setSelected(false);
        }
    }

    public void enableButton(String str) {
        switch (str) {
            case "Resume":
                this.resume.setEnabled(false);
                this.stop.setEnabled(true);
                break;
            case "Stop":
                this.resume.setEnabled(true);
                this.stop.setEnabled(false);
                break;
        }
    }

    private void createPanel() {
        this.createTable();
        this.createCheckBoxes();
        this.createTextFields();
        this.createButtons();
        this.addComponentsToPanel();
    }

    private void createTable() {
        this.statsTable = new JTable(4, 2);
        this.statsTable.setValueAt("Balls Outside", 0, 0);
        this.statsTable.setValueAt("Balls Inside", 1, 0);
        this.statsTable.setValueAt("Balls Waiting", 2, 0);
        this.statsTable.setValueAt("Total Balls", 3, 0);
    }

    private void refreshJTable(Stadistics stadistics) {
        this.statsTable.setValueAt(stadistics.getBallsOutside(), 0, 1);
        this.statsTable.setValueAt(stadistics.getBallsInside(), 1, 1);
        this.statsTable.setValueAt(stadistics.getBallsWaiting(), 2, 1);
        this.statsTable.setValueAt(stadistics.getTotalBalls(), 3, 1);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void run() {
        while (true) {
            this.refreshJTable(this.stadistics);
            try {
                this.controlThread.sleep(this.DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}