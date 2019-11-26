package org.roach.intelligentagents.view.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;

import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.model.Task;

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
    private static volatile int squareSize = INITIAL_ZOOM_FACTOR; // NOPMD by Family on 11/26/19, 2:57 PM
    /** The task this object draws */
    private Task task;
    private Color color = new Color(0, 0, 0);
    
    /**
     * Sets the onscreen size of the task.
     * @param aSquareSize The size in pixels
     */
    public static void setSquareSize(final int aSquareSize) {
        squareSize = aSquareSize;
    }
    
    public static void decSquareSize() {
    	if (squareSize > 2) // NOPMD by Family on 11/26/19, 2:57 PM
    		squareSize--;
    }
    
    public static void incSquareSize() {
    	squareSize++;
    }
    
    /**
     * Draws the Task on the screen.
     * @param g The graphics context in which to draw
     */
    public void draw(final Graphics g) {
            // Calculate color of square based on priority of task
            g.setColor(color);
            // Draw the square
            g.fillRect(task.getLocation().getX() * squareSize + 1, task.getLocation().getY() * squareSize + 1, squareSize - 1, squareSize - 1);
    }

    /**
     * @param t
     */
    public ViewableTask(Task t) {
    	task = t;
    }
    
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertyConstants.TASK_EXECUTE)) {
			color = new Color(0, (Integer)evt.getNewValue() * COLOR_STEP, 0);
		} else if (evt.getPropertyName().equals("taskcomplete")) {
			color = Color.GREEN;
		}
	}
}
