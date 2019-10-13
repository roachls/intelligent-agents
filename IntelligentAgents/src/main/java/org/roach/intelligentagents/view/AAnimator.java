package org.roach.intelligentagents.view;

import java.beans.PropertyChangeSupport;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.controller.AgentApp;
import org.roach.intelligentagents.model.Agent;

public abstract class AAnimator implements IAnimator {
    /** The animation thread. */
    @Nullable protected Thread animatorThread;
    /**
     * Determines whether the simulation is running or not. When this is false,
     * the application terminates.
     */
    protected volatile boolean isRunning = true;
    /** Determines whether the simulation is complete. */
    private volatile boolean simOver = false;
    /** The current master time. */
    private volatile static int time;
    @NonNull protected final AgentApp agentApp;
    protected final PropertyChangeSupport pcs;

    /**
     * Creates a new instance of SwingAnimator.
     * @param p The JPanel to display everything on
     */
    public AAnimator(@NonNull final AgentApp agentApp) {
        this.agentApp = agentApp;
        // Initialize simulation components
        time = 0;
        pcs = new PropertyChangeSupport(this);
    }

    /**
     * Initialize and start the simulation thread.
     */
    @Override
	public void startSim() {
        // If the thread doesn't exist, create it and start it
        if (animatorThread == null || !isRunning) {
            animatorThread = new Thread(this, "AnimationThread");
            animatorThread.start();
        }
    }

    /**
     * Called when all tasks have been found or when the user presses the
     * Stop button.
     */
    @Override
	public void stopSim() {
        simOver = true;
    }

    /**
     * Sets isRunning to false, thus ending the main loop and the thread.
     */
    @Override
	public void endProgram() {
        isRunning = false;
        animatorThread = null;
        System.exit(0);
    }

    /**
     * Updates the status of all agents (calls the doAction() method of each
     * agent).
     */
    protected void simUpdate() {
        if (!simOver) { // If the sim isn't paused or complete
            // Update all agents
            for (Agent a : agentApp.getSimgrid().getAgents()) {
            	if (a != null)
            		a.getStrategy().doAction();
            }
            // Display the new time
            time++;
            pcs.firePropertyChange(PropertyConstants.TIME_TICK, time, time - 1);
        }
    }

    /**
     * Get the current time.
     * @return time
     */
    public static int getTime() {
        return time;
    }

    abstract public void step();
}
