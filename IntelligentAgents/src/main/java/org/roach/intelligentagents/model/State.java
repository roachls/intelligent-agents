package org.roach.intelligentagents.model;

import java.awt.Color;


/**
 * @author Larry S. Roach
 *
 */
public class State {
	
    /**
	 * Setter for 
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(StateAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	/** The Color of the state, used by the Agent.draw method */
    private final Color color;
    /** The algorithm that is performed in the State */
    private StateAlgorithm algorithm;
    private Agent agent;
    
    /**
	 * Setter for 
	 * @param agent the agent to set
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	/** 
     * @param c The color that the Agent in this State will be displayed in.
     * @param alg The algorithm called by this state.
     * @param agent 
     */
    public State(Color c, StateAlgorithm alg, Agent agent) {
        this.color = c;
        this.algorithm = alg;
        this.agent = agent;
    }

	/** Initial state */
	public final static State NOT_SET = new State(Color.black, incoming -> {}, null);

    /**
     * Get the color for this state.
     * @return color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Calls the Agent's public method.
     */
    public void doAction() {
        algorithm.go(agent);
    }
}
