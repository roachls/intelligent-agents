/*
 * Location.java
 *
 * Created on December 3, 2006, 3:47 PM
 * @author L. Stephen Roach
 */

package org.roach.intelligentagents.model;
import java.awt.Dimension;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * The Location class encapsulates an (x, y) coordinate along with tools
 * for measuring distance and moving.
 * @author L. Stephen Roach
 */
public final class Location implements Cloneable {
    /**
     * The x-coordinate of the Location.
     */
    private final int x;
    /**
     * The y-coordinate of the Location.
     */
    private final int y;
    
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
     * @param rhs The Location to be compared
     * @return Manhattan distance (x-distance plus y-distance)
     */
    public int getManDist(final Location rhs) {
        // Return |x - rhs.x| + |y - rhs.y|
        return Math.abs(x - rhs.x) + Math.abs(y - rhs.y);
    }
    /**
     * Calculates whether this Location and rhs are within a given distance of
     * each other.
     * @param rhs The Location to be compared
     * @param radius The radius of the circle to check
     * @return True if the distance from this to rhs is less than or equal to
     * radius, false otherwise
     */
    public boolean isInCircle(final Location rhs, final int radius) {
        // We can avoid floating-point math by using the circular formula
        //     (rhs.x - x)^2 + (rhs.y - y)^2 = radius^2,
        // and so any for any point within the circle it will be true that
        //     (rhs.y - y)^2 <= radius^2 - (rhs.x - x)^2
        return ((rhs.y - y) * (rhs.y - y) <= radius * radius
                - (rhs.x - x) * (rhs.x - x));
    }
    /**
     * Gets the x-component of the manhattan distance from this to rhs.
     * @param rhs The Location to be compared
     * @return An int representing the distance
     */
    public int getXDistance(final Location rhs) {
        // Note: we don't take the absolute value because this distance will
        // be used by an agent to determine which direction to go.  A negative
        // distance indicates that this Location is north of rhs; positive
        // indicates that this is south of rhs.
        return x - rhs.x;
    }
    /**
     * Gets the y-component of the manhattan distance from this to rhs.
     * @param rhs The Location to be compared
     * @return An int representing the distance
     */
    public int getYDistance(final Location rhs) {
        // Note: we don't take the absolute value because this distance will
        // be used by an agent to determine which direction to go.  A negative
        // distance indicates that this Location is east of rhs; positive
        // indicates that this is west of rhs.
        return y - rhs.y;
    }

    /**
     * Determines if this Location is equal to the Location o.
     * @param o The Location object to be compared
     * @return True if both x and y coordinates are the same in each object
     */
    @Override
    public boolean equals(final @Nullable Object o) {
        if (!(o instanceof Location)) {
            return false;
        }
        if ((Location) o == this) {
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
    @NonNull
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
}
