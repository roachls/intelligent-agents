/*
 * Location.java
 *
 * Created on December 3, 2006, 3:47 PM
 * @author L. Stephen Roach
 */

package org.roach.intelligentagents.model;

import java.awt.Dimension;
import java.security.SecureRandom;

import org.eclipse.jdt.annotation.NonNull;

/**
 * The Location class encapsulates an (x, y) coordinate along with tools for
 * measuring distance and moving.
 * 
 * @author L. Stephen Roach
 */
public record Location(int x, int y) {
    private final static SecureRandom RAND = new SecureRandom();

    /**
     * Converts a Location to a java.awt.Dimension.
     * 
     * @return A Dimension object
     */
    @NonNull
    public Dimension toDimension() {
        return new Dimension(x, y);
    }

    /**
     * Get the manhattan distance from the location to the rhs location.
     * 
     * @param rhs The Location to be compared
     * @return Manhattan distance (x-distance plus y-distance)
     */
    public int getManDist(final Location rhs) {
        // Return |x - rhs.x| + |y - rhs.y|
        return Math.abs(x - rhs.x) + Math.abs(y - rhs.y);
    }

    /**
     * Calculates whether this Location and rhs are within a given distance of each
     * other.
     * 
     * @param rhs    The Location to be compared
     * @param radius The radius of the circle to check
     * @return True if the distance from this to rhs is less than or equal to
     *         radius, false otherwise
     */
    public boolean isInCircle(@NonNull final Location rhs, final int radius) {
        /*
         * We can avoid floating-point math by using the circular formula (rhs.x - x)^2
         * + (rhs.y - y)^2 = radius^2, and so any for any point within the circle it
         * will be true that (rhs.y - y)^2 <= radius^2 - (rhs.x - x)^2
         */
        return ((rhs.y - y) * (rhs.y - y) <= radius * radius - (rhs.x - x) * (rhs.x - x));
    }

    /**
     * Gets the x-component of the manhattan distance from this to rhs.
     * 
     * @param rhs The Location to be compared
     * @return An int representing the distance
     */
    public int getXDistance(@NonNull final Location rhs) {
        /*
         * Note: we don't take the absolute value because this distance will be used by
         * an agent to determine which direction to go. A negative distance indicates
         * that this Location is north of rhs; positive indicates that this is south of
         * rhs.
         */
        return x - rhs.x;
    }

    /**
     * Gets the y-component of the manhattan distance from this to rhs.
     * 
     * @param rhs The Location to be compared
     * @return An int representing the distance
     */
    public int getYDistance(@NonNull final Location rhs) {
        /*
         * Note: we don't take the absolute value because this distance will be used by
         * an agent to determine which direction to go. A negative distance indicates
         * that this Location is east of rhs; positive indicates that this is west of
         * rhs.
         */
        return y - rhs.y;
    }

    /**
     * Get the String representation of this Location.
     * 
     * @return a string
     */
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @NonNull
    public static Location randomLocation(final int size) {
        int x = RAND.nextInt(size); // pick a random x
        int y = RAND.nextInt(size); // pick a random y
        return new Location(x, y);
    }
}
