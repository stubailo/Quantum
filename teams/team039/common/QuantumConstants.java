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
	
	
	/** Planck's constant */
	public static final double H = 6.626069E-34;
	/** Reduced Planck's constant */
	public static final double H_BAR = 1.0545716E-34;
	
	private QuantumConstants() {	
	}
}
