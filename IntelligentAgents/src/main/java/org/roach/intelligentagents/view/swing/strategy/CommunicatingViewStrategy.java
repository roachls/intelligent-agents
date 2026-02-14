package org.roach.intelligentagents.view.swing.strategy;

import java.awt.Color;
import java.awt.Graphics;

import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy;

public class CommunicatingViewStrategy extends DefaultViewStrategy {
    @Override
    public void drawHelperGraphics(final Agent agent, final Graphics g, final int squareSize) {
        if (!(agent.getStrategy() instanceof CommunicatingAgentStrategy))
            return;
        var cas = (CommunicatingAgentStrategy) agent.getStrategy();
        if (cas.getState().equals(cas.RANDOMCOMMS)) {
            /*
             * Draw a big red circle indicating the broadcast range of the agent.
             */
            g.setColor(Color.red);
            int commDist = cas.getCommDist();
            g.drawOval(toGraphicLoc(agent.getLoc().x() - commDist, squareSize),
                    toGraphicLoc(agent.getLoc().y() - commDist, squareSize), toGraphicSize(commDist * 2, squareSize),
                    toGraphicSize(commDist * 2, squareSize));
        } else if (cas.getState().equals(cas.GOTO)) {
            /* Draw a red line from the agent to the destination task */
            agent.getStrategy().getTaskToDo().ifPresent(t -> {
                Location l = t.getLocation();
                g.setColor(Color.red);
                int midPoint = squareSize / 2;
                g.drawLine(toGraphicLoc(agent.getLoc().x(), squareSize) + midPoint,
                        toGraphicLoc(agent.getLoc().y(), squareSize) + midPoint,
                        toGraphicLoc(l.x(), squareSize) + midPoint, toGraphicLoc(l.y(), squareSize) + midPoint);
            });
        }
    }
}
