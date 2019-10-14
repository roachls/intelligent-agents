package org.roach.intelligentagents.model.strategy;

import java.awt.Color;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.model.State;
import org.roach.intelligentagents.model.TaskToDo;


/**
 * The Search agent is the "base case" for comparison with all other agents.
 * It simply searches the entire grid in a sweeping fashion. Assuming there
 * are enough agents to overcome the random task-execution factor, the Search
 * agent should complete all tasks in gridsize^2/2 cycles. The number of Search
 * agents used is irrelevant - 1000 agents will take exactly the same amount of
 * time as 100.
 * @author L. Stephen Roach
 * @version 1.0
 */
public class SearchStrategy extends AgentStrategy {

	enum Directions {
        NORTHEAST, NORTHWEST, SOUTHEAST, SOUTHWEST, EMPTY
    }

	private Directions dir = Directions.EMPTY;

	public final State SEARCH = new State(Color.black, null, this.agent);
	
    private boolean starting = true;

    /**
	 * No-arg constructor used only by the resource loader
	 */
	public SearchStrategy() {
		
	}
	
    /**
	 * @param agent 
	 * 
	 */
	public SearchStrategy(@NonNull final Agent agent, @NonNull final SimulationGrid simGrid) {
		super(agent, simGrid);
		state = SEARCH;
	}

	@Override
	public Optional<TaskToDo> getTaskToDo() {
		return Optional.empty();
	}

	@Override
	public void setOptions(AgentAppOpts options) {
		// Nothing to do
	}

	/**
	 * 
	 * @see org.roach.intelligentagents.model.strategy.AgentStrategy#initStates()
	 */
	@Override
	protected void initStates() {
		SEARCH.setAgent(this.agent);
		SEARCH.setAlgorithm(a -> {
	        if (starting) {
	            starting = false;
	            if (a.getLoc().getY() == 0) dir = (a.getLoc().getX() == 0) ? Directions.SOUTHEAST : Directions.SOUTHWEST;
	            else dir = (a.getLoc().getX() == 0) ? Directions.NORTHEAST : Directions.NORTHWEST;
	        } else {
	            if (a.foundNewTask()) {
	                simGrid.executeTaskAt(a.getLoc());
	            }
	            switch (dir) {
	                case NORTHEAST:
	                    if (a.getLoc().getY() > 0) a.moveNorth();
	                    else {
	                        a.moveEast();
	                        if (a.foundNewTask()) simGrid.executeTaskAt(a.getLoc());
	                        dir = Directions.SOUTHEAST;
	                    }
	                    break;
	                case SOUTHEAST:
	                    if (a.getLoc().getY() < simGrid.getGridSize() - 1) a.moveSouth();
	                    else {
	                        a.moveEast();
	                        if (a.foundNewTask()) simGrid.executeTaskAt(a.getLoc());
	                        dir = Directions.NORTHEAST;
	                    }
	                    break;
	                case NORTHWEST:
	                    if (a.getLoc().getY() > 0) a.moveNorth();
	                    else {
	                        a.moveWest();
	                        if (a.foundNewTask()) simGrid.executeTaskAt(a.getLoc());
	                        dir = Directions.SOUTHWEST;
	                    }
	                    break;
	                case SOUTHWEST:
	                    if (a.getLoc().getY() < simGrid.getGridSize() - 1) a.moveSouth();
	                    else {
	                        a.moveWest();
	                        if (a.foundNewTask()) simGrid.executeTaskAt(a.getLoc());
	                        dir = Directions.NORTHWEST;
	                    }
	                    break;
	                default:
	                    assert(false) : "Impossible direction";
	            }
	        }
			
		});
	}

	@Override
	public @NonNull String getDescription() {
		return "Search Strategy";
	}
}
