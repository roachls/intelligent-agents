package org.roach.intelligentagents.model.strategy;

import java.util.List;
import java.util.Optional;

import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.TaskToDo;

/**
 * @author Larry S. Roach A FinderDoerAgent agent is just like a
 *         PrioritizingStrategy, except that each agent is either a Finder or a
 *         Seeker. A Seeker can only be in Random or Goto. A Finder can only be
 *         in Random or RandomComms.
 */
public class FinderDoerStrategy extends CommunicatingAgentStrategy {

	private final static String IS_FINDER = "isFinder";
	private final static String TIME_SINCE_LAST_BROADCAST = "timeSinceLastBroadcast";
	private final static String TIME_SINCE_LAST_FOUND = "timeSinceLastFound";

	/**
	 * @param agent
	 * 
	 */
	public FinderDoerStrategy(Agent agent) {
		super(agent);
		agent.setProperty(IS_FINDER, Boolean.valueOf(Math.random() > 0.7));
		agent.setProperty(TIME_SINCE_LAST_BROADCAST, Integer.valueOf(0));
		agent.setProperty(TIME_SINCE_LAST_FOUND, Integer.valueOf(0));
		
		RANDOM.setAlgorithm(a -> {
			a.getLoc().randomMove();
			if ((Boolean) a.getProperty(IS_FINDER)) {
				if (a.foundNewTask()) {
					a.setProperty(TIME_SINCE_LAST_FOUND, 0);
					agent.executeTask();
					initComms();
					state = RANDOMCOMMS;
				} else {
					a.setProperty(TIME_SINCE_LAST_FOUND, (Integer) a.getProperty(TIME_SINCE_LAST_FOUND) + 1);
					if ((Integer) a.getProperty(TIME_SINCE_LAST_FOUND) > 20) {
						a.setProperty(IS_FINDER, Boolean.FALSE);
						a.setProperty(TIME_SINCE_LAST_BROADCAST, Integer.valueOf(0));
					}
				}
			} else { // if Seeker
				if (isBroadcastReceived()) {
					a.setProperty(TIME_SINCE_LAST_BROADCAST, Integer.valueOf(0));
					setBroadcastReceived(false); // reset broadcast flag
					state = GOTO;
				} else {
					a.setProperty(TIME_SINCE_LAST_BROADCAST,
							Integer.valueOf(((Integer) a.getProperty(TIME_SINCE_LAST_BROADCAST)).intValue() + 1));
					if ((Integer) a.getProperty(TIME_SINCE_LAST_BROADCAST) > 10) {
						a.setProperty(IS_FINDER, Boolean.TRUE);
						a.setProperty(TIME_SINCE_LAST_FOUND, 0);
					}
				}
			}
		});

		RANDOMCOMMS.setAlgorithm(a -> {
			assert ((Boolean) a.getProperty(IS_FINDER)) : "Illegal state reached: Seeker in Comms state.";
			sendMessageIfPossible(() -> state = RANDOM);
			a.getLoc().randomMove();
			if (agent.foundNewTask()) {
				agent.executeTask();
				initComms();
				state = RANDOMCOMMS;
			}

		});

		GOTO.setAlgorithm(a -> {
			assert (!(Boolean) a.getProperty(IS_FINDER)) : "Illegal state reached: Finder in Goto state";
			if (getTaskToDo() == null) {
				state = RANDOM;
			} else {
				if (isBroadcastReceived()) {
					setBroadcastReceived(false);
				}
				getTaskToDo().ifPresent((t) -> a.moveTowards(t.getLocation()));
				if (reachedTask()) {
					if (!agent.hasDoneAlready(Task.getTask(agent.getLoc()))) {
						agent.executeTask(); // execute it and switch back to Random
					}
					state = RANDOM;
				}
			}

		});
		this.state = RANDOM;
	}

	@Override
	public Optional<TaskToDo> getTaskToDo() {
		return Optional.empty();
	}

	@Override
	public List<Agent> getCommunicants() {
		return null;
	}
}
