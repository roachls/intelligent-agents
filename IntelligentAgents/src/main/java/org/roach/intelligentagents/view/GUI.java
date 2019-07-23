package org.roach.intelligentagents.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.controller.AgentApp;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy;

/**
 * @author Larry S. Roach
 *
 */
public class GUI implements WindowListener, PropertyChangeListener {
    private JPanel mainPanel;
	private Animator animator;

	/**
	 * @return animator
	 */
	public Animator getAnimator() {
		return animator;
	}

	/** Text field in which the current sim-time is displayed. */
	private JTextField jtfTime;
    /** The "startPauseBtn" button. */
	private JButton startPauseBtn;
    /** The "render" checkbox. */
	private JCheckBox toggleRender;
	/** The "show agents" checkbox. */
	private JCheckBox toggleAgents;
	/** The "show helper graphics" checkbox. */
	private JCheckBox toggleHelper;
	/** The "stopBtn" button. */
	private JButton stopBtn;
	/** The "step" button */
	private JButton stepBtn;
	/** The progress bar. */
	private JProgressBar progressBar;
	/** The parent application */
	private AgentApp agentapp;

	/**
	 * Creates the GUI environment for the program.
	 * 
	 * @param agentApp - the app to listen to
	 * @param options - the options to use while rendering
	 */
	public GUI(AgentApp agentApp, AgentAppOpts options) {
		this.agentapp = agentApp;
        // The size of a cell in pixels
        int cellSize = options.cellSize;
        //The size of the playing field in pixels
        int mainPanelSize = cellSize * agentApp.getRoomSize() + 2;
        // Determines whether helper graphics are displayed. *
        boolean showHelperGraphics = options.showHelper;
        // Determines whether graphics are rendered.
        boolean showGraphics = options.showGraphics;
		if (agentApp.isBatchMode()) {
			showHelperGraphics = false;
			showGraphics = false;
		}
		ViewableTask.setSquareSize(cellSize);
		ViewableAgent.setSquareSize(cellSize);

		JFrame masterFrame = new JFrame();
        masterFrame.setTitle("Intelligent Agent Simulation");
        masterFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container c = masterFrame.getContentPane(); // default BorderLayout used
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.white);
		mainPanel.setPreferredSize(new Dimension(mainPanelSize, mainPanelSize));
		mainPanel.addPropertyChangeListener(this);
		animator = new Animator(mainPanel);

		// Add bottom portion of screen
		JPanel bottom = new JPanel();
		bottom.setLayout(new BorderLayout());

		// Add stats section at bottom of screen
		JPanel stats = new JPanel(); // a row of textfields
		stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));

		// Make the text-box with the size of the field and number of agents
		JTextField jtfAgents = new JTextField("Size: " + agentApp.getRoomSize() + " Agents: " + agentApp.getNumAgents()
				+ " Tasks: " + agentApp.getNumTasks() + " Type: " + agentApp.getStrategyType().getSimpleName());
		jtfAgents.setEditable(false);
		stats.add(jtfAgents);

		// Make the text-box that shows the current sim-time
		jtfTime = new JTextField("Cycles: 0");
		jtfTime.setEditable(false);
		stats.add(jtfTime);

		// Add the stats to the bottom of the window
		bottom.add(stats, BorderLayout.NORTH);
		c.add(bottom, BorderLayout.SOUTH);

		toggleAgents = new JCheckBox("Show Agents", true);
		toggleAgents.setMnemonic(KeyEvent.VK_A);
		toggleAgents.setToolTipText(
				"Choose whether to display agents and " + "associated graphics. Only tasks will be animated.");
		toggleAgents.addItemListener(e -> {
			animator.setShowAgents(e.getStateChange() == ItemEvent.SELECTED);
			toggleHelper.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
		});

		toggleHelper = new JCheckBox("Show Helper Graphics", true);
		toggleHelper.setSelected(showHelperGraphics);
		toggleHelper.setMnemonic(KeyEvent.VK_H);
		toggleHelper.setToolTipText("Choose whether to display communication-" + "range circles and goto lines.");
		toggleHelper.addItemListener(e -> animator.setHelperGraphics(e.getStateChange() == ItemEvent.SELECTED));

		toggleRender = new JCheckBox("Render", true);
		toggleRender.setSelected(showGraphics);
		toggleRender.setMnemonic(KeyEvent.VK_R);
		toggleRender
				.setToolTipText("Choose whether to render graphics;" + " disabling will maximize processing speed.");
		toggleRender.addItemListener(e -> {
			animator.setRender(e.getStateChange() == ItemEvent.SELECTED);
			toggleAgents.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
			toggleHelper.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
		});

		// Add options
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridLayout(0, 1));
		optionsPanel.add(toggleAgents);
		optionsPanel.add(toggleHelper);
		optionsPanel.add(toggleRender);
		c.add(optionsPanel, BorderLayout.EAST);

		// Add playback buttons
		JPanel playbackCtlPanel = new JPanel();

        /* The rewind button */ /** The rewind button */JButton rewindBtn = new JButton();
		rewindBtn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Rewind24.gif"), "start/pause icon"));
		rewindBtn.setEnabled(false);
		rewindBtn.addActionListener(e -> {
			// TODO
		});

		startPauseBtn = new JButton();
		startPauseBtn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Play24.gif"), "start/pause icon"));
		startPauseBtn.addActionListener(e -> {
			if (animator.isStarted()) {
				if (startPauseBtn.isEnabled()) {
					if (animator.isPaused()) {
						stepBtn.setEnabled(false);
						animator.unpause();
						startPauseBtn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Pause24.gif"), "start/pause icon"));
					} else {
						stepBtn.setEnabled(true);
						animator.pause();
						startPauseBtn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Play24.gif"), "start/pause icon"));
					}
				}
			} else {
				animator.startSim();
				startPauseBtn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Pause24.gif"), "start/pause icon"));
				stopBtn.setEnabled(true);
			}
		});

		stopBtn = new JButton();
		stopBtn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("toolbarButtonGraphics/media/Stop24.gif"), "stop icon"));
		stopBtn.setEnabled(false);
		stopBtn.addActionListener(e -> {
			animator.stopSim();
			toggleRender.setEnabled(false);
			toggleAgents.setEnabled(false);
			toggleHelper.setEnabled(false);
			startPauseBtn.setEnabled(false);
			stepBtn.setEnabled(false);
		});

		stepBtn = new JButton();
		stepBtn.setIcon(new ImageIcon(getClass().getClassLoader().getResource("toolbarButtonGraphics/media/StepForward24.gif"), "step icon"));
		stepBtn.setEnabled(false);
		stepBtn.addActionListener(e -> {
			animator.step();
		});

		playbackCtlPanel.add(rewindBtn);
		playbackCtlPanel.add(startPauseBtn);
		playbackCtlPanel.add(stepBtn);
		playbackCtlPanel.add(stopBtn);
		bottom.add(playbackCtlPanel, BorderLayout.CENTER);

		progressBar = new JProgressBar(0, agentApp.getNumTasks());
		progressBar.setStringPainted(true);
		bottom.add(progressBar, "South");

		animator.setHelperGraphics(showHelperGraphics);
		animator.setRender(showGraphics);

        /* Allows the Animator to scroll within the window. */ /** Allows the Animator to scroll within the window. */
        JScrollPane scrollpane = new JScrollPane(mainPanel);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		c.add(scrollpane, "Center");
		Task.initTaskGrid(agentapp.getNumTasks(), agentapp.getRoomSize(), this);
		for (Task t : Task.getTaskList()) {
			ViewableTask vt = new ViewableTask(t);
			t.addPropertyChangeListener(vt);
			mainPanel.add(vt);
		}
		initAgents();
		masterFrame.pack(); // Makes the main window just the right size to hold everything
		masterFrame.setResizable(true);
		masterFrame.setVisible(!agentApp.isBatchMode()); // Make the window visible
	}

	/**
	 * Initialize all agents and add them to the panel for display.
	 */
	private void initAgents() {
		for (Agent a : Agent.getAgents()) {
			if (a.getStrategy() instanceof CommunicatingAgentStrategy)
				mainPanel.add(new CommunicatingViewableAgent(a));
			else
				mainPanel.add(new ViewableAgent(a));
		}
	}

	/* WindowListener Methods */
	/**
	 * Called whenever the window receives focus; not implemented
	 * 
	 * @param e
	 *            The activation event
	 */
	@Override
	public void windowActivated(final WindowEvent e) {
        // intentionally blank
	}

	/**
	 * Called whenever the window loses focus; not implemented
	 * 
	 * @param e
	 *            The window deactivation event
	 */
	@Override
	public void windowDeactivated(final WindowEvent e) {
        // intentionally blank
	}

	/**
	 * Called when the window is "un-minimized"; resumes the simulation.
	 * 
	 * @param e
	 *            The window deiconification event
	 */
	@Override
	public void windowDeiconified(final WindowEvent e) {
		animator.unpause();
	}

	/**
	 * Called when the window is minimized; pauses the sim.
	 * 
	 * @param e
	 *            The window minimization event
	 */
	@Override
	public void windowIconified(final WindowEvent e) {
		animator.pause();
	}

	/**
	 * Called when the window close button is pressed; stops the simulation.
	 * 
	 * @param e
	 *            The window close event
	 */
	@Override
	public void windowClosing(final WindowEvent e) {
		animator.endProgram();
	}

	/**
	 * Called when the window has been closed; does nothing (required by
	 * WindowListener interface).
	 * 
	 * @param e
	 *            The window closed event
	 */
	@Override
	public void windowClosed(final WindowEvent e) {
	    // intentionally blank
	}

	/**
	 * Called when the window is opened; does nothing (required by WindowListener
	 * interface).
	 * 
	 * @param e
	 *            The window opening event
	 */
	@Override
	public void windowOpened(final WindowEvent e) {
        // intentionally blank
	}

	/**
	 * Called when a property is changed in the Animator; updates GUI information.
	 * 
	 * @param evt
	 *            The property change that has occurred.
	 */
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("time")) {
			jtfTime.setText("Cycles: " + evt.getNewValue());
		} else if (evt.getPropertyName().equals("taskcomplete")) {
			Integer numTasksComplete = (Integer) evt.getNewValue();
			progressBar.setValue(numTasksComplete);
			if (numTasksComplete >= agentapp.getNumTasks() * (float) agentapp.getPercentFinished() / 100.0f) {
				if (agentapp.isBatchMode()) {
					System.out.println(agentapp.getRoomSize() + " " + agentapp.getNumTasks() + " "
							+ agentapp.getNumAgents() + " " + agentapp.getStrategyType() + " " + Animator.getTime());
					animator.endProgram();
				} else {
					animator.stopSim();
				}
			}

		}
	}

}
