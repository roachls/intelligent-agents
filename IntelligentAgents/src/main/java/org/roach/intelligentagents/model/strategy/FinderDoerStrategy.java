package org.roach.intelligentagents.model.strategy;

import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.model.TaskToDo;

/**
 * @author Larry S. Roach A FinderDoerAgent agent is just like a
 *         PrioritizingStrategy, except that each agent is either a Finder or a
 *         Seeker. A Seeker can only be in Random or Goto. A Finder can only be
 *         in Random or RandomComms.
 */
public class FinderDoerStrategy extends CommunicatingAgentStrategy {

	protected boolean isFinder;
	protected int timeSinceLastBroadcast;
	protected int timeSinceLastFound;
	
	/**
	 * This constructor is only used by the resource loader
	 */
	public FinderDoerStrategy() {
		super();
	}

	/**
	 * @param agent
	 * 
	 */
	public FinderDoerStrategy(@NonNull final Agent agent, @NonNull final SimulationGrid simGrid) {
		super(agent, simGrid);
		this.isFinder = Math.random() > 0.7;
		this.state = RANDOM;
	}

	@Override
	public Optional<TaskToDo> getTaskToDo() {
		return Optional.empty();
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
			a.randomMove();
			if (isFinder) {
				if (a.foundNewTask()) {
					timeSinceLastFound = 0;
					agent.executeTask();
					initComms();
					state = RANDOMCOMMS;
				} else {
					timeSinceLastFound++;
					if (timeSinceLastFound > 20) { // NOPMD by Family on 11/26/19, 2:49 PM
						this.isFinder = false;
						this.timeSinceLastBroadcast = 0;
					}
				}
				if (isBroadcastReceived()) {
					setBroadcastReceived(false);
				}
			} else { // if Seeker
				if (isBroadcastReceived()) {
					this.timeSinceLastBroadcast = 0;
					setBroadcastReceived(false); // reset broadcast flag
					state = GOTO;
				} else {
					timeSinceLastBroadcast++;
					if (timeSinceLastBroadcast > 10) { // NOPMD by Family on 11/26/19, 2:49 PM
						isFinder = true;
						timeSinceLastFound = 0;
					}
				}
			}
		});

		RANDOMCOMMS.setAgent(this.agent);
		RANDOMCOMMS.setAlgorithm(a -> {
			assert (isFinder) : "Illegal state reached: Seeker in Comms state.";
			sendMessageIfPossible(() -> state = RANDOM);
			a.randomMove();
			if (agent.foundNewTask()) {
				agent.executeTask();
				initComms();
				state = RANDOMCOMMS;
			}

		});

		GOTO.setAgent(this.agent);
		GOTO.setAlgorithm(a -> {
			assert (!isFinder) : "Illegal state reached: Finder in Goto state";
			if (getTaskToDo().isEmpty()) {
				state = RANDOM;
			} else {
				if (isBroadcastReceived()) {
					setBroadcastReceived(false);
				}
				getTaskToDo().ifPresent((t) -> a.moveTowards(t.getLocation()));
				if (reachedTask()) {
					if (!agent.hasDoneAlready(simGrid.getTask(agent.getLoc()))) {
						agent.executeTask(); // execute it and switch back to Random
					}
					state = RANDOM;
				}
			}

		});
	}

	@Override
	public @NonNull String getDescription() {
		return "FinderDoer Strategy";
	}
}
