package controller.action.ui;

import controller.action.GCAction;
import controller.Log;
import controller.action.ActionBoard;
import controller.action.ActionType;
import data.AdvancedData;
import data.GameControlData;
import data.Rules;


/**
 * @author: Michel Bartsch
 * 
 * This action means that the ball is out.
 */
public class Out extends GCAction
{
    /** On which side (0:left, 1:right) */
    private int side;
    
    
    /**
     * Creates a new Out action.
     * Look at the ActionBoard before using this.
     * 
     * @param side      On which side (0:left, 1:right)
     */
    public Out(int side)
    {
        super(ActionType.UI);
        this.side = side;
    }

    /**
     * Performs this action to manipulate the data (model).
     * 
     * @param data      The current data to work on.
     */
    @Override
    public void perform(AdvancedData data)
    {
        ActionBoard.clock.newDropInTime(data);
        data.dropInTeam = data.team[side].teamColor;
        Log.state(data, "Out by "+Rules.TEAM_COLOR_NAME[data.team[side].teamColor]);
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
        return (data.gameState == GameControlData.STATE_PLAYING) || (data.testmode);
    }
}