package controller.ui;

import data.Teams;
import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * @author: Michel Bartsch
 * 
 * This is only to be on starting the programm to get starting input.
 */
public class StartInput extends JFrame implements Serializable
{
    private static Pattern IPV4_PATTERN;

    static {
        IPV4_PATTERN = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    }

    /**
     * Some constants defining this GUI`s appearance as their names say.
     * Feel free to change them and see what happens.
     */
    private static final String WINDOW_TITLE = "GameController2";
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 480;
    private static final int STANDARD_SPACE = 10;
    private static final int TEAMS_HEIGHT = 300;
    private static final int IMAGE_SIZE = 250;
    private static final int OPTIONS_CONTAINER_HEIGHT = 80;
    private static final int OPTIONS_HEIGHT = 30;
    /** This is not what the name says ;) */
    private static final int FULLSCREEN_WIDTH = 100;
    private static final int BROADCAST_WIDTH = 125;
    private static final int BROADCAST_HEIGHT = 25;
    private static final String[] BACKGROUND_SIDE = {"config/icons/robot_left_blue.png",
                                                        "config/icons/robot_right_red.png"};
    private static final String FULLTIME_LABEL_NO = "10 minutes playing and pause";
    private static final String FULLTIME_LABEL_YES = "Auto-Pause, full 10 minutes playing";
    private static final String FULLSCREEN_LABEL = "Fullscreen";
    private static final String START_LABEL = "Start";
    
    /** If true, this GUI has finished and offers it`s input. */
    public boolean finished = false;
    
    /**
     * This is true, if the teams chosen are legal. They are not legal, if
     * they are the same.
     */
    private boolean teamsOK = false;
    /**
     * This is true, if a time-mode has manually be set. Manually setting this
     * is recommended.
     */
    private boolean fulltimeOK = false;

    private boolean broadcastAddressOk = false;
    
    /** The inputs that can be read from this GUI when it has finished. */
    public int[] outTeam = {0, 0};
    public boolean outFulltime;
    public boolean outFullscreen;
    public String outBroadcastAddress;
    
    /** All the components of this GUI. */
    private ImagePanel[] teamContainer = new ImagePanel[2];
    private ImageIcon[] teamIcon = new ImageIcon[2];
    private JLabel[] teamIconLabel = new JLabel[2];
    private JComboBox[] team = new JComboBox[2];
    private JPanel optionsLeft;
    private JPanel optionsRight;
    private JRadioButton nofulltime;
    private JRadioButton fulltime;
    private ButtonGroup fulltimeGroup;
    private Checkbox fullscreen;
    private JLabel broadcastAddressLabel;
    private JTextField broadcastAddressInput;
    private JButton start;
    
    
    /**
     * Creates a new StartInput.
     */
    public StartInput(final String[] args)
    {
        super(WINDOW_TITLE);
        //layout
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        Dimension desktop = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((desktop.width-WINDOW_WIDTH)/2, (desktop.height-WINDOW_HEIGHT)/2);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, STANDARD_SPACE));
        
        for(int i=0; i<2; i++) {
            teamContainer[i] = new ImagePanel((new ImageIcon(BACKGROUND_SIDE[i])).getImage());
            teamContainer[i].setPreferredSize(new Dimension(WINDOW_WIDTH/2-STANDARD_SPACE, TEAMS_HEIGHT));
            teamContainer[i].setOpaque(true);
            teamContainer[i].setLayout(new BorderLayout());
            add(teamContainer[i]);
            setTeamIcon(i, 0);
            teamIconLabel[i] = new JLabel(teamIcon[i]);
            teamContainer[i].add(teamIconLabel[i], BorderLayout.CENTER);
            team[i] = new JComboBox(Teams.getNames(true));
            teamContainer[i].add(team[i], BorderLayout.SOUTH);
        }
        team[0].addActionListener(new ActionListener()
            {
            @Override
                public void actionPerformed(ActionEvent e)
                {
                    outTeam[0] = Integer.valueOf(((String)team[0].getSelectedItem()).split(": ")[0]);
                    setTeamIcon(0, outTeam[0]);
                    teamIconLabel[0].setIcon(teamIcon[0]);
                    teamIconLabel[0].repaint();
                    teamsOK = outTeam[0] != outTeam[1];
                    startEnableing();
                }
            }
        );
        team[1].addActionListener(new ActionListener()
            {
            @Override
                public void actionPerformed(ActionEvent e)
                {
                    outTeam[1] = Integer.valueOf(((String)team[1].getSelectedItem()).split(": ")[0]);
                    setTeamIcon(1, outTeam[1]);
                    teamIconLabel[1].setIcon(teamIcon[1]);
                    teamIconLabel[1].repaint();
                    teamsOK = outTeam[1] != outTeam[0];
                    startEnableing();
                }
            }
        );

        optionsRight = new JPanel();
        optionsRight.setPreferredSize(new Dimension(WINDOW_WIDTH/2-2*STANDARD_SPACE, OPTIONS_CONTAINER_HEIGHT));
        optionsRight.setLayout(new BoxLayout(optionsRight, BoxLayout.PAGE_AXIS));
        add(optionsRight);

        JPanel fullscreenPanel = new JPanel();
        fullscreenPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        optionsRight.add(fullscreenPanel);
        JPanel broadcastAddressPanel = new JPanel();
        broadcastAddressPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        optionsRight.add(broadcastAddressPanel);

        fullscreen = new Checkbox(FULLSCREEN_LABEL);
        fullscreen.setPreferredSize(new Dimension(FULLSCREEN_WIDTH, OPTIONS_HEIGHT));
        fullscreen.setState(true);
        fullscreenPanel.add(fullscreen);
        broadcastAddressLabel = new JLabel("Broadcast:");
        broadcastAddressPanel.add(broadcastAddressLabel);
        broadcastAddressInput = new JTextField();
        broadcastAddressInput.setPreferredSize(new Dimension(BROADCAST_WIDTH, BROADCAST_HEIGHT));
        broadcastAddressInput.setText(getDefaultBroadcastAddress(args));
        broadcastAddressOk = true; // setText does not trigger change event...
        broadcastAddressInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateBroadcast();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateBroadcast();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateBroadcast();
            }

            private void validateBroadcast() {
                broadcastAddressOk = IPV4_PATTERN.matcher(broadcastAddressInput.getText()).matches();
                startEnableing();
            }
        });
        broadcastAddressPanel.add(broadcastAddressInput);

        optionsLeft = new JPanel();
        optionsLeft.setPreferredSize(new Dimension(WINDOW_WIDTH/2-2*STANDARD_SPACE, OPTIONS_CONTAINER_HEIGHT));
        add(optionsLeft);
        Dimension optionsDim = new Dimension(WINDOW_WIDTH/2-2*STANDARD_SPACE, OPTIONS_HEIGHT);
        nofulltime = new JRadioButton(FULLTIME_LABEL_NO);
        nofulltime.setPreferredSize(optionsDim);
        fulltime = new JRadioButton(FULLTIME_LABEL_YES);
        fulltime.setPreferredSize(optionsDim);
        fulltimeGroup = new ButtonGroup();
        fulltimeGroup.add(nofulltime);
        fulltimeGroup.add(fulltime);
        optionsLeft.add(nofulltime);
        optionsLeft.add(fulltime);
        nofulltime.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    outFulltime = false;
                    fulltimeOK = true;
                    startEnableing();
                }});
        fulltime.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    outFulltime = true;
                    fulltimeOK = true;
                    startEnableing();
                }});
        
        start = new JButton(START_LABEL);
        start.setPreferredSize(optionsDim);
        start.setEnabled(false);
        add(start);
        start.addActionListener(new ActionListener() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    outFullscreen = fullscreen.getState();
                    outBroadcastAddress = broadcastAddressInput.getText();
                    finished = true;
                }});
                
        setVisible(true);
    }

    private String getDefaultBroadcastAddress(final String[] args) {
        String defaultBroadcastAddress = "255.255.255.255";
        if (args.length >= 2 && (args[0].equals("-b") || args[0].equals("--broadcast"))) {
            defaultBroadcastAddress = args[1];
        }
        return defaultBroadcastAddress;
    }
    
    /**
     * Sets the Team-Icon on the GUI.
     * 
     * @param side      The side (0=left, 1=right)
     * @param team      The number of the Team
     */ 
    private void setTeamIcon(int side, int team)
    {
        teamIcon[side] = new ImageIcon(Teams.getIcon(team));
            float scale_factor;
            if(teamIcon[side].getImage().getWidth(null) > teamIcon[side].getImage().getHeight(null)) {
                scale_factor = (float)IMAGE_SIZE/teamIcon[side].getImage().getWidth(null);
            } else {
                scale_factor = (float)IMAGE_SIZE/teamIcon[side].getImage().getHeight(null);
            }
            teamIcon[side].setImage(teamIcon[side].getImage().getScaledInstance(
                    (int)(teamIcon[side].getImage().getWidth(null)*scale_factor),
                    (int)(teamIcon[side].getImage().getHeight(null)*scale_factor),
                    Image.SCALE_DEFAULT));
    }
    
    /**
     * Enables the start button, if the conditions are ok.
     */
    private void startEnableing()
    {
        start.setEnabled(teamsOK && fulltimeOK && broadcastAddressOk);
    }
    
    /**
     * @author: Michel Bartsch
     * 
     * This is a normal JPanel, but it has a background image.
     */
    class ImagePanel extends JPanel
    {
        /** The image that is shown in the background. */
        private Image image;

        /**
         * Creates a new ImagePanel.
         * 
         * @param image     The Image to be shown in the background.
         */
        public ImagePanel(Image image)
        {
            this.image = image;
        }
        
        /**
         * Changes the background image.
         * 
         * @param image     Changes the image to this one.
         */
        public void setImage(Image image)
        {
            this.image = image;
        }
        
        /**
         * Paints this Component, should be called automatically.
         * 
         * @param g     This components graphical content.
         */
        @Override
        public void paintComponent(Graphics g)
        {
            if(super.isOpaque()) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            g.drawImage(image, (getWidth()-image.getWidth(null))/2, 0, image.getWidth(null), image.getHeight(null), null);
        }
    }
}