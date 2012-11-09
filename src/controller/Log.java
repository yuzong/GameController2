package controller;

import data.AdvancedData;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;


/**
 * @author: Michel Bartsch
 * 
 * This class should be used to log into a log file. A new file will be created
 * every time the GameController is started.
 * At the end of an actions the Log should be used to add a state into the
 * timeline, that is provided by this class too.
 * 
 * This class is a singleton!
 */
public class Log
{
    /** The instance of the singleton. */
    private static Log instance;
    
    /** The file to write into. */
    private FileWriter file;
    /** The format of timestamps. */
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy.M.dd-kk.mm.ss");
    /** The timeline. */
    private LinkedList<AdvancedData> states = new LinkedList<AdvancedData>();
    
    
    /**
     * Creates a new Log.
     */
    private Log() {}
    
    /**
     * Must be called once at the very beginning to allow Log to work.
     * 
     * @param path  The path where the log-file should be created.
     */
    public synchronized static void init(String path)
    {
        instance = new Log();
        try{
            instance.file = new FileWriter(new File(path));
        } catch(IOException e) {
            String error = "\ncannot write to logfile "+path;
            System.out.println(error);
        }
        toFile(Main.version);
    }
    
    /**
     * Simply writes a line, beginning with a timestamp, in the file.
     * May be used to log something that should not be in the timeline.
     * 
     * @param s     The string to be written in the file.
     */
    public static void toFile(String s)
    {
        try{
            instance.file.write(instance.timestampFormat.format(new Date(System.currentTimeMillis()))+": "+s+"\n");
            instance.file.flush();
        } catch(IOException e) {}
    }
    
    /**
     * Puts a copy of the given data into the timeline, attaching the message
     * to it and writing it to the file using toFile method.
     * This should be used at the very end of all actions that are meant to be
     * in the timeline.
     * 
     * @param data  The current data that have just been changed and should
     *              go into the timeline.
     * @param message   A message describing what happened to the data.
     */
    public static void state(AdvancedData data, String message)
    {
        AdvancedData state = new AdvancedData(data);
        state.message = message;
        instance.states.add(state);
        toFile(message);
    }
    
    /**
     * Changes the data used in all actions via the EventHandler to a data from
     * the timeline. So this is the undo function.
     * 
     * @param states    How far you want to go back, how many states.
     * 
     * @return The message that was attached to the data you went back to.
     */
    public static String goBack(int states)
    {
        if(states >= instance.states.size()) {
            states = instance.states.size()-1;
        }
        for(int i=0; i<states; i++) {
            instance.states.removeLast();
        }
        AdvancedData state = new AdvancedData(instance.states.getLast());
        state.copyTime(EventHandler.getInstance().data);
        EventHandler.getInstance().data = state;
        return state.message;
    }
    
    /**
     * Gives you the messages attached to the latest data in the timeline.
     * 
     * @param states    Of how many datas back you want to have the messages.
     * 
     * @return The messages attached to the data, beginning with the latest.
     *         The arrays length equals the states parameter.
     */
    public static String[] getLast(int states)
    {
        String[] out = new String[states];
        for(int i=0; i<states; i++) {
            if(instance.states.size()-1-i >= 0) {
                out[i] = instance.states.get(instance.states.size()-1-i).message;
            } else {
                out[i] = "";
            }
        }
        return out;
    }

    /**
     * Closes the Log
     *
     * @throws IOException if an error occurred while trying to close the FileWriter
     */
    public static void close() throws IOException {
        instance.file.close();
    }
}