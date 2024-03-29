package org.roach.intelligentagents.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy;

/**
 * @author Larry S. Roach
 *
 */
public class SimulationGrid implements PropertyChangeListener {
    private List<List<Set<Agent>>> grid;
    private final int gridSize;
    /** A grid of tasks in x-y coordinates */
    private Task[][] taskGrid = new Task[0][0];
    /** List of all tasks */
    @NonNull
    private List<Task> taskList = new ArrayList<>();
    /** Number of tasks */
    private final int numTasks;
    /** Number of "complete" tasks */
    private volatile int numTasksComplete; // NOPMD by Family on 11/26/19, 2:28 PM
    private PropertyChangeSupport mPcs = new PropertyChangeSupport(this);
    /** The list of all agents */
    @NonNull
    private List<Agent> agents = new ArrayList<>();

    /**
     * A table of sets of agents. This data structure is used to greatly speed up
     * the "communications" between agents. Each table entry represents a row of the
     * grid-space. Each entry in a set represents a column value. This allows an
     * agent to search the space "nearby" in less than O(n^2) time, by essentially
     * allowing the algorithm to skip blank rows and columns.
     */
    private Map<Integer, HashSet<Integer>> xRef;

    public SimulationGrid(final int gridSize, final int numTasks) {
	this.gridSize = gridSize;
	this.numTasks = numTasks;
	this.grid = Collections.nCopies(gridSize, Collections.nCopies(gridSize, new HashSet<Agent>()));
	this.xRef = new HashMap<>();
    }

    @Override
    public void propertyChange(@Nullable final PropertyChangeEvent evt) {
	if (evt == null)
	    return;
	final String message = evt.getPropertyName(); // NOPMD by Family on 11/26/19, 2:44 PM
	final Agent sender = (Agent) evt.getSource();
	if (sender == null)
	    return;
	final Location newLocation = (Location) evt.getNewValue();
	if (newLocation == null)
	    return;
	if (message.equals(PropertyConstants.NEW_AGENT)) {
	    addAgentToCell(sender);
	    addAgentToXref(sender);
	} else if (message.equals(PropertyConstants.SEND_MESSAGE)) {
	    Set<Agent> list = getNearbyAgents(sender.getLoc(),
		    ((CommunicatingAgentStrategy) sender.getStrategy()).getCommDist());
	    for (Agent receiver : list) { // For each agent in the list
		// Send the agent the message
		((CommunicatingAgentStrategy) receiver.getStrategy()).receiveMessage(newLocation);
	    }
	} else if (message.equals(PropertyConstants.PREPARE_TO_ACT)) {
	    removeAgentFromCell(sender);
	    removeAgentFromXref(sender);
	} else if (message.equals(PropertyConstants.UPDATE_GRID)) {
	    addAgentToCell(sender);
	    addAgentToXref(sender);
	} else {
	    System.err.println("Unsupported message type: " + evt.getPropertyName()); // NOPMD by Family on 11/26/19,
										      // 2:19 PM
	}
    }

    /**
     * Used for record-keeping; adds the agent to the list of agents in the xRef
     * data structure.
     * 
     * @param a The agent to be added
     */
    private void addAgentToXref(@NonNull final Agent a) {
	Integer X = a.getLoc()
		     .x();
	// If the HashSet entry doesn't exist, create it
	if (!xRef.containsKey(X)) {
	    xRef.put(X, new HashSet<Integer>());
	}
	// Add the y-coordinate to the set
	xRef.get(X)
	    .add(a.getLoc()
		  .y());
    }

    /**
     * Used for record-keeping; removes the agent from the list of agents in the
     * xRef data structure.
     * 
     * @param a The agent to be removed
     */
    private void removeAgentFromXref(@NonNull final Agent a) {
	// Remove value from set
	xRef.get(a.getLoc()
		  .x())
	    .remove(a.getLoc()
		     .y());
    }

    /**
     * @param a The agent to be added
     */
    private void addAgentToCell(@NonNull final Agent a) {
	grid.get(a.getLoc()
		  .x())
	    .get(a.getLoc()
		  .y())
	    .add(a);
    }

    /**
     * Used for record-keeping; removes the agent from the list of agents in the
     * agentGrid data structure.
     * 
     * @param a The agent to be removed
     */
    private void removeAgentFromCell(@NonNull final Agent a) {
	grid.get(a.getLoc()
		  .x())
	    .get(a.getLoc()
		  .y())
	    .remove(a);
    }

    /**
     * Get a list of agents in broadcast range.
     * 
     * @return List of agents
     * @param loc      The location at the center of the search area
     * @param distance The radius to search around the location
     */
    @NonNull
    public Set<Agent> getNearbyAgents(@NonNull final Location loc, final int distance) {
	int commDistSq = distance * distance;
	// Initialize the list to return
	Set<Agent> list = new HashSet<>();
	// Calculate the rows to check
	Integer startCheckX = (loc.x() > distance) ? (loc.x() - distance) : 0;
	Integer endCheckX = ((loc.x() + distance) < (gridSize - 1)) ? (loc.x() + distance) : (gridSize - 1);

	// Go through each X value within the boundaries
	for (Integer X = startCheckX; X <= endCheckX; X++) {
	    if (xRef.containsKey(X)) {
		// If there are any agents on this row...
		for (Integer Y : xRef.get(X)) {
		    // For each Y-coordinate on the row
		    Location checkLoc = new Location(X, Y); // NOPMD by Family on 11/26/19, 2:24 PM
		    if (!loc.equals(checkLoc)) {
			// Make sure loc != checkLoc (i.e., agent doesn't
			// communicate with itself)
			if (loc.isInCircle(checkLoc, commDistSq)) // If the location is within broadcast range, add
			    // all agents in that location to the list
			    list.addAll(grid.get(checkLoc.x())
					    .get(checkLoc.y()));
		    }
		}
	    }
	}
	return list;
    }

    /**
     * Getter for
     * 
     * @return the gridSize
     */
    public int getGridSize() {
	return gridSize;
    }

    public void initTaskGrid() {
	taskGrid = new Task[this.gridSize][this.gridSize];
	if (!taskList.isEmpty()) {
	    taskList.clear();

	}
	for (int whichTask = 0; whichTask < numTasks; whichTask++) {
	    boolean taskPlaced = false;
	    while (!taskPlaced) { // Keep picking random locations until an unused
		// square is found.
		Location tempLoc = Location.randomLocation(this.gridSize);
		if (!isTask(tempLoc)) { // If no task exists there
		    taskPlaced = true; // Set to exit the while-loop
		    Task newTask = new Task(tempLoc); // NOPMD by Family on 11/26/19, 2:43 PM
		    taskList.add(newTask);
		    taskGrid[tempLoc.x()][tempLoc.y()] = newTask;
		}
	    }
	}
	numTasksComplete = 0;
    }

    public void setAgents(List<Agent> agents) {
	this.agents = agents;
    }

    /**
     * Executes the task at the given location.
     * 
     * @param loc The location of the task to execute
     */
    public void executeTaskAt(@NonNull final Location loc) {
	if (isTask(loc)) {
	    Task task = taskGrid[loc.x()][loc.y()];
	    task.execute();
	    if (task.isComplete()) { // If task is now complete, let listeners know
		numTasksComplete++;
		mPcs.firePropertyChange(PropertyConstants.TASK_COMPLETE, numTasksComplete - 1, numTasksComplete);
	    }

	}
    }

    /**
     * Determines if a task exists at a given location.
     * 
     * @param loc The location to look at
     * @return True if a task exists at loc
     */
    public boolean isTask(@NonNull final Location loc) {
	return (taskGrid[loc.x()][loc.y()] != null);
    }

    /**
     * Returns the task at the given location, or null if no task exists.
     * 
     * @param loc The location of the task
     * @return The task at the given location, or null
     */
    @Nullable
    public Task getTask(@NonNull final Location loc) {
	return taskGrid[loc.x()][loc.y()];
    }

    /**
     * Checks to see if a given task has been completed.
     * 
     * @param loc The location of the task to check
     * @return True if the task is complete, false if not
     */
    public boolean isTaskComplete(@NonNull final Location loc) {
	Task t = getTask(loc);
	if (t != null)
	    return t.isComplete();
	return false;
    }

    /**
     * @return task list
     */
    @NonNull
    public List<Task> getTaskList() {
	return taskList;
    }

    public void addPropertyChangeListener(final @NonNull PropertyChangeListener gui) {
	this.mPcs.addPropertyChangeListener(gui);
    }

    /**
     * Get the number of tasks in the simulation.
     * 
     * @return Size of Tasks array
     */
    public int getNumTasks() {
	return numTasks;
    }

    /**
     * Get the list of all agents
     * 
     * @return Agents
     */
    public List<Agent> getAgents() {
	return agents;
    }

    public int getNumAgents() {
	return agents.size();
    }

}