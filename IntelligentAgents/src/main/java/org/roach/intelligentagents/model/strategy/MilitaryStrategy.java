/*
 * MilitaryStrategy.java
 *
 * Created on June 2, 2007, 2:48 PM
 */
package org.roach.intelligentagents.model.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.TaskToDo;


/**
 * This agent performs in a military-style hierarchy.  Each agent at most two
 * subordinates.  When an agent receives a broadcast, it first checks to see if
 * it is busy.  If not, it switches to Goto mode.  In either case, it also passes
 * the message along to its subordinates.  They in turn pass the message down the
 * chain to their subordinates.
 * @author L. Stephen Roach
 */
public class MilitaryStrategy extends CommunicatingAgentStrategy {
	private int numSubordinates = 2;
	private int numLevels = 3;
	private List<Agent> subordinates = new ArrayList<>();
	
	/**
	 * @param options
	 * @see org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy#setOptions(org.roach.intelligentagents.AgentAppOpts)
	 */
	@Override
	public void setOptions(AgentAppOpts options) {
		super.setOptions(options);
		this.numSubordinates = options.numSubordinates;
		this.numLevels = options.numLevels;
        // For all agents with ID not 0 or 1, they should have two subordinates.
        // I.e., agent 0 has subordinate agent 1. Agent 1 has subordinates 2 and
        // 3. Agent 2 has subordinates 4 and 5, etc.
        if (agent.getId() > 0) {
            ((MilitaryStrategy)Agent.getAgents().get(agent.getId()/numSubordinates).getStrategy()).addSubordinate(agent);
        }
	}

    /**
     * Constructor. Adds this agent as a subordinate to the "boss" agent.
     * @param agent 
     */
    public MilitaryStrategy(Agent agent) {
    	super(agent);
    	
        /*
         * The actions this agent should do in the RANDOM state.
         * <ol>
         * <li>If a message has been received, switch to the GOTO state.</li>
         * <li>Otherwise, move one cell in a random direction. If this results in
         * finding a new task, execute it and notify subordinates.</li>
         * </ol>
         */
        RANDOM.setAlgorithm(a -> {
            if (isBroadcastReceived()) {
                setBroadcastReceived(false);
                state = GOTO;
            } else {
                a.getLoc().randomMove();
                if (a.foundNewTask()) {
                    a.executeTask();
                    notifySubordinates(a.getLoc(), numLevels);
                }
            }
        });
        
        GOTO.setAlgorithm(a -> {
            getTaskToDo().ifPresent((t) -> a.moveTowards(t.getLocation()));
            if (isBroadcastReceived()) {
                setBroadcastReceived(false);
                notifySubordinates(locToGoto, numLevels);
            } else if (reachedTask()) {
                a.executeTask();
                notifySubordinates(a.getLoc(), numLevels);
                state = RANDOM;
            } else if (a.foundNewTask()) {
            	a.executeTask();
            	notifySubordinates(a.getLoc(), numLevels);
            }
        });
        state = RANDOM;
    }
    /**
     * Adds a subordinate for this agent.
     * @param sub The AGent to add as subordinate
     */
	public void addSubordinate(Agent sub) {
    	subordinates.add(sub);
    }

    /** Broadcasts any received message to subordinates (if any). This is a
     * recursive subroutine, calling itself for the next lower level, unless
     * level is 0. The end result is that when a MilitaryStrategy calls
     * notifySubordinates, up to level^2-1 agents are notified.
     * @param l The location subordinates should go to
     * @param level The level of this recursive call.
     */
    public void notifySubordinates(Location l, int level) {
        if (level < 0) {
            return;
        }
        for (Agent m : subordinates) {
            if (m.getLoc().isInCircle(l, commDist)) {
                ((MilitaryStrategy)m.getStrategy()).receiveMessage(l);
                ((MilitaryStrategy)m.getStrategy()).notifySubordinates(l, level - 1);
            }
        }
    }

    /**
     * The actions for this agent to take in the GOTO state.
     * <ol>
     * <li>Move towards the current broadcast location.</li>
     * <li>If a message is received, pass it along to subordinates</li>
     * <li>If the task is reached, execute it, notify subordinates about it, and
     * switch back to RANDOM</li>
     * </ol>
     */

    /**
     * When in Goto state, checks if the agent has reached its goal.
     * @return True if task has been reached, false if not
     */
    @Override
    public boolean reachedTask() {
        return agent.getLoc().equals(locToGoto);
    }

    /**
     * Get the current task to go to
     * @return An optional taskToDo, or empty if none
     */
    @Override
    public Optional<TaskToDo> getTaskToDo() {
        return Optional.of(new TaskToDo(locToGoto));
    }
    
	/**
	 * @return subordinates
	 */
	@Override
	public List<Agent> getCommunicants() {
		return subordinates;
	}
}
