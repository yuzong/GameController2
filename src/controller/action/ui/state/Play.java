package controller.action.ui.state;

import controller.action.GCAction;
import controller.Log;
import controller.action.ActionType;
import data.AdvancedData;
import data.GameControlData;


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
        data.gameState = GameControlData.STATE_PLAYING;
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