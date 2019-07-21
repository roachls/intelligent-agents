/*
 * PrioritizingStrategy.java
 * @version 2.0
 * Created on November 16, 2006, 8:35 PM
 */

package org.roach.intelligentagents.model.strategy;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;

import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.TaskToDo;


/**
 * The Prioritizing agent works exactly like the basic Communicating agent
 * except for the way in which it decides which task to do. When it receives a
 * task, it adds it to a priority Queue ordered by when the task was found.
 * Newer messages take priority over older ones, the assumption being that a
 * task found a long time ago has probably already been done by now and can thus
 * be virtually ignored.
 * @author L. Stephen Roach
 */
public class PrioritizingStrategy extends CommunicatingAgentStrategy {
    /**
     * The priority queue of tasks an agent has received. An agent will remain in
     * GOTO state until this queue is empty.
     */
	protected static final String TASK_QUEUE = "taskQueue";
	protected static final String TASK_TO_DO = "taskToDo";
	
	/**
	 * @param agent
	 */
	public PrioritizingStrategy(Agent agent) {
		super(agent);
		agent.setProperty(TASK_TO_DO, Optional.empty());
		agent.setProperty(TASK_QUEUE, new PriorityQueue<TaskToDo>());
		state = RANDOM;
	}

    /**
     * This method correlates to the "sendMessage" method. It "receives" a
     * broadcast from another agent, switches the agent to Goto state, and
     * sets the count-down timer.
     * @param receivedLoc Location of the task
     */
    @SuppressWarnings("unchecked")
	@Override
	public void receiveMessage(Location receivedLoc) {
        Task t = Task.getTask(receivedLoc);
        if (!agent.hasDoneAlready(t)) {
			PriorityQueue<TaskToDo> taskQueue = (PriorityQueue<TaskToDo>)agent.getProperty(TASK_QUEUE);
            setBroadcastReceived(true);
            // Add the state to the priority queue (not necessarily on top)
            taskQueue.add(new TaskToDo(receivedLoc));
            boolean hasDoneAlready = true;
            while (!taskQueue.isEmpty() && hasDoneAlready) {
                agent.setProperty(TASK_TO_DO, Optional.of(taskQueue.peek()));
                if (agent.hasDoneAlready(Task.getTask(((Optional<TaskToDo>)agent.getProperty(TASK_TO_DO)).get().getLocation()))) {
                    taskQueue.poll();
                } else {
                    hasDoneAlready = false;
                }
            }
        }
    }

    /**
     * Gets the Task at the top of the task queue.
     * @return A TaskToDo, if any, null otherwise.
     */
    @SuppressWarnings("unchecked")
	@Override
    public Optional<TaskToDo> getTaskToDo() {
        return (Optional<TaskToDo>)agent.getProperty(TASK_TO_DO);
    }
    
    /**
     * Executes the task at the current location.
     */
    @SuppressWarnings("unchecked")
	public void executeTask() {
        ((PriorityQueue<TaskToDo>)agent.getProperty(TASK_QUEUE)).poll(); // remove top TaskToDo from queue
    }

	@Override
	public List<Agent> getCommunicants() {
		return null;
	}
}
