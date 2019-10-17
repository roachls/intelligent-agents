package org.roach.intelligentagents.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.model.strategy.AgentStrategy;
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
	private ArrayList<Task> taskList = new ArrayList<>();
	/** Number of tasks */
	private final int numTasks;
	/** Number of "complete" tasks */
	private volatile int numTasksComplete = 0;
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

	private static SimulationGrid instance;

	@SuppressWarnings("null")
	@NonNull
	public static SimulationGrid getInstance(@NonNull final AgentAppOpts options) {
		if (instance == null) {
			instance = new SimulationGrid(options);
		}
		return instance;
	}

	/**
	 * @param gridSize
	 */
	private SimulationGrid(@NonNull final AgentAppOpts options) {
		this.gridSize = options.roomsize;
		this.numTasks = options.tasks;
		grid = new ArrayList<>(gridSize);
		for (int x = 0; x < gridSize; x++) {
			List<Set<Agent>> row = new ArrayList<>(gridSize);
			for (int y = 0; y < gridSize; y++) {
				row.add(new HashSet<Agent>());
			}
			grid.add(row);
		}
		xRef = new HashMap<>();
		initTaskGrid(options);
		initAgents(options);
	}

	@Override
	public void propertyChange(@Nullable PropertyChangeEvent evt) {
		if (evt == null)
			return;
		String message = evt.getPropertyName();
		Agent sender = (Agent) evt.getSource();
		if (sender == null)
			return;
		Location newLocation = (Location) evt.getNewValue();
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
			System.err.println("Unsupported message type: " + evt.getPropertyName());
		}
	}

	/**
	 * Used for record-keeping; adds the agent to the list of agents in the xRef
	 * data structure.
	 * 
	 * @param a The agent to be added
	 */
	private void addAgentToXref(@NonNull final Agent a) {
		Integer X = a.getLoc().getX();
		// If the HashSet entry doesn't exist, create it
		if (!xRef.containsKey(X)) {
			xRef.put(X, new HashSet<Integer>());
		}
		// Add the y-coordinate to the set
		xRef.get(X).add(a.getLoc().getY());
	}

	/**
	 * Used for record-keeping; removes the agent from the list of agents in the
	 * xRef data structure.
	 * 
	 * @param a The agent to be removed
	 */
	private void removeAgentFromXref(@NonNull final Agent a) {
		// Remove value from set
		xRef.get(a.getLoc().getX()).remove(a.getLoc().getY());
	}

	/**
	 * @param a The agent to be added
	 */
	private void addAgentToCell(@NonNull final Agent a) {
		grid.get(a.getLoc().getX()).get(a.getLoc().getY()).add(a);
	}

	/**
	 * Used for record-keeping; removes the agent from the list of agents in the
	 * agentGrid data structure.
	 * 
	 * @param a The agent to be removed
	 */
	private void removeAgentFromCell(@NonNull final Agent a) {
		grid.get(a.getLoc().getX()).get(a.getLoc().getY()).remove(a);
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
		Integer startCheckX = (loc.getX() > distance) ? (loc.getX() - distance) : 0;
		Integer endCheckX = ((loc.getX() + distance) < (gridSize - 1)) ? (loc.getX() + distance) : (gridSize - 1);

		// Go through each X value within the boundaries
		for (Integer X = startCheckX; X <= endCheckX; X++) {
			if (xRef.containsKey(X)) {
				// If there are any agents on this row...
				for (Integer Y : xRef.get(X)) {
					// For each Y-coordinate on the row
					@SuppressWarnings("null")
					Location checkLoc = new Location(X, Y);
					if (!loc.equals(checkLoc)) {
						// Make sure loc != checkLoc (i.e., agent doesn't
						// communicate with itself)
						if (loc.isInCircle(checkLoc, commDistSq)) // If the location is within broadcast range, add
							// all agents in that location to the list
							list.addAll(grid.get(checkLoc.getX()).get(checkLoc.getY()));
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

	/**
	 * Initializes the task grid and task list
	 * 
	 * @param numTasks Number of tasks to create
	 * @param size     Size of the playing field
	 * @param pcl
	 */
	private void initTaskGrid(@NonNull final AgentAppOpts options) {
		taskGrid = new Task[options.roomsize][options.roomsize];
		if (!taskList.isEmpty()) {
			taskList.clear();

		}
		for (int whichTask = 0; whichTask < options.tasks; whichTask++) {
			boolean taskPlaced = false;
			Random rand = new Random();
			while (!taskPlaced) { // Keep picking random locations until an unused
				// square is found.
				int x = rand.nextInt(options.roomsize); // pick a random x
				int y = rand.nextInt(options.roomsize); // pick a random y
				Location tempLoc = new Location(x, y);
				if (!isTask(tempLoc)) { // If no task exists there
					taskPlaced = true; // Set to exit the while-loop
					Task newTask = new Task(tempLoc);
					taskList.add(newTask);
					taskGrid[x][y] = newTask;
				}
			}
		}
		numTasksComplete = 0;
	}

	/**
	 * Initialize/reset/create all agents and grids
	 * 
	 * @param strategyType
	 * @param numAgents    Number of agents to create
	 * @param simGrid
	 * @param options
	 */
	private void initAgents(@NonNull final AgentAppOpts options) {
		agents = new ArrayList<>();

		for (int i = 0; i < options.agents; i++) {
			@Nullable
			AgentStrategy strategy = null;
			try {
				if (options.strategy != null) {
					Constructor<? extends AgentStrategy> strategyConst = options.strategy.getConstructor(Agent.class,
							SimulationGrid.class);
					strategy = strategyConst.newInstance((Agent) null, this);
				}
				if (strategy != null) {
					Agent a = new Agent(strategy, this);
					agents.add(a);
					strategy.setAgent(a);
					strategy.setOptions(options);
					a.addPropertyChangeListener(this);
					a.firePropertyChange(PropertyConstants.NEW_AGENT, null, null);
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
	 * Executes the task at the given location.
	 * 
	 * @param loc The location of the task to execute
	 */
	public void executeTaskAt(@NonNull final Location loc) {
		if (isTask(loc)) {
			Task task = taskGrid[loc.getX()][loc.getY()];
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
		return (taskGrid[loc.getX()][loc.getY()] != null);
	}

	/**
	 * Returns the task at the given location, or null if no task exists.
	 * 
	 * @param loc The location of the task
	 * @return The task at the given location, or null
	 */
	@Nullable
	public Task getTask(@NonNull final Location loc) {
		return taskGrid[loc.getX()][loc.getY()];
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
	public ArrayList<Task> getTaskList() {
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