package controller.action.ui.penalty;

import controller.EventHandler;
import controller.action.GCAction;
import controller.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import data.AdvancedData;
import data.GameControlData;
import data.PlayerInfo;
import data.Rules;


/**
 * @author: Michel Bartsch
 * 
 * This action means that the player pushing penalty has been selected.
 */
public class Pushing extends GCAction
{
    /** A random constant that flags the penalty time to ejected */
    public static final int BANN_TIME = Short.MAX_VALUE;
    
    
    /**
     * Creates a new Pushing action.
     * Look at the ActionBoard before using this.
     */
    public Pushing()
    {
        super(ActionType.UI);
        penalty = PlayerInfo.PENALTY_SPL_PLAYER_PUSHING;
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        if(EventHandler.getInstance().lastUIEvent == this) {
            EventHandler.getInstance().noLastUIEvent = true;
        }
    }
    
    /**
     * Performs this action`s penalty on a selected player.
     * 
     * @param data      The current data to work on.
     * @param player    The player to penalise.
     * @param side      The side the player is playing on (0:left, 1:right).
     * @param number    The player`s number, beginning with 0!
     */
    @Override
    public void performOn(AdvancedData data, PlayerInfo player, int side, int number)
    {
        player.penalty = penalty;
        if(data.gameState != GameControlData.STATE_READY) {
            data.pushes[side]++;
        }
        
        boolean rejected = false;
        for(int i=0; i<Rules.PUSHES_TO_REJECTION.length; i++) {
            if(data.pushes[side] == Rules.PUSHES_TO_REJECTION[i]) {
                rejected = true;
                break;
            }
        }
        if(data.gameState != GameControlData.STATE_READY) {
            if(!rejected) {
                ActionBoard.clock.setPlayerPenTime(data, side, number, Rules.PENALTY_STANDARD_TIME);
            } else {
                ActionBoard.clock.setPlayerPenTime(data, side, number, BANN_TIME);
            }
        } else {
            ActionBoard.clock.setPlayerPenTime(data, side, number, (int)Math.min(Rules.PENALTY_STANDARD_TIME, data.remainingReady/1000));
        }
        Log.state(data, "Player Pushing "+
                    Rules.TEAM_COLOR_NAME[data.team[side].teamColor]
                    + " " + (number+1));
    }
    
    /**
     * Checks if this action is legal with the given data (model).
     * Illegal actions are not performed by the EventHandler.
     * 
     * @param data      The current data to check with.
     */
    @Override
    public boolean isLegal(AdvancedData data)
    {
        return (data.gameState == GameControlData.STATE_READY)
            || (data.gameState == GameControlData.STATE_SET) 
            || (data.gameState == GameControlData.STATE_PLAYING) 
            || (data.testmode);
    }
}