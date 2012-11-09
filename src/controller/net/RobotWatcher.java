package controller.net;

import controller.EventHandler;
import controller.action.ActionBoard;
import data.GameControlReturnData;
import data.PlayerInfo;
import data.Rules;

/**
 * @author: Marcel Steinbeck, Michel Bartsch
 * 
 * You can ask this class about the robots online-status.
 * 
 * This class is a sigleton!
 */
public class RobotWatcher
{
    /** The instance of the singleton. */
    private static RobotWatcher instance = new RobotWatcher();

    /** A timestamp when the last reply from each robot was received. */
    private long [][] robotsLastAnswer = new long[2][Rules.TEAM_SIZE];
    /** Last message reeived from each robot.
     *  Look at GameControlReturnData for information about messages */
    private int [][] robotsLastMessage = new int[2][Rules.TEAM_SIZE];
    /** The calculated information about the online-status. */
    private RobotOnlineStatus [][] status = new RobotOnlineStatus[2][Rules.TEAM_SIZE];

    /** What the constants name says. */
    private final static int MILLIS_UNTILL_ROBOTER_IS_OFFLINE = 4*1000;
    private final static int MILLIS_UNTILL_ROBOTER_HAS_HIGH_LATANCY = 2*1000;

    /**
     * Creates a new RobotWatcher.
     */
    private RobotWatcher()
    {
        for(int i  = 0; i < 2; i++) {
            for (int j = 0; j < Rules.TEAM_SIZE; j++) {
                robotsLastMessage[i][j] = PlayerInfo.PENALTY_NONE;
                status[i][j] = RobotOnlineStatus.UNKNOWN;
            }
        }
    }
    
    /**
     * Recieves robot´s answers to update corresponding timestamps and fire
     * actions caused manual on the robot.
     * 
     * @param gameControlReturnData     The robot`s answer.
     */
    public static synchronized void update(GameControlReturnData gameControlReturnData)
    {
        int team = EventHandler.getInstance().data.team[0].teamNumber == gameControlReturnData.team ? 0 : 1;
        instance.robotsLastAnswer[team][gameControlReturnData.player-1] = System.currentTimeMillis();
        if(instance.robotsLastMessage[team][gameControlReturnData.player-1] != gameControlReturnData.message) {
            instance.robotsLastMessage[team][gameControlReturnData.player-1] = gameControlReturnData.message;
            if(gameControlReturnData.message == GameControlReturnData.GAMECONTROLLER_RETURN_MSG_MAN_PENALISE) {
                ActionBoard.manualPen[team][gameControlReturnData.player-1].actionPerformed(null);
            } else if(gameControlReturnData.message == GameControlReturnData.GAMECONTROLLER_RETURN_MSG_MAN_UNPENALISE) {
                ActionBoard.manualUnpen[team][gameControlReturnData.player-1].actionPerformed(null);
            }
        }
    }

    /**
     * Calculates new online-status for each robot.
     * 
     * @return The updated online-status of each robot.
     */
    public static synchronized RobotOnlineStatus[][] updateRobotOnlineStatus()
    {
        long currentTime = System.currentTimeMillis();
        int robotsOffline;
        for(int i=0; i<2; i++) {
            robotsOffline = 0;
            for(int j=0; j < instance.status[i].length; j++) {
                if(currentTime - instance.robotsLastAnswer[i][j] > MILLIS_UNTILL_ROBOTER_IS_OFFLINE) {
                    instance.status[i][j] = RobotOnlineStatus.OFFLINE;
                    if(++robotsOffline >= Rules.TEAM_SIZE) {
                        for(int k=0; k < Rules.TEAM_SIZE; k++) {
                            instance.status[i][k] = RobotOnlineStatus.UNKNOWN;
                        }
                    }
                } else if(currentTime - instance.robotsLastAnswer[i][j] > MILLIS_UNTILL_ROBOTER_HAS_HIGH_LATANCY) {
                    instance.status[i][j] = RobotOnlineStatus.HIGH_LATENCY;
                } else {
                    instance.status[i][j] = RobotOnlineStatus.ONLINE;
                }
            }
        }
        return instance.status;
    }
}