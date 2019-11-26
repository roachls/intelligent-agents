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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.PropertyConstants;

/**
 * The Task class represents an executable task within the grid-space.
 * 
 * @author L. Stephen Roach
 */
public final class Task implements ISimItem {
	/**
	 * A task has this percentage probability of being bumped "up" in completeness
	 * when executed.
	 */
	private static final float TASK_COMPLETE_PROBABILITY = 1.0f;
	/** Constant indicating task completeness. */
	private static final int TASK_COMPLETE = 5;
	/** Location of the task within the sim-space. */
	@NonNull
	private final Location location;
	/** Current priority of the task. */
	private int prio;

	private PropertyChangeSupport mPcs = new PropertyChangeSupport(this);

	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(@NonNull final PropertyChangeListener listener) {
		mPcs.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(@NonNull final PropertyChangeListener listener) {
		mPcs.removePropertyChangeListener(listener);
	}

	/**
	 * @param inputLoc The location to place the task
	 */
	public Task(@NonNull final Location inputLoc) {
		location = inputLoc;
	}

	/**
	 * Gets the current priority of the task.
	 * 
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
				mPcs.firePropertyChange(PropertyConstants.TASK_EXECUTE, prio - 1, prio);
			}
		} else // if task is already complete
			return;
	}

	/**
	 * Return completeness of the task.
	 * 
	 * @return True if task is complete, False otherwise
	 */
	public boolean isComplete() {
		synchronized (this) {
			return prio == getTaskComplete();
		}
	}

	/**
	 * Gets the hash code of the task.
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		return location.hashCode();
	}

	/**
	 * Determines if this task equals another task. Note that initTaskGrid ensures
	 * no two tasks are in the same location, so this method is safe.
	 * 
	 * @param o The Task to be compared.
	 * @return True if the two tasks have the same location, false if not or if o is
	 *         null
	 */
	@Override
	public boolean equals(final @Nullable Object o) {
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
	 * 
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
	@NonNull
	public Location getLocation() {
		return location;
	}
}
