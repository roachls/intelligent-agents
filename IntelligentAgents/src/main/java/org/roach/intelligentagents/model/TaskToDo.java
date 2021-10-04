package org.roach.intelligentagents.model;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.view.AAnimator;

/**
 * A TaskToDo object represents a single task for an agent to perform in GOTO
 * mode. It implements the Comparable interface to allow for use in a priority
 * queue.
 */
public final class TaskToDo implements Comparable<TaskToDo> {
    /** The Location of the task. */
    @NonNull
    private final Location taskLoc;
    /** The time the task was found */
    private final int timeWhenFound;

    /**
     * Creates a new instance of a TaskToDo.
     * 
     * @param inputTaskLoc Location of the new task
     */
    public TaskToDo(@NonNull final Location inputTaskLoc) {
	taskLoc = inputTaskLoc;
	this.timeWhenFound = AAnimator.getTime();
    }

    /**
     * Get the location of the TaskToDo.
     * 
     * @return A location
     */
    @NonNull
    public Location getLocation() {
	return taskLoc;
    }

    /**
     * Compares two TaskToDo objects by time when they were found; required by the
     * Comparable interface.
     * 
     * @param rhs The TaskToDo to be compared
     * @return difference between time temp was found and time this was found
     */
    @Override
    public int compareTo(final TaskToDo rhs) {
	return rhs.timeWhenFound - timeWhenFound;
    }

    /**
     * Get a String representation of this TaskToDo.
     * 
     * @return String representation
     */
    @Override
    public String toString() {
	return "TaskToDo: " + taskLoc + ", time: " + timeWhenFound;
    }
}
