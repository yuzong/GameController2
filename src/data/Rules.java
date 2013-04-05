package data;

import java.awt.Color;


/**
 * @author: Michel Bartsch
 * 
 * This class holds constants given by the spl rules.
 */
public class Rules
{
    /** How many robots are in a team. */
    public static final int TEAM_SIZE = 5;
    /** The Java Colors the left and the right team starts with. */
    public static final Color[] TEAM_COLOR = {Color.BLUE, Color.RED};
    /** The name of the colors. */
    public static final String[] TEAM_COLOR_NAME = {"Blue", "Red"};
    /** Time in seconds one half is long. */
    public static final int HALF_TIME = 10*60;
    /** Time in seconds the ready state is long. */
    public static final int READY_TIME = 45;
    /** Time in seconds between first and second half. */
    public static final int PAUSE_TIME = 10*60;
    /** Time in seconds the ball is blocked after kickoff. */
    public static final int KICKOFF_TIME = 10;
    /** Time in seconds between second half and penalty shoot. */
    public static final int PAUSE_PENALTY_SHOOT_TIME = 5*60;
    /** Time in seconds one penalty shoot is long. */
    public static final int PENALTY_SHOOT_TIME = 1*60;
    /** Time in seconds normal penalties take. */
    public static final int PENALTY_STANDARD_TIME = 30;
    /** Time in seconds a robot is taken out when manually penalized (ChestButton). */
    public static final int PENALTY_MANUAL_TIME = 1;
    /** Time in seconds one team has as timeOut. */
    public static final int TIME_OUT_TIME = 5*60;
    /** How many times a team may take a timeOut. */
    public static final int TIME_OUT_MAX_NUMBER = 1;
    /** On how many pushings is a robot ejected. */
    public static final int[] PUSHES_TO_REJECTION = {4, 6, 8};
}