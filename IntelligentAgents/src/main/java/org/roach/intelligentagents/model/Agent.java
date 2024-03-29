/*
 * Agent.java
 *
 * Created on April 28, 2007, 6:36 PM
 */

package org.roach.intelligentagents.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.model.strategy.AgentStrategy;

/**
 * An Agent is the SuperClass for all agents. It contains methods and properties
 * used by all agents, such as state representation, randomMove, doAction, etc.
 * 
 * @author L. Stephen Roach
 * @version %I%, %G%
 */
public class Agent implements ISimItem {
    private static int id_root;

    /**
     * A set of locations of tasks that the agent has already executed; used to
     * prevent an agent from executing a task more than once.
     */
    protected Set<Task> executedTasks;
    /** A unique identifier for each Agent. */
    protected int id;

    /** Location of the agent within the sim-space. */
    @NonNull
    protected Location loc;

    // Setup property-change support
    @NonNull
    private final PropertyChangeSupport mPcs = new PropertyChangeSupport(this);

    protected final AgentStrategy strategy;

    @NonNull
    private SimulationGrid simGrid;

    public void setSimGrid(final SimulationGrid simGrid) {
	this.simGrid = simGrid;
	addPropertyChangeListener(simGrid);
	this.loc = Location.randomLocation(simGrid.getGridSize());
    }

    public Agent(final AgentStrategy strategy) {
	this.strategy = strategy;
	id = id_root++;
	executedTasks = new HashSet<>();
    }

    /**
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
	mPcs.addPropertyChangeListener(listener);
    }

    /**
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
	mPcs.removePropertyChangeListener(listener);
    }

    public void firePropertyChange(@NonNull final String propertyName, final Object oldValue, final Object newValue) {
	mPcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * Determine if this Agent is equal to the Agent o.
     * 
     * @param o The Agent to be compared
     * @return True if the Agent IDs are the same
     */
    @Override
    public boolean equals(final @Nullable Object o) {
	if (!(o instanceof Agent)) {
	    return false;
	}
	return (id == ((Agent) o).id);
    }

    /**
     * Executes the task at the current location.
     */
    public void executeTask() {
	simGrid.executeTaskAt(loc);
	Task gotTask = simGrid.getTask(loc);
	if (gotTask != null)
	    executedTasks.add(gotTask);
    }

    /**
     * Checks whether the agent has found a new task. A new task is a task that a)
     * exists, b) is not complete, and c) has not been executed before by this
     * agent.
     * 
     * @return True if new task found
     */
    public boolean foundNewTask() {
	return (simGrid.isTask(loc) && !simGrid.isTaskComplete(loc)) && !hasDoneAlready(simGrid.getTask(loc));
    }

    /**
     * Getter for
     * 
     * @return the executedTasks
     */
    public Set<Task> getExecutedTasks() {
	return executedTasks;
    }

    /**
     * Getter for
     * 
     * @return the id
     */
    public int getId() {
	return id;
    }

    /**
     * Get the location of the agent within the sim-space.
     * 
     * @return loc
     */
    @NonNull
    public Location getLoc() {
	return loc;
    }

    /**
     * Getter for
     * 
     * @return the mPcs
     */
    @NonNull
    public PropertyChangeSupport getmPcs() {
	return mPcs;
    }

    /**
     * Getter for
     * 
     * @return the strategy
     */
    public AgentStrategy getStrategy() {
	return strategy;
    }

    /**
     * Checks if the agent has already executed a task.
     * 
     * @param t The task to check
     * @return True if already-executed list contains the task, False otherwise
     */
    public boolean hasDoneAlready(@Nullable final Task t) {
	return executedTasks.contains(t);
    }

    /**
     * Gets the hash code of this agent.
     * 
     * @return hash code
     */
    @Override
    public int hashCode() {
	return id % 7;
    }

    /**
     * Gets the String representation of this agent.
     * 
     * @return String representation including type of agent, location, and index
     */
    @Override
    public String toString() {
	return this.getClass()
		   .toString()
		+ ", location: " + loc;
    }

    /**
     * Setter for
     * 
     * @param loc the loc to set
     */
    public void setLoc(@NonNull final Location loc) {
	this.loc = loc;
    }

    public void moveNorth() {
	if (this.loc.y() > 0)
	    this.loc = new Location(this.loc.x(), this.loc.y() - 1);
    }

    public void moveSouth() {
	if (this.loc.y() < simGrid.getGridSize() - 1)
	    this.loc = new Location(this.loc.x(), this.loc.y() + 1);
    }

    public void moveWest() {
	if (this.loc.x() > 0)
	    this.loc = new Location(this.loc.x() - 1, this.loc.y());
    }

    public void moveEast() {
	if (this.loc.x() < simGrid.getGridSize() - 1)
	    this.loc = new Location(this.loc.x() + 1, this.loc.y());
    }

    public void moveNorthEast() {
	if (loc.x() < simGrid.getGridSize() - 1 && loc.y() > 0)
	    this.loc = new Location(this.loc.x() + 1, this.loc.y() - 1);
    }

    public void moveSouthEast() {
	if (loc.x() < simGrid.getGridSize() - 1 && loc.y() < simGrid.getGridSize() - 1)
	    this.loc = new Location(this.loc.x() + 1, this.loc.y() + 1);
    }

    public void moveNorthWest() {
	if (loc.x() > 0 && loc.y() > 0)
	    this.loc = new Location(this.loc.x() - 1, this.loc.y() - 1);
    }

    public void moveSouthWest() {
	if (loc.x() > 0 && loc.y() < simGrid.getGridSize() - 1)
	    this.loc = new Location(this.loc.x() - 1, this.loc.y() + 1);
    }

    /**
     * When in Random or Random-Comms state, this method chooses a random direction
     * and moves the agent.
     */
    public void randomMove() {
	Random rand = new Random();
	// Get a random number between 1 and 8
	int dir = rand.nextInt(8);
	switch (dir) { // Move the selected direction
	case 0:
	    // move north
	    moveNorth();
	    break;
	case 1:
	    // move east
	    moveEast();
	    break;
	case 2:
	    // move south
	    moveSouth();
	    break;
	case 3:
	    // move west
	    moveWest();
	    break;
	case 4:
	    moveNorthWest();
	    break;
	case 5:
	    moveSouthWest();
	    break;
	case 6:
	    moveNorthEast();
	    break;
	case 7:
	    moveSouthEast();
	    break;
	default:
	    break;
	}
    }

    /**
     * When the agent is in Goto state, this method moves it towards the task being
     * sought.
     * 
     * @param other Location to move towards
     */
    public void moveTowards(@NonNull final Location other) {
	// Calculate how far the agent is from the task along the X and Y axes
	int diffx = loc.getXDistance(other);
	int diffy = loc.getYDistance(other);

	// Move one step along whichever axis the agent is further from the task
	if (diffx > 0) {
	    if (diffy > 0)
		moveNorthWest();
	    else if (diffy < 0)
		moveSouthWest();
	    else
		moveWest();
	} else if (diffx < 0) {
	    if (diffy > 0)
		moveNorthEast();
	    else if (diffy < 0)
		moveSouthEast();
	    else
		moveEast();
	} else {
	    // diffx == 0
	    if (diffy > 0)
		moveNorth();
	    else if (diffy < 0)
		moveSouth();
	}
    }

}
