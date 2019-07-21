package org.roach.intelligentagents.model.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.TaskToDo;

/**
 * @author Larry S. Roach
 *
 */
public class NeighborhoodStrategy extends CommunicatingAgentStrategy {

	/**
	 * @param options
	 * @see org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy#setOptions(org.roach.intelligentagents.AgentAppOpts)
	 */
	@Override
	public void setOptions(AgentAppOpts options) {
		super.setOptions(options);
    	agent.setProperty(NeighborhoodStrategy.NUM_NEIGHBORS, options.neighbors);
		if (agent.getId() == 0)
			return;
		int numNeighbors = ((Integer)agent.getProperty(NUM_NEIGHBORS)).intValue();
		if (agent.getId() % numNeighbors == 0) {
			((NeighborhoodStrategy) Agent.getAgents().get(agent.getId() - 1).getStrategy()).addNeighbor(
					Agent.getAgents().get(agent.getId() - numNeighbors));
		} else {
			((NeighborhoodStrategy) Agent.getAgents().get(agent.getId() - 1).getStrategy()).addNeighbor(agent);
		}
	}

	/** Location to go to in Goto state. */
	private static final String LOC_TO_GOTO = "locToGoto";
	/** The list of this agent's subordinates */
	private static final String NEIGHBORS = "neighbors";
	/** The number of agents in each neighborhood */
	public static final String NUM_NEIGHBORS = "numNeighbors";

	/**
	 * @param agent
	 * 
	 */
	public NeighborhoodStrategy(Agent agent) {
		super(agent);
		agent.setProperty(NEIGHBORS, new ArrayList<Agent>());

		RANDOM.setAlgorithm(a -> {
			if (isBroadcastReceived()) {
				setBroadcastReceived(false);
				state = GOTO;
			} else {
				a.getLoc().randomMove();
				if (a.foundNewTask()) {
					a.executeTask();
					notifyNeighbors(a.getLoc());
				}
			}
		});

		/**
		 * The actions for this agent to take in the GOTO state.
		 * <ol>
		 * <li>Move towards the current broadcast location.</li>
		 * <li>If a message is received, pass it along to subordinates</li>
		 * <li>If the task is reached, execute it, notify subordinates about it, and
		 * switch back to RANDOM</li>
		 * </ol>
		 */
		GOTO.setAlgorithm(a -> {
			getTaskToDo().ifPresent((t) -> a.moveTowards(t.getLocation()));
			if (isBroadcastReceived()) {
				setBroadcastReceived(false);
				notifyNeighbors((Location)a.getProperty(LOC_TO_GOTO));
			}
			if (reachedTask()) {
				a.executeTask();
				notifyNeighbors(a.getLoc());
				state = RANDOM;
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void addNeighbor(Agent n) {
		((ArrayList<Agent>)agent.getProperty(NEIGHBORS)).add(n);
	}

	/**
	 * @return the current task to do
	 */
	@Override
	public Optional<TaskToDo> getTaskToDo() {
		return Optional.of(new TaskToDo((Location)agent.getProperty(LOC_TO_GOTO)));
	}

	@Override
	public void receiveMessage(Location receivedLoc) {
		Task t = Task.getTask(receivedLoc);
		if (t != null && t.isComplete()) {
			agent.getExecutedTasks().add(t);
		}

		if (!agent.getExecutedTasks().contains(t)) {
			agent.setProperty(LOC_TO_GOTO, receivedLoc);
			notifyNeighbors(receivedLoc);
			setBroadcastReceived(true);
		}
	}

	/**
	 * Broadcasts any received message to subordinates (if any). This is a recursive
	 * subroutine, calling itself for the next lower level, unless level is 0. The
	 * end result is that when a MilitaryStrategy calls notifySubordinates, up to
	 * level^2-1 agents are notified.
	 * 
	 * @param l
	 *            The location subordinates should go to
	 */
	public void notifyNeighbors(Location l) {
		@SuppressWarnings("unchecked")
		ArrayList<Agent> neighbors = (ArrayList<Agent>)agent.getProperty(NEIGHBORS);
		for (Agent m : neighbors) {
			((NeighborhoodStrategy)m.getStrategy()).receiveMessage(l);
		}
	}

	/**
	 * When in Goto state, checks if the agent has reached its goal.
	 * 
	 * @return True if task has been reached, false if not
	 */
	@Override
	public boolean reachedTask() {
		return agent.getLoc().equals(agent.getProperty(LOC_TO_GOTO));
	}

	/**
	 * @return neighbors
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Agent> getCommunicants() {
		return (List<Agent>)agent.getProperty(NEIGHBORS);
	}
}
