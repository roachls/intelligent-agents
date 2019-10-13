package org.roach.intelligentagents.view.swing;

import java.awt.Graphics;

import org.roach.intelligentagents.model.Agent;

public interface ViewStrategy {
	void draw(final Agent agent, final Graphics g, final int squareSize);
	void drawHelperGraphics(final Agent agent, final Graphics g, final int squareSize);
}
