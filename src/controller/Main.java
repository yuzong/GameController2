package controller;

import controller.action.ActionBoard;
import controller.net.Receiver;
import controller.net.Sender;
import controller.ui.GCGUI;
import controller.ui.GUI;
import controller.ui.KeyboardListener;
import controller.ui.StartInput;
import data.AdvancedData;
import data.GameControlData;
import data.Teams;

import javax.swing.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author: Michel Bartsch
 * 
 * The programm starts in this class.
 * The main components are initialised here.
 */
public class Main
{
    /**
     * The version of the GameController.
     * Actually there are no dependencies, but this should be the first thing
     * to be written into the log file.
     */
    public static final String version = "GC2 0.9";
    
    /**
     * The programm starts here.
     * 
     * @param args  This is ignored.
     */
    public static void main(String[] args)
    {
        //application-lock
        final ApplicationLock applicationLock = new ApplicationLock("GameController");
        try {
            if (!applicationLock.acquire()) {
                JOptionPane.showMessageDialog(null,
                        "An instance of GameController already exists.",
                        "Multiple instances",
                        JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error while trying to acquire the application lock.",
                    "IOError",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        //collect the start parameters and put them into the first data.
        StartInput input = new StartInput();
        while(!input.finished) {
            try{
            Thread.sleep(100);
            } catch(Exception e) {}
        }

        AdvancedData data = new AdvancedData();
        for(int i=0; i<2; i++) {
            data.team[i].teamNumber = (byte)input.outTeam[i];
        }
        data.fulltime = input.outFulltime;

        try {
            //sender
            Sender sender = Sender.getInstance();
            sender.send(data);
            sender.start();

            //event-handler
            EventHandler.getInstance().data = data;

            //receiver
            Receiver receiver = Receiver.getInstance();
            receiver.start();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "Error while setting up GameController on port: " + GameControlData.GAMECONTROLLER_PORT + ".",
                    "Error on configured port",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(-1);
        }

        //log
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-S");
        Log.init("log_"+df.format(new Date(System.currentTimeMillis()))+".txt");
        Log.toFile("Fulltime = "+data.fulltime);

        //ui
        ActionBoard.init();
        Log.state(data, Teams.getNames(false)[data.team[0].teamNumber] +" vs "+Teams.getNames(false)[data.team[1].teamNumber]);
        GCGUI gui = new GUI(input.outFullscreen, data);
        KeyboardListener kui = new KeyboardListener();
        EventHandler.getInstance().setGUI(gui);
        gui.update(data);

        //input dispose
        input.dispose();

        //shutdown hook, to release resources
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                Log.toFile("Shutdown GameController");
                try {
                    applicationLock.release();
                } catch (IOException e) {
                    System.err.println("Error while trying to release the application lock.");
                }
                Sender.getInstance().interrupt();
                Receiver.getInstance().interrupt();

                try {
                    Log.close();
                } catch (IOException e) {
                    System.err.println("Error while trying to close the log.");
                }
            }
        });

        //clock
        Clock clock = new Clock();
        clock.start();
    }
}