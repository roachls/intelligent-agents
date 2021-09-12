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

//	/**
//	 * communications distance
//	 */
//	@Parameter(names = {"--commDist"}, description="Communications distance")
//	public int commDist = 12;

    /**
     * communications time
     */
    @Parameter(names = {
	    "--commTime" }, description = "Number of ticks an agent broadcasts before turning the receiver off")
    public int commTime = 3;

    /**
     * 
     */
    @Parameter(names = { "--showHelper" }, description = "Show helper graphics (comm distance, etc.)")
    public boolean showHelper = true;

    /**
     * 
     */
    @Parameter(names = { "--showGraphics" }, description = "Render graphics")
    public boolean showGraphics = true;

    /**
     * 
     */
    @Parameter(names = { "--cellSize" }, description = "Size of a cell in pixels")
    public int cellSize = 6;

    @Override
    public String toString() {
	return "AgentAppOpts [commTime=" + commTime + ", showHelper=" + showHelper + ", showGraphics=" + showGraphics
		+ ", cellSize=" + cellSize + "]";
    }
}

class ClassConverter implements IStringConverter<Class<? extends AgentStrategy>> {
    @SuppressWarnings({ "unchecked" })
    @Override
    public Class<? extends AgentStrategy> convert(@Nullable String value) {
	try {
	    return (Class<? extends AgentStrategy>) Class.forName(value);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace(); // NOPMD by Family on 11/26/19, 2:59 PM
	    return AgentStrategy.class;
	}
    }
}
