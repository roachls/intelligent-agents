package org.roach.intelligentagents.model;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy;

/**
 * @author Larry S. Roach
 *
 */
public class SimulationGrid implements PropertyChangeListener {
	private Set<Agent>[][] grid;
	private int gridSize;
    /**
     * A table of sets of agents. This data structure is used to greatly speed
     * up the "communications" between agents. Each table entry represents a row
     * of the grid-space. Each entry in a set represents a column value. This
     * allows an agent to search the space "nearby" in less than O(n^2) time, by
     * essentially allowing the algorithm to skip blank rows and columns.
     */
    private Map<Integer, HashSet<Integer>> xRef;
    
	private class AgentList extends HashSet<Agent> {
		/**
		 * serial Version UID
		 */
		private static final long serialVersionUID = -3358985673880477446L;
	}
	
	/**
	 * @param gridSize
	 */
	public SimulationGrid(int gridSize) {
		this.gridSize = gridSize;
		grid = new AgentList[gridSize][gridSize];
		xRef = new HashMap<Integer, HashSet<Integer>>();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		String message = evt.getPropertyName();
		Agent sender = (Agent)evt.getSource();
		if (message.equals("new_agent")) {
	        addAgentToCell(sender);
	        addAgentToXref(sender);
		} else if (message.equals("send_message")) {
		    Set<Agent> list = getNearbyAgents(sender.getLoc(), ((CommunicatingAgentStrategy)sender.getStrategy()).getCommDist());
		    for (Agent receiver : list) { // For each agent in the list
		        // Send the agent the message
		        ((CommunicatingAgentStrategy)((Agent) receiver).getStrategy()).receiveMessage((Location)evt.getNewValue());
		    }
		} else if (message.equals("prepare_to_act")) {
			removeAgentFromCell(sender);
			removeAgentFromXref(sender);
		} else if (message.equals("update_grid")) {
	        addAgentToCell(sender);
	        addAgentToXref(sender);
		} else {
			System.err.println("Unsupported message type: " + evt.getPropertyName());
		}
	}
	
    /**
     * Used for record-keeping; adds the agent to the list of agents in
     * the xRef data structure.
     * @param a The agent to be added
     */
    private void addAgentToXref(Agent a) {
        Integer X = a.getLoc().getX();
        // If the HashSet entry doesn't exist, create it
        if (!xRef.containsKey(X)) {
            xRef.put(X, new HashSet<Integer>());
        }
        // Add the y-coordinate to the set
        xRef.get(X).add(a.getLoc().getY());
    }
    
    /**
     * Used for record-keeping; removes the agent from the list of agents in
     * the xRef data structure.
     * @param a The agent to be removed
     */
    private void removeAgentFromXref(Agent a) {
        // Remove value from set
        xRef.get(a.getLoc().getX()).remove(a.getLoc().getY());
    }

    /**
     * @param a The agent to be added
     */
    private void addAgentToCell(Agent a) {
    	if (grid[a.getLoc().getX()][a.getLoc().getY()] == null) {
    		grid[a.getLoc().getX()][a.getLoc().getY()] = new AgentList();
    	}
    	// Add the agent to the set
    	grid[a.getLoc().getX()][a.getLoc().getY()].add(a);
    }
    
    /**
     * Used for record-keeping; removes the agent from the list of agents in
     * the agentGrid data structure.
     * @param a The agent to be removed
     */
    private void removeAgentFromCell(Agent a) {
        grid[a.getLoc().getX()][a.getLoc().getY()].remove(a);
    }
    
    /**
     * Get a list of agents in broadcast range.
     * @return List of agents
     * @param loc The location at the center of the search area
     * @param distance The radius to search around the location
     */
    public Set<Agent> getNearbyAgents(final Location loc,
            final int distance) {
        int commDistSq = distance * distance;
        // Initialize the list to return
        Set<Agent> list = new HashSet<Agent>();
        // Calculate the rows to check
        Integer startCheckX = (loc.getX() > distance)
                ? (loc.getX() - distance) : 0;
        Integer endCheckX = ((loc.getX() + distance) < (gridSize - 1))
                ? (loc.getX() + distance) : (gridSize - 1);

        // Go through each X value within the boundaries
        for (Integer X = startCheckX; X <= endCheckX; X++) {
            if (xRef.containsKey(X)) {
                // If there are any agents on this row...
                for (Integer Y : xRef.get(X)) {
                    // For each Y-coordinate on the row
                    Location checkLoc = new Location(X, Y);
                    if (!loc.equals(checkLoc)) {
                        // Make sure loc != checkLoc (i.e., agent doesn't
                        // communicate with itself)
                        if (loc.isInCircle(checkLoc, commDistSq)) // If the location is within broadcast range, add
                        // all agents in that location to the list
                            list.addAll(grid[checkLoc.getX()][checkLoc.getY()]);
                    }
                }
            }
        }
        return list;
    }
}
