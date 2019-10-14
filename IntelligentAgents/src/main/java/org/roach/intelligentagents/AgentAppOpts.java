package org.roach.intelligentagents;

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
	@Nullable public Class<? extends AgentStrategy> strategy = null;

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

	@Override
	public String toString() {
		return "AgentAppOpts [roomsize=" + roomsize + ", agents=" + agents + ", tasks=" + tasks + ", stopat=" + stopat
				+ ", strategy=" + strategy + ", batch=" + batch + ", commDist=" + commDist + ", commTime=" + commTime
				+ ", showHelper=" + showHelper + ", showGraphics=" + showGraphics + ", cellSize=" + cellSize + "]";
	}
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
