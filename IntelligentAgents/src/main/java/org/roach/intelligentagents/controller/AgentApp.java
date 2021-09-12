/*
 * AgentApp.java
 *
 * Created on December 1, 2006, 5:15 PM
 * @author L. Stephen Roach
 * @version 2.1 November 22, 2007
 * Added support for Properties.
 */
package org.roach.intelligentagents.controller;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.model.strategy.AgentStrategy;

/**
 * Singleton application controller
 * 
 * @author L. Stephen Roach
 */
public final class AgentApp {

    /** The percentage of tasks that must be complete for the simulation to stop. */
    private int percentFinished;

    public void setPercentFinished(int percentFinished) {
	this.percentFinished = percentFinished;
    }

    /** The simulation grid */
    @NonNull
    private SimulationGrid simgrid;

    /** Determines agent type. */
    private Class<? extends AgentStrategy> strategyType;

    public void setStrategyType(Class<? extends AgentStrategy> strategyType) {
	this.strategyType = strategyType;
    }

    public void setSimgrid(SimulationGrid simgrid) {
	this.simgrid = simgrid;
    }

    /**
     * @return % finished
     */
    public int getPercentFinished() {
	return percentFinished;
    }

    /**
     * @return type of agent
     */
    public String getStrategyType() {
	return strategyType.getName();
    }

    /**
     * Getter for
     * 
     * @return the simgrid
     */
    public SimulationGrid getSimgrid() {
	return simgrid;
    }

}
