package org.roach.intelligentagents.model.strategy;

import java.awt.Color;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.State;
import org.roach.intelligentagents.model.TaskToDo;

/**
 * @author Larry S. Roach
 *
 */
public abstract class AgentStrategy {
	protected Agent agent;
	/** The current state of the agent. */
	protected State state = State.NOT_SET;
	/**
	 * 
	 */
	public final State RANDOM = new State(Color.black, null, this.agent);
	/**
	 * 
	 */
	public final State GOTO = new State(Color.black, null, this.agent);
	/**
	 * @param agent 
	 * 
	 */
	public AgentStrategy(@NonNull final Agent agent) {
		this.agent = agent;
	}

	protected void initStates() {
		assert(agent != null);
		RANDOM.setAlgorithm(a -> {
			a.randomMove();
			if (a.foundNewTask()) {
				a.executeTask();
			}	
		});
		this.state = RANDOM;
	}
	
	/**
	 * @param options
	 */
	abstract public void setOptions(AgentAppOpts options);

	/**
	 * Setter for
	 * 
	 * @param state
	 *            the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * Getter for
	 * 
	 * @return the agent
	 */
	public Agent getAgent() {
		return agent;
	}

	/**
	 * Performs the action(s) of the agent based on the current state. Actions are
	 * delegated back to the state.
	 */
	public void doAction() {
		assert (agent != null);
		agent.getmPcs().firePropertyChange(PropertyConstants.PREPARE_TO_ACT, null, null);
		state.doAction();
		agent.getmPcs().firePropertyChange(PropertyConstants.UPDATE_GRID, null, null);
	}

	/**
	 * Gets the current task that the agent is going to, if any.
	 * 
	 * @return An Optional TaskToDo
	 */
	public abstract Optional<TaskToDo> getTaskToDo();

	/**
	 * When in Goto state, checks if the agent has reached its goal.
	 * 
	 * @return True if task has been reached, false if not
	 */
	public boolean reachedTask() {
		Optional<TaskToDo> t = getTaskToDo();
		if (t.isPresent()) {
			return agent.getLoc().equals(t.get().getLocation());
		}
		return false;
	}

	/**
	 * Setter for 
	 * @param agent the agent to set
	 */
	public void setAgent(@NonNull final Agent agent) {
		this.agent = agent;
		initStates();
	}
}
