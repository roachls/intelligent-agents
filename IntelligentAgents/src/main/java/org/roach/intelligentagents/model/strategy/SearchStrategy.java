package org.roach.intelligentagents.model.strategy;

import java.util.List;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.Task;
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

	private boolean starting = true;

	/**
	 * @param agent 
	 * 
	 */
	public SearchStrategy(Agent agent) {
		super(agent);
		RANDOM.setAlgorithm(a -> {
	        if (starting) {
	            starting = false;
	            if (a.getLoc().getY() == 0) dir = (a.getLoc().getX() == 0) ? Directions.SOUTHEAST : Directions.SOUTHWEST;
	            else dir = (a.getLoc().getX() == 0) ? Directions.NORTHEAST : Directions.NORTHWEST;
	        } else {
	            if (a.foundNewTask()) {
	                Task.executeTaskAt(a.getLoc());
	            }
	            switch (dir) {
	                case NORTHEAST:
	                    if (a.getLoc().getY() > 0) a.getLoc().moveNorth();
	                    else {
	                        a.getLoc().moveEast();
	                        if (a.foundNewTask()) Task.executeTaskAt(a.getLoc());
	                        dir = Directions.SOUTHEAST;
	                    }
	                    break;
	                case SOUTHEAST:
	                    if (a.getLoc().getY() < Location.getGridSize() - 1) a.getLoc().moveSouth();
	                    else {
	                        a.getLoc().moveEast();
	                        if (a.foundNewTask()) Task.executeTaskAt(a.getLoc());
	                        dir = Directions.NORTHEAST;
	                    }
	                    break;
	                case NORTHWEST:
	                    if (a.getLoc().getY() > 0) a.getLoc().moveNorth();
	                    else {
	                        a.getLoc().moveWest();
	                        if (a.foundNewTask()) Task.executeTaskAt(a.getLoc());
	                        dir = Directions.SOUTHWEST;
	                    }
	                    break;
	                case SOUTHWEST:
	                    if (a.getLoc().getY() < Location.getGridSize() - 1) a.getLoc().moveSouth();
	                    else {
	                        a.getLoc().moveWest();
	                        if (a.foundNewTask()) Task.executeTaskAt(a.getLoc());
	                        dir = Directions.NORTHWEST;
	                    }
	                    break;
	                default:
	                    assert(false) : "Impossible direction";
	            }
	        }
			
		});
		state = RANDOM;
	}
	
    enum Directions {
        NORTHEAST, SOUTHEAST, NORTHWEST, SOUTHWEST
    }
    private Directions dir;

	@Override
	public TaskToDo getTaskToDo() {
		return null;
	}

	@Override
	public List<Agent> getCommunicants() {
		return null;
	}

	@Override
	public void setOptions(AgentAppOpts options) {
		
	}
}
