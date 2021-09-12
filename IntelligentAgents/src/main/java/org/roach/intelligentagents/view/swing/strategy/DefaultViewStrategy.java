package org.roach.intelligentagents.view.swing.strategy;

import java.awt.Graphics;

import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.view.swing.ViewStrategy;

public class DefaultViewStrategy implements ViewStrategy {
    @Override
    public void draw(final Agent agent, final Graphics g, int squareSize) {
	g.setColor(agent.getStrategy()
			.getState()
			.getColor());
	g.drawRect(toGraphicLoc(agent.getLoc()
				     .getX(),
		squareSize),
		toGraphicLoc(agent.getLoc()
				  .getY(),
			squareSize),
		squareSize, squareSize);
    }

    /**
     * Convert a grid location to graphic coordinates for screen display.
     * 
     * @param coord The coordinate to convert
     * @return The converted coordinate
     */
    protected static int toGraphicLoc(int coord, final int squareSize) {
	return coord * squareSize;
    }

    /**
     * Converts a width in sim-space to an actual pixel-width for screen display.
     * 
     * @param width The width in grid-cells
     * @return Width in pixels
     */
    protected static int toGraphicSize(final int width, final int squareSize) {
	return width * squareSize + squareSize - 1;
    }

    @Override
    public void drawHelperGraphics(final Agent agent, final Graphics g, final int squareSize) {
	// Nothing to do
    }
}
