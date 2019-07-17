package org.roach.intelligentagents.view;

import javax.swing.JComponent;

import org.roach.intelligentagents.model.Task;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Larry S. Roach
 *
 */
public class ViewableTask extends JComponent implements PropertyChangeListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1038440479158389343L;
	/** The maximum color level. */
    private static final int MAX_COLOR = 255;
    /** The initial size of a task (in pixels). */
    private static final int INITIAL_ZOOM_FACTOR = 6;
    /** The amount of green to add to each task as it gets more complete. */
    private static final int COLOR_STEP = MAX_COLOR / Task.getTaskComplete();
    /** The size to draw the graphical square. */
    private static int squareSize = INITIAL_ZOOM_FACTOR;
    /** The task this object draws */
    private Task task;
    private Color color = new Color(0, 0, 0);
    int x, y, width, height;
    
    /**
     * Sets the onscreen size of the task.
     * @param aSquareSize The size in pixels
     */
    public static void setSquareSize(final int aSquareSize) {
        squareSize = aSquareSize;
    }
    /**
     * Draws the Task on the screen.
     * @param g The graphics context in which to draw
     */
    public void draw(final Graphics g) {
            // Calculate color of square based on priority of task
            g.setColor(color);
            // Draw the square
            g.fillRect(x, y, width, height);
    }

    /**
     * @param t
     */
    public ViewableTask(Task t) {
    	task = t;
    	x = task.getLocation().getX() * squareSize + 1;
    	y = task.getLocation().getY() * squareSize + 1;
    	width = squareSize - 1;
    	height = squareSize - 1;
    }
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("taskexecute")) {
			color = null;
			color = new Color(0, (Integer)evt.getNewValue() * COLOR_STEP, 0);
		} else if (evt.getPropertyName().equals("taskcomplete")) {
			color = null;
			color = Color.GREEN;
		}
	}
}
