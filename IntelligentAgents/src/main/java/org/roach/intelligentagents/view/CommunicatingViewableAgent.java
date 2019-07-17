package org.roach.intelligentagents.view;

import java.awt.Color;
import java.awt.Graphics;

import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.TaskToDo;
import org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy;

/**
 * @author Larry S. Roach
 *
 */
public class CommunicatingViewableAgent extends ViewableAgent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CommunicatingAgentStrategy strategy = null;
	
	/**
	 * @param a
	 */
	public CommunicatingViewableAgent(Agent a) {
		super(a);
		if (a.getStrategy() instanceof CommunicatingAgentStrategy)
			this.strategy = (CommunicatingAgentStrategy)a.getStrategy();
	}

	/**
	 * Draws optional "helper graphics" to the screen.
	 * 
	 * @param g
	 *            The graphics context in which to draw
	 */
	@Override
	public void drawHelperGraphics(Graphics g) {
		if (strategy.getState().equals(strategy.RANDOMCOMMS)) {
			/*
			 * Draw a big red circle indicating the broadcast range of the agent.
			 */
			g.setColor(Color.red);
			int commDist = ((Integer)agent.getProperty(CommunicatingAgentStrategy.COMM_DIST)).intValue();
			g.drawOval(toGraphicLoc(agent.getLoc().getX() - commDist), toGraphicLoc(agent.getLoc().getY() - commDist),
					toGraphicSize(commDist * 2), toGraphicSize(commDist * 2));
		} else if (strategy.getState().equals(strategy.GOTO)) {
			/* Draw a red line from the agent to the destination task */
			TaskToDo t = strategy.getTaskToDo();
			if (t != null) {
				Location l = t.getLocation();
				g.setColor(Color.red);
				int midPoint = squareSize / 2;
				g.drawLine(toGraphicLoc(agent.getLoc().getX()) + midPoint,
						toGraphicLoc(agent.getLoc().getY()) + midPoint, toGraphicLoc(l.getX()) + midPoint,
						toGraphicLoc(l.getY()) + midPoint);
			}
		}
		int midPoint = squareSize / 2;
		if (agent.getStrategy().getCommunicants() != null) {
			for (Agent a : agent.getStrategy().getCommunicants()) {
				Location l = a.getLoc();
				g.drawLine(toGraphicLoc(agent.getLoc().getX()) + midPoint,
						toGraphicLoc(agent.getLoc().getY()) + midPoint, toGraphicLoc(l.getX()) + midPoint,
						toGraphicLoc(l.getY()) + midPoint);
			}
		}
	}

}
