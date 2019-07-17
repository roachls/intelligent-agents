package org.roach.intelligentagents.model;

/**
 * Interface StateAlgorithm
 * Uses the Strategy pattern to simplify adding new functionality. The action
 * done by each agent in each state is delegated to the State, which then calls
 * the appropriate function in the Agent. This allows adding new states more
 * easily.
 * A StateAlgorithm contains one method, go(), which calls back to the Agent's
 * doXXXStateActions method.
 * @author L. Stephen Roach
 */
@FunctionalInterface
public interface StateAlgorithm {
	/**
	 * @param agent
	 */
	void go(Agent agent);
}
