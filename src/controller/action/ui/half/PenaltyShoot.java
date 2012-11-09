package controller.action.ui.half;

import controller.action.GCAction;
import controller.Log;
import controller.action.ActionType;
import data.AdvancedData;
import data.GameControlData;


/**
 * @author: Michel Bartsch
 * 
 * This action means that a penalty shoot is to be starting.
 */
public class PenaltyShoot extends GCAction
{
    /**
     * Creates a new PenaltyShoot action.
     * Look at the ActionBoard before using this.
     */
    public PenaltyShoot()
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
        if(data.secGameState != GameControlData.STATE2_PENALTYSHOOT) {
            data.secGameState = GameControlData.STATE2_PENALTYSHOOT;
            data.gameState = GameControlData.STATE_INITIAL;
            Log.state(data, "Half set to PenaltyShoot");
        }
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
        return (data.secGameState == GameControlData.STATE2_PENALTYSHOOT)
          || ( (data.firstHalf != GameControlData.C_TRUE)
            && (data.gameState == GameControlData.STATE_FINISHED) )
          || (data.testmode);
    }
}