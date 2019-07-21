/*
 * AgentApp.java
 *
 * Created on December 1, 2006, 5:15 PM
 * @author L. Stephen Roach
 * @version 2.1 November 22, 2007
 * Added support for Properties.
 */
package org.roach.intelligentagents.controller;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.model.strategy.AgentStrategy;
import org.roach.intelligentagents.view.GUI;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Singleton application controller
 * 
 * @author L. Stephen Roach
 */
public final class AgentApp {
	/**
	 * The main() method. Parses command-line arguments (if any) and creates a new
	 * AgentApp object, which takes over control.
	 * 
	 * @param args Command-line arguments (if any)
	 */
	public static void main(final String[] args) {
		AgentAppOpts options = new AgentAppOpts();
		JCommander jCommander = JCommander.newBuilder().addObject(options).build();
		try {
			jCommander.parse(args);
		} catch (ParameterException e) {
			jCommander.usage();
			throw e;
		}

		AgentApp aa = new AgentApp(options);
		Location.setGridSize(options.roomsize);
		Agent.initAgents(options.strategy, aa.getNumAgents(), aa.simgrid, options);
		GUI gui = new GUI(aa, options);
		if (aa.isBatchMode())
			gui.getAnimator().startSim();
	}

	/** Determines agent type. */
	private Class<? extends AgentStrategy> strategyType;
	/** Determines whether program runs in batch mode. */
	private boolean batchMode;
	/** The number of agents. */
	private int numAgents;
	/** The number of tasks. */
	private int numTasks;
	/** The percentage of tasks that must be complete for the simulation to stop. */
	private int percentFinished;
	/** The roomSize of the sim-space. */
	private int roomSize;

	/** The simulation grid */
	private SimulationGrid simgrid;

	private AgentApp(AgentAppOpts opts) {
		roomSize = opts.roomsize;
		numAgents = opts.agents;
		numTasks = opts.tasks;
		percentFinished = opts.stopat;
		batchMode = opts.batch;
		strategyType = opts.strategy;
		simgrid = new SimulationGrid(roomSize);
	}

	/**
	 * @return type of agent
	 */
	public Class<? extends AgentStrategy> getStrategyType() {
		return strategyType;
	}

	/**
	 * @return number of agents
	 */
	public int getNumAgents() {
		return numAgents;
	}

	/**
	 * @return number of tasks
	 */
	public int getNumTasks() {
		return numTasks;
	}

	/**
	 * @return % finished
	 */
	public int getPercentFinished() {
		return percentFinished;
	}

	/**
	 * @return size of room
	 */
	public int getRoomSize() {
		return roomSize;
	}

	/**
	 * @return batch mode
	 */
	public boolean isBatchMode() {
		return batchMode;
	}
}
