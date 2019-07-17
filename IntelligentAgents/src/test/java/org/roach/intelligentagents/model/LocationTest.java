package org.roach.intelligentagents.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;

import org.junit.Test;
import org.roach.intelligentagents.model.Location;

/**
 * @author Larry S. Roach
 *
 */
public class LocationTest {

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testLocationCreation() {
		Location l1 = new Location(0, 0);
		assertTrue(l1 instanceof org.roach.intelligentagents.model.Location);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testEquals() {
		Location l1 = new Location(1, 1);
		Location l2 = new Location(1, 1);
		Location l3 = new Location(0, 0);
		assertTrue(l1.equals(l2));
		assertTrue(l2.equals(l1));
		assertFalse(l1.equals(l3));
		assertTrue(l1.equals(l1));
		assertFalse(l3.equals(l1));
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testToDimension() {
		Location l1 = new Location(2, 3);
		Dimension d1 = new Dimension(2, 3);
		Dimension d2 = new Dimension(3, 2);
		Dimension d3 = new Dimension(-2, -3);
		assertEquals(l1.toDimension(), d1);
		assertFalse(l1.toDimension() == d2);
		assertFalse(l1.toDimension() == d3);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetManDist() {
		Location l1 = new Location(2, 3);
		Location l2 = new Location(-2, 0);
		assertEquals(l1.getManDist(l2), 7);
		assertEquals(l2.getManDist(l1), 7);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testIsInCircle() {
		Location l1 = new Location(0, 0);
		Location l2 = new Location(5, 5);
		assertTrue(l1.isInCircle(l2, 10));
		assertFalse(l1.isInCircle(l2, 5));
		assertTrue(l2.isInCircle(l1, 10));
		assertFalse(l2.isInCircle(l1, 5));
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetXDistance() {
		Location l1 = new Location(0, 0);
		Location l2 = new Location(5, 5);
		assertEquals(l1.getXDistance(l2), -5);
		assertEquals(l2.getXDistance(l1), 5);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetYDistance() {
		Location l1 = new Location(0, 0);
		Location l2 = new Location(5, 5);
		assertEquals(l1.getYDistance(l2), -5);
		assertEquals(l2.getYDistance(l1), 5);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testMoveWest() {
		Location l1 = new Location(0, 0);
		l1.moveWest();
		assertEquals(l1.getX(), -1);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testMoveEast() {
		Location l1 = new Location(0, 0);
		l1.moveEast();
		assertEquals(l1.getX(), 1);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testMoveNorth() {
		Location l1 = new Location(0, 0);
		l1.moveNorth();
		assertEquals(l1.getY(), -1);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testMoveSouth() {
		Location l1 = new Location(0, 0);
		l1.moveSouth();
		assertEquals(l1.getY(), 1);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetX() {
		Location l1 = new Location(4, 5);
		assertEquals(l1.getX(), 4);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetY() {
		Location l1 = new Location(4, 5);
		assertEquals(l1.getY(), 5);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testClone() {
		Location l1 = new Location(4, 5);
		Location l2 = l1.clone();
		assertEquals(l1, l2);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testToString() {
		Location l1 = new Location(4, 5);
		assertEquals(l1.toString(), "(4,5)");
	}
}
