package org.roach.intelligentagents;

import org.roach.intelligentagents.model.strategy.AgentStrategy;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * @author Larry S. Roach
 *
 */
@Parameters
public class AgentAppOpts {

	/**
	 * width/height of the grid
	 */
	@Parameter(names = { "--roomsize" })
	public int roomsize = 50;

	/**
	 * Number of agents in the grid
	 */
	@Parameter(names = { "--agents" })
	public int agents = 15;

	/**
	 * Number of tasks in the grid
	 */
	@Parameter(names = { "--tasks" })
	public int tasks = 100;

	/**
	 * Percentage of tasks to be complete before stopping
	 */
	@Parameter(names = { "--stopat" })
	public int stopat = 100;

	/**
	 * The strategy to use
	 */
	@Parameter(names = { "--strategy" }, converter=ClassConverter.class)
	public Class<? extends AgentStrategy> strategy;

	/**
	 * Whether to run in batch mode or not
	 */
	@Parameter(names = {"--batch"})
	public boolean batch = false;
	
	/**
	 * communications distance
	 */
	@Parameter(names = {"--commDist"})
	public int commDist = 12;
	
	/**
	 * communications time
	 */
	@Parameter(names = {"--commTime"})
	public int commTime = 3;
	
	/**
	 * 
	 */
	@Parameter(names= {"--showHelper"})
	public boolean showHelper = true;

	/**
	 * 
	 */
	@Parameter(names= {"--showGraphics"})
	public boolean showGraphics = true;
	
	/**
	 * 
	 */
	@Parameter(names= {"--cellSize"})
	public int cellSize = 6;
	
	/**
	 * Used only by NeighborhoodStrategy
	 */
	@Parameter(names= {"--neighbors"})
	public int neighbors = 3;
	
	/**
	 * Used only by MilitaryStrategy
	 */
	@Parameter(names= {"--numSubordinates"})
	public int numSubordinates = 2;
	
	/**
	 * 
	 */
	@Parameter(names= {"--numLevels"})
	public int numLevels = 3;
}