/*
 * AgentApp.java
 *
 * Created on December 1, 2006, 5:15 PM
 * @author L. Stephen Roach
 * @version 2.1 November 22, 2007
 * Added support for Properties.
 */
package org.roach.intelligentagents.controller;

import javax.swing.SwingUtilities;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.view.console.ConsoleAnimator;
import org.roach.intelligentagents.view.swing.ConfigurationDialog;
import org.roach.intelligentagents.view.swing.GUI;

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
	 * @throws ClassNotFoundException
	 */
	public static void main(final String[] args) throws ClassNotFoundException {
		AgentAppOpts options = new AgentAppOpts();
		JCommander jCommander = JCommander.newBuilder().addObject(options).build();
		try {
			jCommander.parse(args);
		} catch (ParameterException e) {
			jCommander.usage();
			System.out.println(e.getMessage());
			System.exit(1);
		}

		if (options.batch) {
			if (options.strategy != null) {
				AgentApp aa = new AgentApp(options);
				new ConsoleAnimator(aa).startSim();
			} else {
				System.err.println("You must specify a strategy in batch mode!");
				jCommander.usage();
				System.exit(1);
			}
		} else {
			if (options.strategy == null) {
				SwingUtilities.invokeLater(() -> new ConfigurationDialog(null, true, options).setVisible(true));
				while (options.strategy == null) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			AgentApp aa = new AgentApp(options);
			GUI gui = new GUI(aa, options);
			aa.simgrid.addPropertyChangeListener(gui);
		}
	}

	/** The percentage of tasks that must be complete for the simulation to stop. */
	private final int percentFinished;
	/** The simulation grid */
	@NonNull
	private SimulationGrid simgrid;

	/** Determines agent type. */
	private final String strategyType;

	private AgentApp(AgentAppOpts opts) {
		percentFinished = opts.stopat;
		strategyType = opts.strategy.getSimpleName();
		simgrid = SimulationGrid.getInstance(opts);
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
		return strategyType;
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
