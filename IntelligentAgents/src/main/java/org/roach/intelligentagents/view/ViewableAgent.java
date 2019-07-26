package org.roach.intelligentagents.view;

import java.awt.Graphics;

import javax.swing.JComponent;

import org.roach.intelligentagents.model.Agent;

/**
 * @author Larry S. Roach
 *
 */
public class ViewableAgent extends JComponent {

	protected Agent agent; // The agent this object is displaying
	protected static ViewStrategy viewStrategy;
	protected static int squareSize = 6;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6014264948161059653L;

	/**
	 * @param a
	 */
	public ViewableAgent(Agent a) {
		agent = a;
	}

	/**
	 * Sets the visual size of a square.
	 * 
	 * @param aSquareSize
	 *            Number of pixels for a square
	 */
	public static void setSquareSize(int aSquareSize) {
		squareSize = aSquareSize;
	}
	
	/**
	 * Draws the agent to the screen as a square of the color specified by the
	 * current state.
	 * 
	 * @param g
	 *            The graphics context in which to draw
	 */
	public void draw(Graphics g) {
		if (viewStrategy != null) {
			viewStrategy.draw(agent, g, squareSize);
		}
	}

	/**
	 * @param g
	 */
	public void drawHelperGraphics(Graphics g) {
		if (viewStrategy != null)
			viewStrategy.drawHelperGraphics(agent, g, squareSize);
	}

	/**
	 * Setter for 
	 * @param viewStrategy the viewStrategy to set
	 */
	public static void setViewStrategy(ViewStrategy viewStrategy) {
		ViewableAgent.viewStrategy = viewStrategy;
	}
}
