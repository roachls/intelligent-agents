package org.roach.intelligentagents.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.Task;

/**
 * @author Larry S. Roach
 *
 */
public class TaskTest {
	
	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testGetPrio() {
		Task t1 = new Task(new Location(0, 0));
		assertEquals(t1.getPrio(), 0);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testExecute() {
		Task t1 = new Task(new Location(0, 0));
		t1.execute();
		assertEquals(t1.getPrio(), 1);
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testIsComplete() {
		Task t1 = new Task(new Location(0, 0));
		assertFalse(t1.isComplete());
		t1.execute();
		assertFalse(t1.isComplete());
		t1.execute();
		assertFalse(t1.isComplete());
		t1.execute();
		assertFalse(t1.isComplete());
		t1.execute();
		assertFalse(t1.isComplete());
		t1.execute();
		assertTrue(t1.isComplete());
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testEqualsObject() {
		Task t1 = new Task(new Location(0, 0));
		Task t2 = new Task(new Location(0, 1));
		Task t3 = new Task(new Location(0, 0));
		assertFalse(t1.equals(t2));
		assertTrue(t1.equals(t1));
		assertTrue(t1.equals(t3));
		assertTrue(t3.equals(t1));
	}

	/**
	 * 
	 */
	@SuppressWarnings("static-method")
	@Test
	public void testToString() {
		Task t1 = new Task(new Location(0, 0));
		assertEquals(t1.toString(), "Task: (0,0), prio: 0");
	}

}
