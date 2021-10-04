/**
 * SwingAnimator.java
 *
 * Created on December 1, 2006, 4:34 PM
 * @author L. Stephen Roach
 */
package org.roach.intelligentagents.view.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.controller.AgentApp;
import org.roach.intelligentagents.view.AAnimator;

/**
 * <p>
 * The SwingAnimator class is the Thread that animates and runs the simulation.
 * <p>
 * NOTE: The overall concepts of the graphics programming for this simulation is
 * derived from the book <i>Killer Game Programming in Java</i>, by Andrew
 * Davison.
 * </p>
 *
 * @author L. Stephen Roach
 */
public final class SwingAnimator extends AAnimator {

    /** The JPanel to display everything on */
    private JPanel panel;
    /** Determines whether the simulation is paused. */
    private volatile boolean isPaused = true; // NOPMD by Family on 11/26/19, 2:57 PM
    /**
     * Determines whether the sim is rendered to the screen. This may speed up
     * progress because it reduces the overhead of the graphics.
     */
    private boolean render = true;
    /**
     * Determines whether "helper" graphics are shown, including the red
     * broadcast-range circle and the red "goto" lines.
     */
    private boolean showHelperGraphics = true;
    /** Determines whether agents are displayed. */
    private boolean showAgents = true;
    /** The double-buffered graphics context in which to render frames. */
    @Nullable
    private Graphics dbg;
    /** The buffer image in which to render each new frame. */
    @Nullable
    private Image dbImage;

    /**
     * Creates a new instance of SwingAnimator.
     * 
     * @param p The JPanel to display everything on
     */
    public SwingAnimator(@NonNull final JPanel p, @NonNull final AgentApp agentApp) {
	super(agentApp);
	this.panel = p;
	// Initialize simulation components
	isPaused = true;
    }

    /**
     * Initialize and start the simulation thread.
     */
    @Override
    public void startSim() {
	// If the thread doesn't exist, create it and start it
	super.startSim();
	isPaused = false;
    }

    /**
     * Determine if the animation has been started
     * 
     * @return True if the animatorThread thread exists and is running
     */
    public boolean isStarted() {
	return (animatorThread != null && isRunning);
    }

    /**
     * Repeatedly run update-render-sleep cycle.
     */
    @Override
    public void run() {
	isRunning = true;
	while (isRunning) {
	    if (!isPaused) {
		step();
	    }
	}
    }

    @Override
    public void step() {
	simUpdate(); // update sim state
	simRender(); // render to a graphics buffer
	paintScreen(); // draw buffer to screen
    }

    /**
     * Sets active rendering on or off; does not affect display of statistics.
     * 
     * @param render Set to true to turn rendering on
     */
    public void setRender(boolean render) {
	this.render = render;
    }

    /**
     * Renders the current frame to the buffer and flips the double-buffer page.
     */
    private void simRender() {
	if (render) { // If rendering is turned on
	    // Get the image buffer to draw to
	    int windowWidth = panel.getWidth();
	    int windowHeight = panel.getHeight();
	    if (dbImage == null) {
		dbImage = panel.createImage(windowWidth, windowHeight);
		if (dbImage != null)
		    dbg = dbImage.getGraphics();
		else
		    return;
	    }
	    // clear the background
	    if (dbg != null) {
		dbg.clearRect(0, 0, windowWidth, windowHeight);

		for (Component c : panel.getComponents()) {
		    if (c instanceof ViewableTask) {
			ViewableTask t = (ViewableTask) c;
			t.draw(dbg);
		    }
		}
		// Now display the agents
		if (showAgents) {
		    for (Component c : panel.getComponents()) {
			if (c instanceof ViewableAgent) {
			    ViewableAgent a = (ViewableAgent) c;
			    a.draw(dbg);
			    if (showHelperGraphics) { // Display helper graphics if selected
				a.drawHelperGraphics(dbg);
			    }
			}
		    }
		}
	    }
	}
    }

    /**
     * Actively renders the buffer image to the screen.
     */
    private void paintScreen() {
	if (render) {
	    try {
		Graphics g = panel.getGraphics(); // get the panel's graphic context
		if ((g != null) && (dbImage != null)) {
		    g.drawImage(dbImage, 0, 0, null); // draw image to buffer
		    g.dispose(); // Switch buffers
		}
	    } catch (Exception e) {
		javax.swing.JOptionPane.showMessageDialog(panel, "Graphics context error: " + e.toString(),
			"Graphics error", JOptionPane.ERROR_MESSAGE);
	    }
	}
    }

    /**
     * Sets whether the simulation is paused.
     * 
     * @param paused Set to true to pause the simulation
     */
    public void pause() {
	// called when the JFrame is activated / deiconified
	isPaused = true;
    }

    public void unpause() {
	isPaused = false;
    }

    /**
     * Sets showHelperGraphics on or off.
     * 
     * @param show Set to true to show helper graphics
     */
    public void setHelperGraphics(boolean show) {
	showHelperGraphics = show;
    }

    /**
     * Tells whether the simulation is currently paused.
     * 
     * @return true if simulation is paused
     */
    public boolean isPaused() {
	return isPaused;
    }

    /**
     * Sets whether agents are displayed graphically or not.
     * 
     * @param show true displays agents, false does not
     */
    public void setShowAgents(boolean show) {
	showAgents = show;
    }
}
