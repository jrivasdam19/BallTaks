package mainProject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ControlPanel extends JPanel implements Runnable {

    private JButton addBall, newGame, resume, stop, info;
    private JLabel neighborIp, port;
    private boolean openedLeftEdge, openedRightEdge;
    private JCheckBox leftSide, rightSide;
    private JTable statsTable;
    private ArrayList<Ball> ballList;
    private Stadistics stadistics;
    private BallTask ballTask;

    private Thread controlThread;
    private final int DELAY = 4;

    public boolean isOpenedLeftEdge() {
        return openedLeftEdge;
    }

    public boolean isOpenedRightEdge() {
        return openedRightEdge;
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

    /**
     * Updates JLabels text.
     * @param ip Current ip direction given by user.
     * @param port Current port number given by user.
     */
    public void refreshJLabelText(String ip, int port){
        this.neighborIp.setText(ip);
        this.port.setText(String.valueOf(port));
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * Adds components by defining GridBagLayout constraints.
     */
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

        c.gridx=21;
        c.gridy=0;
        c.gridwidth=2;
        c.gridheight=3;
        c.fill=GridBagConstraints.BOTH;
        this.add(this.info,c);
    }

    /**
     * Switchs selected state between checkboxes in order to avoid having both walls open.
     * @param str JCheckBox name.
     */
    public void changeBoxState(String str) {
        switch (str) {
            case "Right side":
                if(this.rightSide.isSelected()){
                    this.openedRightEdge = true;
                    this.openedLeftEdge = false;
                    this.leftSide.setSelected(false);
                }else{
                    this.openedRightEdge = false;
                }
                break;
            case "Left side":
                if(this.leftSide.isSelected()){
                    this.openedLeftEdge = true;
                    this.openedRightEdge = false;
                    this.rightSide.setSelected(false);
                }else{
                    this.openedLeftEdge = false;
                }
        }
    }

    /**
     * Manages creation of ControlPanel JButtons.
     */
    private void createButtons() {
        this.addBall = new JButton("Add New Ball");
        this.addBall.addActionListener(this.ballTask);
        this.newGame = new JButton("New Game");
        this.newGame.addActionListener(this.ballTask);
        this.resume = new JButton("Resume");
        this.resume.addActionListener(this.ballTask);
        this.resume.setEnabled(false);
        this.stop = new JButton("Stop");
        this.stop.addActionListener(this.ballTask);
        this.info=new JButton("Change IP & Port");
        this.info.addActionListener(this.ballTask);
    }

    /**
     * Manages creation of ControlPanel JCheckboxes.
     */
    private void createCheckBoxes() {
        this.leftSide = new JCheckBox("Left side");
        this.leftSide.addActionListener(this.ballTask);
        this.rightSide = new JCheckBox("Right side");
        this.rightSide.addActionListener(this.ballTask);
    }

    /**
     * Manages creation of ControlPanel JTable.
     */
    private void createJLabels() {
        this.neighborIp = new JLabel("Neighbor IP");
        this.port = new JLabel("Common Port");
    }

    /**
     * Manages creation of ControlPanel JPanel.
     */
    private void createPanel() {
        this.createTable();
        this.createCheckBoxes();
        this.createJLabels();
        this.createButtons();
        this.addComponentsToPanel();
    }

    /**
     * Manages creation of ControlPanel JPanel.
     */
    private void createTable() {
        this.statsTable = new JTable(4, 2);
        this.statsTable.setValueAt("Balls Outside", 0, 0);
        this.statsTable.setValueAt("Balls Inside", 1, 0);
        this.statsTable.setValueAt("Balls Waiting", 2, 0);
        this.statsTable.setValueAt("Total Balls", 3, 0);
    }

    /**
     * Switchs button enable state between buttons in order to prevent inconsistencies.
     * @param str JButton name.
     */
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

    /**
     * Updates JTable data.
     * @param stadistics Stadistics class instance.
     */
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