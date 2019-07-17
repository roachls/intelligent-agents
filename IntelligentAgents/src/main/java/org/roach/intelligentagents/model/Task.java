/*
 * Task.java
 *
 * Created on November 25, 2006, 1:28 PM
 * @author L. Stephen Roach
 * University of Nebraska at Omaha
 * CSCI 4450
 * Due 12 Dec 06
 */

package org.roach.intelligentagents.model;
import java.beans.*;
import java.util.ArrayList;
import java.util.Random;


/**
 * The Task class represents an executable task within the grid-space.
 * @author L. Stephen Roach
 */
public final class Task implements ISimItem {
// <editor-fold defaultstate="collapsed" desc="Static variables">
    /**
     * A task has this percentage probability of being bumped "up" in
     * completeness when executed.
     */
    private static final float TASK_COMPLETE_PROBABILITY = 1.0f;
    /** Constant indicating task completeness. */
    private static final int TASK_COMPLETE = 5;
    /** A grid of tasks in x-y coordinates */
    private static Task[][] taskGrid;
    /** Number of "complete" tasks */
    private static int numTasksComplete = 0;
    /** List of all tasks */
    private static ArrayList<Task> taskList;// </editor-fold>
    /** Location of the task within the sim-space. */
    private Location location;
    /** Current priority of the task. */
    private int prio;

    private PropertyChangeSupport mPcs = new PropertyChangeSupport(this);
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
    
    /**
     * @return task list
     */
    public static ArrayList<Task> getTaskList() {
        return taskList;
    }
    /**
     * Get the number of tasks in the simulation.
     * @return Size of Tasks array
     */
    public static int getNumTasks() {
        return taskList.size();
    }

    /**
     * Initializes the task grid and task list
     * @param numTasks Number of tasks to create
     * @param size Size of the playing field
     * @param pcl
     */
    public static void initTaskGrid(int numTasks, int size, PropertyChangeListener pcl) {
        if (taskGrid == null) {
            taskGrid = new Task[size][size];

        }
        if (taskList == null) {
            taskList = new ArrayList<Task>();

        }
        if (!taskList.isEmpty()) {
            taskList.clear();

        }
        for (int whichTask = 0; whichTask < numTasks; whichTask++) {
            boolean taskPlaced = false;
            Random rand = new Random();
            while (!taskPlaced) { // Keep picking random locations until an unused
                // square is found.
                int x = rand.nextInt(size); // pick a random x
                int y = rand.nextInt(size); // pick a random y
                Location tempLoc = new Location(x, y);
                if (!isTask(tempLoc)) { // If no task exists there
                    taskPlaced = true; // Set to exit the while-loop
                    Task newTask = new Task(tempLoc);
                    taskList.add(newTask);
                    taskGrid[x][y] = newTask;
                    newTask.addPropertyChangeListener(pcl);
                }
            }
        }
        numTasksComplete = 0;
    }

    /** Determines if a task exists at a given location.
     * @param   loc The location to look at
     * @return True if a task exists at loc
     */
    public static boolean isTask(Location loc) {
        return (taskGrid[loc.getX()][loc.getY()] != null);
    }

    /**
     * Executes the task at the given location.
     * @param loc The location of the task to execute
     */
    public static void executeTaskAt(Location loc) {
        if (isTask(loc)) {
            taskGrid[loc.getX()][loc.getY()].execute();
        }
    }

    /**
     * Checks to see if a given task has been completed.
     * @param loc The location of the task to check
     * @return True if the task is complete, false if not
     */
    public static boolean isTaskComplete(Location loc) {
        return getTask(loc).isComplete();
    }

    /**
     * Returns the task at the given location, or null if no task exists.
     * @param loc The location of the task
     * @return The task at the given location, or null
     */
    public static Task getTask(Location loc) {
        return taskGrid[loc.getX()][loc.getY()];
    }// </editor-fold>
    /**
     * @param inputLoc The location to place the task
     */
    public Task(final Location inputLoc) {
        location = inputLoc;
    }

    /**
     * Gets the current priority of the task.
     * @return prio
     */
    public int getPrio() {
        return prio;
    }
    /**
     * Executes the task. Has a 90% chance of incrementing the task priority.
     */
    public void execute() {
        if (!isComplete()) { // If task not already complete
            Random rand = new Random();
            float pUp = rand.nextFloat(); // Pick a random float
            if (pUp < TASK_COMPLETE_PROBABILITY) {
                prio++;
                mPcs.firePropertyChange("taskexecute", prio-1, prio);
            }
        } else // if task is already complete
            return;
        if (isComplete()) { // If task is now complete, let listeners know
            numTasksComplete++;
            mPcs.firePropertyChange("taskcomplete", numTasksComplete-1, numTasksComplete);
        }
    }
    /**
     * Return completeness of the task.
     * @return True if task is complete, False otherwise
     */
    public synchronized boolean isComplete() {
        return prio == getTaskComplete();
    }

    /**
     * Gets the hash code of the task.
     * @return hash code
     */
    @Override
    public int hashCode() {
        return location.hashCode();
    }
    /**
     * Determines if this task equals another task. Note that initTaskGrid
     * ensures no two tasks are in the same location, so this method is safe.
     * @param o The Task to be compared.
     * @return True if the two tasks have the same location, false if not or if
     * o is null
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Task)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        return ((Task) o).location.equals(location);
    }
    /**
     * Gets a String representation of this task.
     * @return String representation
     */
    @Override
    public String toString() {
        return "Task: " + location + ", prio: " + prio;
    }
    
	/**
	 * @return task complete status
	 */
	public static int getTaskComplete() {
		return TASK_COMPLETE;
	}
	/**
	 * @return location of this task
	 */
	public Location getLocation() {
		return location;
	}
	/**
	 * @param location location of this task
	 */
	public void setLocation(Location location) {
		this.location = location;
	}
}
