package org.roach.intelligentagents.view;

import java.awt.Graphics;

import javax.swing.JComponent;

import org.roach.intelligentagents.model.Agent;

/**
 * @author Larry S. Roach
 *
 */
public class ViewableAgent extends JComponent {

	/**
	 * Getter for 
	 * @return the squareSize
	 */
	public static int getSquareSize() {
		return squareSize;
	}

	protected Agent agent; // The agent this object is displaying
	/** The size of the square to be displayed. */
	protected static volatile int squareSize = 6;
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
	
	public static void decSquareSize() {
		if (squareSize > 1)
			squareSize--;
	}
	
	public static void incSquareSize() {
		squareSize++;
	}

	/**
	 * Convert a grid location to graphic coordinates for screen display.
	 * 
	 * @param coord
	 *            The coordinate to convert
	 * @return The converted coordinate
	 */
	protected static int toGraphicLoc(int coord) {
		return coord * squareSize;
	}

	/**
	 * Converts a width in sim-space to an actual pixel-width for screen display.
	 * 
	 * @param width
	 *            The width in grid-cells
	 * @return Width in pixels
	 */
	protected static int toGraphicSize(int width) {
		return width * squareSize + squareSize - 1;
	}

	/**
	 * Draws the agent to the screen as a square of the color specified by the
	 * current state.
	 * 
	 * @param g
	 *            The graphics context in which to draw
	 */
	public void draw(Graphics g) {
		g.setColor(agent.getStrategy().getState().getColor());
		g.drawRect(agent.getLoc().getX() * squareSize, agent.getLoc().getY() * squareSize, squareSize, squareSize);
	}

	/**
	 * @param g
	 */
	public void drawHelperGraphics(Graphics g) {
		
	}
}
