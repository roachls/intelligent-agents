/*
 * PrioritizingStrategy.java
 * @version 2.0
 * Created on November 16, 2006, 8:35 PM
 */

package org.roach.intelligentagents.model.strategy;

import java.util.Optional;
import java.util.PriorityQueue;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.TaskToDo;

/**
 * The Prioritizing agent works exactly like the basic Communicating agent
 * except for the way in which it decides which task to do. When it receives a
 * task, it adds it to a priority Queue ordered by when the task was found.
 * Newer messages take priority over older ones, the assumption being that a
 * task found a long time ago has probably already been done by now and can thus
 * be virtually ignored.
 * 
 * @author L. Stephen Roach
 */
public class PrioritizingStrategy extends CommunicatingAgentStrategy {
    protected final PriorityQueue<TaskToDo> taskQueue = new PriorityQueue<>();
    protected TaskToDo taskToDo;

    /**
     * No-arg constructor used only by the resource loader
     */
    public PrioritizingStrategy() {

    }

    /**
     * @param agent
     */
    public PrioritizingStrategy(@NonNull final Agent agent, @NonNull final SimulationGrid simGrid) {
	super(agent, simGrid);
	state = RANDOM;
    }

    /**
     * This method correlates to the "sendMessage" method. It "receives" a broadcast
     * from another agent, switches the agent to Goto state, and sets the count-down
     * timer.
     * 
     * @param receivedLoc Location of the task
     */
    @Override
    public void receiveMessage(@NonNull final Location receivedLoc) {
	Task t = simGrid.getTask(receivedLoc);
	if (!agent.hasDoneAlready(t)) {
	    setBroadcastReceived(true);
	    // Add the state to the priority queue (not necessarily on top)
	    taskQueue.add(new TaskToDo(receivedLoc));
	    boolean hasDoneAlready = true;
	    while (!taskQueue.isEmpty() && hasDoneAlready) {
		this.taskToDo = taskQueue.peek();
		if (agent.hasDoneAlready(simGrid.getTask((taskToDo).getLocation()))) {
		    taskQueue.poll();
		} else {
		    hasDoneAlready = false;
		}
	    }
	}
    }

    /**
     * Gets the Task at the top of the task queue.
     * 
     * @return A TaskToDo, if any, null otherwise.
     */
    @Override
    public Optional<TaskToDo> getTaskToDo() {
	return taskToDo == null ? Optional.empty() : Optional.of(taskToDo);
    }

    /**
     * Executes the task at the current location.
     */
    public void executeTask() {
	taskQueue.poll(); // remove top TaskToDo from queue
    }

    @Override
    public @NonNull String getDescription() {
	return "Prioritizing Strategy";
    }
}
