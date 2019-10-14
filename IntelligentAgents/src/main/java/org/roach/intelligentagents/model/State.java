package org.roach.intelligentagents.model;

import java.awt.Color;

import org.eclipse.jdt.annotation.Nullable;


/**
 * @author Larry S. Roach
 *
 */
public class State {
	private Agent agent;
    /** The algorithm that is performed in the State */
    private StateAlgorithm algorithm;
    /** The Color of the state, used by the Agent.draw method */
	@Nullable private Color color = Color.BLACK;
    
    public State() {
	}

	/** 
     * @param c The color that the Agent in this State will be displayed in.
     * @param alg The algorithm called by this state.
     * @param agent 
     */
    public State(@Nullable final Color c, StateAlgorithm alg, Agent agent) {
        this.color = c;
        this.algorithm = alg;
        this.agent = agent;
    }
	
	/**
     * Calls the Agent's public method.
     */
    public void doAction() {
        algorithm.go(agent);
    }

	/**
     * Get the color for this state.
     * @return color
     */
    @Nullable
    public Color getColor() {
        return color;
    }

    /**
	 * Setter for 
	 * @param agent the agent to set
	 */
	public void setAgent(Agent agent) {
		this.agent = agent;
	}

    /**
	 * Setter for 
	 * @param algorithm the algorithm to set
	 */
	public void setAlgorithm(StateAlgorithm algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * Setter for 
	 * @param color the color to set
	 */
	public void setColor(@Nullable final Color color) {
		if (color != null)
			this.color = color;
	}
}
