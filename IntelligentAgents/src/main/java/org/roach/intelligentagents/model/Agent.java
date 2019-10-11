/*
 * Agent.java
 *
 * Created on April 28, 2007, 6:36 PM
 */

package org.roach.intelligentagents.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.model.strategy.AgentStrategy;

/**
 * An Agent is the SuperClass for all agents. It contains methods and properties
 * used by all agents, such as state representation, randomMove, doAction, etc.
 * 
 * @author L. Stephen Roach
 * @version %I%, %G%
 */
public class Agent implements ISimItem {
	/** The list of all agents */
	@NonNull private static List<Agent> agents = new ArrayList<>();

	private static int id_root = 0;

	/**
	 * A set of locations of tasks that the agent has already executed; used to
	 * prevent an agent from executing a task more than once.
	 */
	protected Set<Task> executedTasks;
	/** A unique identifier for each Agent. */
	protected int id;

	/** Location of the agent within the sim-space. */
	@NonNull protected Location loc;

	// Setup property-change support
	@NonNull private final PropertyChangeSupport mPcs = new PropertyChangeSupport(this);

	protected final AgentStrategy strategy;

	/**
	 * Creates a new instance of Agent.
	 */
	public Agent(final AgentStrategy strategy) {
		this.strategy = strategy;
		id = id_root++;
		loc = Location.getRandomLocation();
		executedTasks = new HashSet<>();
	}

	/**
	 * Get the list of all agents
	 * 
	 * @return Agents
	 */
	public static List<Agent> getAgents() {
		return agents;
	}

	public static int getNumAgents() {
		return agents.size();
	}

	/**
	 * Initialize/reset/create all agents and grids
	 * 
	 * @param strategyType
	 * @param numAgents    Number of agents to create
	 * @param simGrid
	 * @param options
	 */
	public static void initAgents(final Class<? extends AgentStrategy> strategyType, final int numAgents,
			PropertyChangeListener simGrid, AgentAppOpts options) {
		agents = new ArrayList<>();

		for (int i = 0; i < numAgents; i++) {
			@Nullable
			AgentStrategy strategy = null;
			try {
				Constructor<? extends AgentStrategy> strategyConst = strategyType.getConstructor(Agent.class);
				strategy = strategyConst.newInstance((Agent)null);
				if (strategy != null) {
					Agent a = new Agent(strategy);
					agents.add(a);
					strategy.setAgent(a);
					strategy.setOptions(options);
					a.addPropertyChangeListener(simGrid);
					a.mPcs.firePropertyChange(PropertyConstants.NEW_AGENT, null, null);
				}
			} catch (InstantiationException ex) {
				System.err.println("Unable to instantiate class.");
				ex.printStackTrace();
				System.exit(2);
			} catch (IllegalAccessException ex) {
				System.err.println("Illegal access exception.");
				ex.printStackTrace();
				System.exit(3);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				System.exit(4);
			} catch (SecurityException e) {
				e.printStackTrace();
				System.exit(5);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				System.exit(6);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				System.exit(7);
			}
		}
	}

	/**
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		mPcs.addPropertyChangeListener(listener);
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
		Task.executeTaskAt(loc);
		Task gotTask = Task.getTask(loc);
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
		return (Task.isTask(loc) && !Task.isTaskComplete(loc)) && !hasDoneAlready(Task.getTask(loc));
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
	@NonNull public PropertyChangeSupport getmPcs() {
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
	 * Moves the agent towards the given location
	 * 
	 * @param location
	 */
	public void moveTowards(Location location) {
		loc = loc.moveTowards(location);
	}

	/**
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		mPcs.removePropertyChangeListener(listener);
	}

	/**
	 * Gets the String representation of this agent.
	 * 
	 * @return String representation including type of agent, location, and index
	 */
	@Override
	public String toString() {
		return this.getClass().toString() + ", location: " + loc;
	}

	/**
	 * Setter for
	 * 
	 * @param loc the loc to set
	 */
	public void setLoc(@NonNull final Location loc) {
		this.loc = loc;
	}

}
