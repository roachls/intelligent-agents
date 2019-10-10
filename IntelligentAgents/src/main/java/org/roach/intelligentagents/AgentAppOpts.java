package org.roach.intelligentagents;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.model.strategy.AgentStrategy;

import com.beust.jcommander.IStringConverter;
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
	@Parameter(names = { "--roomsize" }, description="Size of the room in gridcells")
	public int roomsize = 50;

	/**
	 * Number of agents in the grid
	 */
	@Parameter(names = { "--agents" }, description="Number of agents in the grid")
	public int agents = 15;

	/**
	 * Number of tasks in the grid
	 */
	@Parameter(names = { "--tasks" }, description="Number of tasks in the grid")
	public int tasks = 100;

	/**
	 * Percentage of tasks to be complete before stopping
	 */
	@Parameter(names = { "--stopat" }, description="Percentage of tasks to be complete before stopping")
	public int stopat = 100;

	/**
	 * The strategy to use
	 */
	@Parameter(names = { "--strategy" }, converter=ClassConverter.class, description="The strategy to use")
	@NonNull public Class<? extends AgentStrategy> strategy = AgentStrategy.class;

	/**
	 * Whether to run in batch mode or not
	 */
	@Parameter(names = {"--batch"}, description="Enable batch mode")
	public boolean batch = false;
	
	/**
	 * communications distance
	 */
	@Parameter(names = {"--commDist"}, description="Communications distance")
	public int commDist = 12;
	
	/**
	 * communications time
	 */
	@Parameter(names = {"--commTime"}, description="Number of ticks an agent broadcasts before turning the receiver off")
	public int commTime = 3;
	
	/**
	 * 
	 */
	@Parameter(names= {"--showHelper"}, description="Show helper graphics (comm distance, etc.)")
	public boolean showHelper = true;

	/**
	 * 
	 */
	@Parameter(names= {"--showGraphics"}, description="Render graphics")
	public boolean showGraphics = true;
	
	/**
	 * 
	 */
	@Parameter(names= {"--cellSize"}, description="Size of a cell in pixels")
	public int cellSize = 6;
	
	/**
	 * Used only by NeighborhoodStrategy
	 */
	@Parameter(names= {"--neighbors"})
	public int neighbors = 3;
	
	/**
	 * Used only by MilitaryStrategy
	 */
	@Parameter(names= {"--numSubordinates"}, description="Max number of subordinates (military strategy only)")
	public int numSubordinates = 2;
	
	/**
	 * 
	 */
	@Parameter(names= {"--numLevels"}, description="Number of levels deep a military agent should communicate with subordinates")
	public int numLevels = 3;
}

class ClassConverter implements IStringConverter<Class<? extends AgentStrategy>> {
	@SuppressWarnings({ "unchecked" })
	@Override
	public Class<? extends AgentStrategy> convert(@Nullable String value) {
		try {
			return (Class<? extends AgentStrategy>) Class.forName(value);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return AgentStrategy.class;
		}
	}
}
