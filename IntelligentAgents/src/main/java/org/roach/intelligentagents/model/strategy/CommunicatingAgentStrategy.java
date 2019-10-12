package org.roach.intelligentagents.model.strategy;

import java.awt.Color;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.State;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.TaskToDo;

/**
 * @author Larry S. Roach
 *
 */
public abstract class CommunicatingAgentStrategy extends AgentStrategy {

	/**
	 * The default state
	 */
	public final State RANDOM = new State(Color.black, null, this.agent);
	/**
	 * State where the agent is "going to" a location
	 */
	public final State GOTO = new State(Color.black, null, this.agent);

	/**
	 * property representing communications distance for all agents
	 */
	protected int commDist = 12;
	/** The time remaining to leave communications on. */
	protected int commTime = 3;
	/**
	 * Getter for 
	 * @return the commDist
	 */
	public int getCommDist() {
		return commDist;
	}

	@NonNull
	protected Location locToGoto = new Location(0, 0);

	/**
	 * @param options
	 * @see org.roach.intelligentagents.model.strategy.AgentStrategy#setOptions(org.roach.intelligentagents.AgentAppOpts)
	 */
	@Override
	public void setOptions(AgentAppOpts options) {
		this.commDist = options.commDist;
		this.commTime = options.commTime;
	}

	/**
	 * The location of the task, if any, that the agent is broadcasting in COMMS
	 * mode, or going to in GOTO mode.
	 */
	protected Location commTaskLoc;

	/**
	 * 
	 */
	public final State RANDOMCOMMS = new State();

	/** Flag to indicate whether a broadcast has been received. */
	protected boolean broadcastReceived;

	/**
	 * @param agent
	 */
	public CommunicatingAgentStrategy(@NonNull final Agent agent) {
		super(agent);
	}

	@Override
	public abstract Optional<TaskToDo> getTaskToDo();

	/**
	 * When an agent is is Random-Comms state, this method is used to "broadcast"
	 * the location of the last-executed task to other agents in range.
	 * @param actionIfNotPossible 
	 */
	public void sendMessageIfPossible(Runnable actionIfNotPossible) {
		if (commTime > 0) {
			agent.getmPcs().firePropertyChange(PropertyConstants.SEND_MESSAGE, null, commTaskLoc);
			commTime--;
		} else {
			actionIfNotPossible.run();
		}
	}
	
    /**
     * @param receivedLoc
     */
    public void receiveMessage(@NonNull final Location receivedLoc) {
        Task t = Task.getTask(receivedLoc);
        if (t != null && t.isComplete()) {
            agent.getExecutedTasks().add(t);
        }

        if (!agent.getExecutedTasks().contains(t)) {
        	this.locToGoto = receivedLoc;
            setBroadcastReceived(true);
        }
    }

	/**
	 * Initialize communications
	 */
	public void initComms() {
		Task task = Task.getTask(agent.getLoc());
		if (task != null && !task.isComplete()) { // if task isn'task compete
			// Important - since loc will be changed later, commTaskLoc must
			// be a clone of loc, not a reference to it
			commTaskLoc = agent.getLoc().clone();
		}
	}

	/**
	 * Sets the broadcastReceived flag.
	 * 
	 * @param broadcastReceived
	 *            value to set the flag to
	 */
	public void setBroadcastReceived(boolean broadcastReceived) {
		this.broadcastReceived = broadcastReceived;
	}

	/**
	 * Checks if a broadcast has been received from another agent.
	 * 
	 * @return true if a broadcast has been received, false otherwise
	 */
	public boolean isBroadcastReceived() {
		return broadcastReceived;
	}

	/**
	 * 
	 * @see org.roach.intelligentagents.model.strategy.AgentStrategy#initStates()
	 */
	@Override
	protected void initStates() {
		RANDOM.setAlgorithm(a -> {
			a.randomMove();
			if (a.foundNewTask()) {
				a.executeTask();
				initComms();
				setState(RANDOMCOMMS);
			} else if (isBroadcastReceived()) {
				setBroadcastReceived(false); // reset broadcast flag
			}
		});
		RANDOM.setAgent(this.agent);

		/** The Goto state. */
		GOTO.setAlgorithm(a -> {
			a.getStrategy().getTaskToDo().ifPresentOrElse((t) -> {
				if (isBroadcastReceived()) {
					setBroadcastReceived(false);
				}
			},
			() -> getTaskToDo().ifPresent(task -> a.moveTowards(task.getLocation())));
			if (a.getStrategy().reachedTask()) {
				if (!a.hasDoneAlready(Task.getTask(a.getLoc())))
					a.executeTask(); // execute it and switch back to Random
				setState(RANDOM);
			}
		});
		GOTO.setAgent(this.agent);
		GOTO.setColor(Color.red);

		// /** The random-comms state */
		RANDOMCOMMS.setColor(Color.blue);
		RANDOMCOMMS.setAlgorithm(a -> {
			sendMessageIfPossible(() -> state = RANDOM);
			a.randomMove();
			if (a.foundNewTask()) {
				a.executeTask();
				initComms();
				a.getStrategy().setState(RANDOMCOMMS);
			} else if (isBroadcastReceived()) {
				setBroadcastReceived(false);
				a.getStrategy().setState(GOTO);
			}
		});
		RANDOMCOMMS.setAgent(this.agent);
	}

}
