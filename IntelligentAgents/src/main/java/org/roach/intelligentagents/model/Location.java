/*
 * Location.java
 *
 * Created on December 3, 2006, 3:47 PM
 * @author L. Stephen Roach
 */

package org.roach.intelligentagents.model;
import java.awt.Dimension;
import java.util.Random;

/**
 * The Location class encapsulates an (x, y) coordinate along with tools
 * for measuring distance and moving.
 * @author L. Stephen Roach
 */
public final class Location implements Cloneable {
	/** The size of the grid-space in which the agent moves. */
	private static int gridSize = 100;

    /**
     * The x-coordinate of the Location.
     */
    private int x;
    /**
     * The y-coordinate of the Location.
     */
    private int y;
    /**
     * Creates a new instance of Location.
     * @param inputx x-coordinate
     * @param inputy y-coordinate
     */
    public Location(final int inputx, final int inputy) {
        x = inputx;
        y = inputy;
    }
    /**
     * Creates a new instance of Location.
     * @param inputX x-coordinate
     * @param inputY y-coordinate
     */
    public Location(final Integer inputX, final Integer inputY) {
        x = inputX;
        y = inputY;
    }
    /**
     * Converts a Location to a java.awt.Dimension.
     * @return A Dimension object
     */
    public Dimension toDimension() {
        return new Dimension(x, y);
    }
    /**
     * Get the manhattan distance from the location to the rhs location.
     * @param that The Location to be compared
     * @return Manhattan distance (x-distance plus y-distance)
     */
    public int getManDist(final Location that) {
        // Return |x - rhs.x| + |y - rhs.y|
        return Math.abs(this.x - that.x) + Math.abs(this.y - that.y);
    }
    /**
     * Calculates whether this Location and rhs are within a given distance of
     * each other.
     * @param that The Location to be compared
     * @param radius The radius of the circle to check
     * @return True if the distance from this to rhs is less than or equal to
     * radius, false otherwise
     */
    public boolean isInCircle(final Location that, final int radius) {
        // We can avoid floating-point math by using the circular formula
        //     (rhs.x - x)^2 + (rhs.y - y)^2 = radius^2
        float dx = (float) this.x - that.x;
        float dy = (float) this.y - that.y;
        return dx * dx + dy * dy <= radius * radius;
    }
    /**
     * Gets the x-component of the manhattan distance from this to rhs.
     * @param that The Location to be compared
     * @return An int representing the distance
     */
    public int getXDistance(final Location that) {
        // Note: we don't take the absolute value because this distance will
        // be used by an agent to determine which direction to go.  A negative
        // distance indicates that this Location is north of rhs; positive
        // indicates that this is south of rhs.
        return x - that.x;
    }
    /**
     * Gets the y-component of the manhattan distance from this to rhs.
     * @param that The Location to be compared
     * @return An int representing the distance
     */
    public int getYDistance(final Location that) {
        // Note: we don't take the absolute value because this distance will
        // be used by an agent to determine which direction to go.  A negative
        // distance indicates that this Location is east of rhs; positive
        // indicates that this is west of rhs.
        return y - that.y;
    }
    /**
     * Moves the Location one square west.
     */
    public void moveWest() {
        x--;
    }
    /**
     * Moves the Location one square east.
     */
    public void moveEast() {
        x++;
    }
    /**
     * Moves the Location one square north.
     */
    public void moveNorth() {
        y--;
    }
    /**
     * Moves the Location one square south.
     */
    public void moveSouth() {
        y++;
    }

    /**
     *
     */
    private void moveNorthWest() {
    	moveNorth();
    	moveWest();
    }

    /**
     *
     */
    private void moveSouthWest() {
    	moveSouth();
    	moveWest();
    }

    /**
     *
     */
    private void moveNorthEast() {
    	moveNorth();
    	moveEast();
    }

    /**
     *
     */
    private void moveSouthEast() {
    	moveSouth();
    	moveEast();
    }

    /**
     * Determines if this Location is equal to the Location o.
     * @param o The Location object to be compared
     * @return True if both x and y coordinates are the same in each object
     */
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Location)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        return x == ((Location) o).x && y == ((Location) o).y;
    }
    /**
     * Gets the x-coordinate of this Location.
     * @return x
     */
    public int getX() {
        return x;
    }
    /**
     * Gets the y-coordinate of this Location.
     * @return y
     */
    public int getY() {
        return y;
    }
    /**
     * Returns a clone of this location.
     * @return A new copy of this Location
     */
    @Override
    public Location clone() {
        return new Location(x, y);
    }

    /**
     * Gets a hash code for this Location.
     * @return hash code
     */
    @Override
    public int hashCode() {
        int result = 17;
        result = 37 * result + x;
        result = 37 * result + y;
        return result;
    }
    /**
     * Get the String representation of this Location.
     * @return a string
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
	/**
	 * When in Random or Random-Comms state, this method chooses a random direction
	 * and moves the agent.
	 */
	public void randomMove() {
		Random rand = new Random();
		// Get a random number between 1 and 8
		int dir = rand.nextInt(8);
		switch (dir) { // Move the selected direction
		case 0:
			// move north
			if (y > 0) { // Can't move past the north wall!
				moveNorth();
			}
			break;
		case 1:
			// move east
			if (x < gridSize - 1) {
				// Can't move past the east wall!
				moveEast();
			}
			break;
		case 2:
			// move south
			if (y < gridSize - 1) {
				// Can't move past the south wall!
				moveSouth();
			}
			break;
		case 3:
			// move west
			if (x > 0) {
				// Can't move past the west wall!
				moveWest();
			}
			break;
		case 4:
			if (x > 0 && y > 0)
				moveNorthWest();
			break;
		case 5:
			if (x > 0 && y < gridSize - 1)
				moveSouthWest();
			break;
		case 6:
			if (x < gridSize - 1 && y > 0)
				moveNorthEast();
			break;
		case 7:
			if (x < gridSize - 1 && y < gridSize - 1)
				moveSouthEast();
			break;
		}
	}
	
	/**
	 * @return the gridSize
	 */
	public static int getGridSize() {
		return gridSize;
	}
	
	/**
	 * @param aGridSize
	 *            Number of squares on a side for the grid
	 */
	public static void setGridSize(int aGridSize) {
		gridSize = aGridSize;
	}

	/**
	 * @return a random location
	 */
	public static Location getRandomLocation() {
		// Randomly pick a position on the grid for the agent to start at
		Random rand = new Random();
		int x = rand.nextBoolean() ? 0 : (gridSize - 1);
		int y = rand.nextBoolean() ? 0 : (gridSize - 1);
		return new Location(x, y);
	}
	
	/**
	 * When the agent is in Goto state, this method moves it towards the task being
	 * sought.
	 * @param target Location to move towards
	 */
	void moveTowards(Location target) {

	    // Together, these two conditions form the eight movement directions
	    if(this.x < target.x) {
	        this.x++;
        } else if (this.x > target.x) {
	        this.x--;
        }

        if(this.y < target.y) {
	        this.y++;
        } else if (this.y > target.y) {
	        this.y--;
        }
	}

}
