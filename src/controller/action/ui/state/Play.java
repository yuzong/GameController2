package controller.action.ui.state;

import controller.action.GCAction;
import controller.Log;
import controller.action.ActionType;
import data.AdvancedData;
import data.GameControlData;
import data.Rules;


/**
 * @author: Michel Bartsch
 * 
 * This action means that the state is to be set to play.
 */
public class Play extends GCAction
{
    /**
     * Creates a new Play action.
     * Look at the ActionBoard before using this.
     */
    public Play()
    {
        super(ActionType.UI);
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        if(data.gameState == GameControlData.STATE_PLAYING) {
            return;
        }
        data.gameState = GameControlData.STATE_PLAYING;
        if(data.secGameState != GameControlData.STATE2_PENALTYSHOOT) {
            data.remainingKickoffBlocked = Rules.KICKOFF_TIME*1000;
        } else {
            data.remainingKickoffBlocked = -10*1000;
            data.penaltyShoot[data.kickOffTeam == data.team[0].teamColor ? 0 : 1]++;
        }
        Log.state(data, "State set to Playing");
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
        return (data.gameState == GameControlData.STATE_SET)
            || (data.gameState == GameControlData.STATE_PLAYING)
            || data.testmode;
    }
}