package team039.common;

/**
 * Class for keeping adjustable constants for gameplay.  
 */
import battlecode.common.GameConstants;

public final class QuantumConstants {
    
	/** Total flux needed to explore for more mines */
	public static final int TOTAL_FLUX_EXPLORE = 120;
	/** Number of locations to remember for bug navigation */
	public static final int BUG_MEMORY_LENGTH = 15;
	/** Bug distance increase before trying different path */
	public static final double BUG_DISTANCE_SWITCH = 1.2;
	
	private QuantumConstants() {	
	}
}
