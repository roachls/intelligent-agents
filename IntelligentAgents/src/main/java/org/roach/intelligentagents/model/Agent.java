/*
 * Agent.java
 *
 * Created on April 28, 2007, 6:36 PM
 */

package org.roach.intelligentagents.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.strategy.AgentStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An Agent is the SuperClass for all agents. It contains methods and properties
 * used by all agents, such as state representation, randomMove, doAction, etc.
 *
 * @author L. Stephen Roach
 * @version %I%, %G%
 */
public class Agent {
	private static Logger logger = LoggerFactory.getLogger(Agent.class);

	/** The list of all agents */
	private static List<Agent> agents;

	private static int idRoot = 0;

	/**
	 * Get the list of all agents
	 *
	 * @return Agents
	 */
	public static List<Agent> getAgents() {
		return agents;
	}

	/**
	 * Initialize/reset/create all agents and grids
	 *
	 * @param strategyType
	 * @param numAgents
	 *            Number of agents to create
	 * @param simGrid
	 * @param options
	 */
	public static void initAgents(final Class<? extends AgentStrategy> strategyType, final int numAgents, PropertyChangeListener simGrid, AgentAppOpts options) {
		agents = new ArrayList<>();

		for (int i = 0; i < numAgents; i++) {
			Agent a;
			AgentStrategy strategy;
			try {
				a = new Agent();
				Constructor<? extends AgentStrategy> strategyConst = strategyType.getConstructor(Agent.class);
				strategy = strategyConst.newInstance(a);
				strategy.setOptions(options);
				a.setStrategy(strategy);
				a.addPropertyChangeListener(simGrid);
				a.mPcs.firePropertyChange("new_agent", null, null);
				agents.add(a);
			} catch (Exception ex) {
				logger.error("Unable to instantiate class.", ex);
				System.exit(2);
			}
		}
	}

	/**
	 * A set of locations of tasks that the agent has already executed; used to
	 * prevent an agent from executing a task more than once.
	 */
	private Set<Task> executedTasks;
	/** A unique identifier for each Agent. */
	private int id;

	/** Location of the agent within the sim-space. */
	private Location loc;

	// Setup property-change support
	private PropertyChangeSupport mPcs = new PropertyChangeSupport(this);

	protected AgentStrategy strategy;

	/**
	 * Creates a new instance of Agent.
	 */
	public Agent() {
		id = idRoot++;
		loc = Location.getRandomLocation();
		executedTasks = new HashSet<>();
	}

	/**
	 * Setter for
	 * @param strategy the strategy to set
	 */
	public void setStrategy(AgentStrategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * @param listener
	 */
	private void addPropertyChangeListener(PropertyChangeListener listener) {
		mPcs.addPropertyChangeListener(listener);
	}

	/**
	 * Determine if this Agent is equal to the Agent o.
	 *
	 * @param o
	 *            The Agent to be compared
	 * @return True if the Agent IDs are the same
	 */
	@Override
	public boolean equals(final Object o) {
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
		executedTasks.add(Task.getTask(loc));
	}

	/**
	 * Checks whether the agent has found a new task. A new task is a task that a)
	 * exists, b) is not complete, and c) has not been executed before by this
	 * agent.
	 *
	 * @return True if new task found
	 */
	public boolean foundNewTask() {
		return (Task.isTask(loc) && Task.isTaskInProgress(loc)) && !hasDoneAlready(Task.getTask(loc));
	}

	/**
	 * Getter for
	 * @return the executedTasks
	 */
	public Set<Task> getExecutedTasks() {
		return executedTasks;
	}

	/**
	 * Getter for
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
	public Location getLoc() {
		return loc;
	}

	/**
	 * Getter for
	 *
	 * @return the mPcs
	 */
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
	 * @param t
	 *            The task to check
	 * @return True if already-executed list contains the task, False otherwise
	 */
	public boolean hasDoneAlready(Task t) {
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
		return this.getClass().toString() + ", location: " + loc;
	}

	/**
	 * Moves the agent towards the given location
	 * @param location
	 */
	public void moveTowards(Location location) {
		loc.moveTowards(location);
	}

}
