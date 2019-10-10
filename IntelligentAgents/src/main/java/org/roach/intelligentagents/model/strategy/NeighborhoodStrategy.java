package org.roach.intelligentagents.model.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.TaskToDo;

/**
 * @author Larry S. Roach
 *
 */
@Strategy
public class NeighborhoodStrategy extends CommunicatingAgentStrategy {

	private int numNeighbors = 3;
	@NonNull private List<Agent> neighbors = new ArrayList<>(); 

	/**
	 * @param options
	 * @see org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy#setOptions(org.roach.intelligentagents.AgentAppOpts)
	 */
	@Override
	public void setOptions(AgentAppOpts options) {
		super.setOptions(options);
		this.numNeighbors = options.neighbors;
		if (agent.getId() == 0)
			return;
		if (agent.getId() % numNeighbors == 0) {
			((NeighborhoodStrategy) Agent.getAgents().get(agent.getId() - 1).getStrategy()).addNeighbor(
					Agent.getAgents().get(agent.getId() - numNeighbors));
		} else {
			((NeighborhoodStrategy) Agent.getAgents().get(agent.getId() - 1).getStrategy()).addNeighbor(agent);
		}
	}

	/**
	 * @param agent
	 * 
	 */
	public NeighborhoodStrategy(@NonNull final Agent agent) {
		super(agent);

		this.state = RANDOM;
	}

	private void addNeighbor(Agent n) {
		neighbors.add(n);
	}

	/**
	 * @return the current task to do
	 */
	@Override
	public Optional<TaskToDo> getTaskToDo() {
		return Optional.of(new TaskToDo(locToGoto));
	}

	@Override
	public void receiveMessage(Location receivedLoc) {
		Task t = Task.getTask(receivedLoc);
		if (t != null && t.isComplete()) {
			agent.getExecutedTasks().add(t);
		}

		if (!(agent.getExecutedTasks().contains(t) || this.locToGoto == null || this.locToGoto.equals(receivedLoc))) {
			this.locToGoto = receivedLoc;
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
		return agent.getLoc().equals(locToGoto);
	}

	/**
	 * @return neighbors
	 */
	@Override
	@NonNull public List<Agent> getCommunicants() {
		return neighbors;
	}

	/**
	 * 
	 * @see org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy#initStates()
	 */
	@Override
	protected void initStates() {
		super.initStates();
		RANDOM.setAgent(this.agent);
		RANDOM.setAlgorithm(a -> {
			if (isBroadcastReceived()) {
				setBroadcastReceived(false);
				if (!a.getExecutedTasks().contains(Task.getTask(locToGoto)))
					state = GOTO;
			} else {
				a.setLoc(a.getLoc().randomMove());
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
		GOTO.setAgent(this.agent);
		GOTO.setAlgorithm(a -> {
			getTaskToDo().ifPresent((t) -> a.moveTowards(t.getLocation()));
			if (isBroadcastReceived()) {
				setBroadcastReceived(false);
				notifyNeighbors(locToGoto);
			}
			if (reachedTask()) {
				a.executeTask();
				notifyNeighbors(a.getLoc());
				state = RANDOM;
			}
		});
		
	}
}
