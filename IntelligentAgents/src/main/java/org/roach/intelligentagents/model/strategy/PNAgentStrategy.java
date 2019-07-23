package org.roach.intelligentagents.model.strategy;

import java.util.ArrayList;
import java.util.List;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.TaskToDo;


/**
 * @author Larry S. Roach
 *
 */
public class PNAgentStrategy extends PrioritizingStrategy {

	private int numNeighbors = 3;
	private List<Agent> neighbors = new ArrayList<>();

    /**
	 * @param options
	 * @see org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy#setOptions(org.roach.intelligentagents.AgentAppOpts)
	 */
	@Override
	public void setOptions(AgentAppOpts options) {
		super.setOptions(options);
    	numNeighbors = options.neighbors;
    	if (agent.getId() == 0) return;
   		if (agent.getId() % numNeighbors == 0) {
   			((PNAgentStrategy)Agent.getAgents().get(agent.getId()-1).getStrategy()).addNeighbor(Agent.getAgents().get(agent.getId() - numNeighbors));
   		} else {
   	  		((PNAgentStrategy)Agent.getAgents().get(agent.getId()-1).getStrategy()).addNeighbor(agent);
   		}
	}

	/**
     * @param agent 
     * 
     */
    public PNAgentStrategy(Agent agent) {
    	super(agent);
   		state = RANDOM;
    }

	private void addNeighbor(Agent n) {
    	neighbors.add(n);
    }

    /**
     * This method correlates to the "sendMessage" method. It "receives" a
     * broadcast from another agent, switches the agent to Goto state, and
     * sets the count-down timer.
     * @param receivedLoc Location of the task
     */
    @Override
    public void receiveMessage(Location receivedLoc) {
        Task t = Task.getTask(receivedLoc);
        TaskToDo taskToDo = new TaskToDo(receivedLoc);
        if (isBroadcastReceived() || agent.hasDoneAlready(t) || taskQueue.contains(taskToDo))
        	return;
        setBroadcastReceived(true);
        // Add the state to the priority queue (not necessarily on top)
        taskQueue.add(taskToDo);
        boolean hasDoneAlready = true;
        while (!taskQueue.isEmpty() && hasDoneAlready) {
            taskToDo = taskQueue.peek();
            if (agent.hasDoneAlready(Task.getTask(taskToDo.getLocation()))) {
                taskQueue.poll();
            } else {
            	sendMessageIfPossible(null);
                hasDoneAlready = false;
            }
        }
    }
    
    @Override
	public void sendMessageIfPossible(Runnable actionIfNotPossible) {
        for (Agent n : neighbors) {
            ((PNAgentStrategy)n.getStrategy()).receiveMessage(agent.getLoc());
        }
    }
}
