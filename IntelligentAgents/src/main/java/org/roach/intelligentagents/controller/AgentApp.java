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
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.strategy.AgentStrategy;
import org.roach.intelligentagents.model.strategy.Strategy;
import org.roach.intelligentagents.view.GUI;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * Singleton application controller
 * 
 * @author L. Stephen Roach
 */
public final class AgentApp {
	@SuppressWarnings("unused")
	private static void findAllStrategies() {
		ClassPathScanningCandidateComponentProvider scanner =
				new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Strategy.class));
		for (BeanDefinition bd : scanner.findCandidateComponents("org.roach")) {
			System.out.println(bd.getBeanClassName());
		}
	}

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
			System.out.println(e.getMessage());
			System.exit(1);
		}

		AgentApp aa = new AgentApp(options);
		Agent.initAgents(options.strategy, options.agents, aa.simgrid, options);
		Task.setNumTasks(options.tasks);
		GUI gui = new GUI(aa, options);
		aa.simgrid.addPropertyChangeListener(gui);
		if (aa.isBatchMode())
			gui.getAnimator().startSim();
	}

	/** Determines whether program runs in batch mode. */
	private boolean batchMode;
	/** The percentage of tasks that must be complete for the simulation to stop. */
	private int percentFinished;
	/** The roomSize of the sim-space. */
	private int roomSize;
	/** The simulation grid */
	private SimulationGrid simgrid;

	/** Determines agent type. */
	private Class<? extends AgentStrategy> strategyType;

	private AgentApp(AgentAppOpts opts) {
		roomSize = opts.roomsize;
		percentFinished = opts.stopat;
		batchMode = opts.batch;
		strategyType = opts.strategy;
		simgrid = SimulationGrid.getInstance(roomSize, opts.tasks);
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
	 * @return type of agent
	 */
	public Class<? extends AgentStrategy> getStrategyType() {
		return strategyType;
	}

	/**
	 * @return batch mode
	 */
	public boolean isBatchMode() {
		return batchMode;
	}

	/**
	 * Getter for 
	 * @return the simgrid
	 */
	public SimulationGrid getSimgrid() {
		return simgrid;
	}
}
