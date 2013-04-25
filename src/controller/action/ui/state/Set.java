package controller.action.ui.state;

import controller.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import controller.action.GCAction;
import data.AdvancedData;
import data.GameControlData;
import data.Rules;


/**
 * @author: Michel Bartsch
 * 
 * This action means that the state is to be set to set.
 */
public class Set extends GCAction
{
    /**
     * Creates a new Set action.
     * Look at the ActionBoard before using this.
     */
    public Set()
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
        if(data.gameState == GameControlData.STATE_SET) {
            return;
        }
        data.gameState = GameControlData.STATE_SET;
        ActionBoard.clock.resetPlayerPenTime(data);
        if(data.secGameState == GameControlData.STATE2_PENALTYSHOOT) {
            if(data.penaltyShoot[data.kickOffTeam == data.team[0].teamColor ? 0 : 1]
                    >= (!data.fulltime ? Rules.NUMBER_OF_PENALTY_SHOOTS_SHORT : Rules.NUMBER_OF_PENALTY_SHOOTS_LONG))
            {
                data.penaltyShootTime = Rules.PENALTY_SHOOT_TIME_SUDDEN_DEATH*1000;
            } else {
              data.penaltyShootTime = Rules.PENALTY_SHOOT_TIME*1000;  
            }
        }
        Log.state(data, "State set to Set");
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
            || ( (data.secGameState == GameControlData.STATE2_PENALTYSHOOT)
              && (data.gameState != GameControlData.STATE_PLAYING)
              && !data.timeOutActive[0]
              && !data.timeOutActive[1] )
            || data.testmode;
    }
}