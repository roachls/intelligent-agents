package org.roach.intelligentagents.view.swing.strategy;

import java.awt.Color;
import java.awt.Graphics;

import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy;

public class CommunicatingViewStrategy extends DefaultViewStrategy {
    @Override
    public void drawHelperGraphics(final Agent agent, final Graphics g, final int squareSize) {
	if (agent.getStrategy() instanceof CommunicatingAgentStrategy && agent.getStrategy()
									      .getState()
									      .equals(((CommunicatingAgentStrategy) agent.getStrategy()).RANDOMCOMMS)) {
	    /*
	     * Draw a big red circle indicating the broadcast range of the agent.
	     */
	    g.setColor(Color.red);
	    if (agent.getStrategy() instanceof CommunicatingAgentStrategy) {
		int commDist = ((CommunicatingAgentStrategy) agent.getStrategy()).getCommDist();
		g.drawOval(toGraphicLoc(agent.getLoc()
					     .getX()
			- commDist, squareSize), toGraphicLoc(
				agent.getLoc()
				     .getY() - commDist,
				squareSize),
			toGraphicSize(commDist * 2, squareSize), toGraphicSize(commDist * 2, squareSize));
	    }
	} else if (agent.getStrategy() instanceof CommunicatingAgentStrategy && agent.getStrategy()
										     .getState()
										     .equals(((CommunicatingAgentStrategy) agent.getStrategy()).GOTO)) {
	    /* Draw a red line from the agent to the destination task */
	    agent.getStrategy()
		 .getTaskToDo()
		 .ifPresent((t) -> {
		     Location l = t.getLocation();
		     g.setColor(Color.red);
		     int midPoint = squareSize / 2;
		     g.drawLine(toGraphicLoc(agent.getLoc()
						  .getX(),
			     squareSize) + midPoint,
			     toGraphicLoc(agent.getLoc()
					       .getY(),
				     squareSize) + midPoint,
			     toGraphicLoc(l.getX(), squareSize) + midPoint,
			     toGraphicLoc(l.getY(), squareSize) + midPoint);
		 });
	}
    }
}
