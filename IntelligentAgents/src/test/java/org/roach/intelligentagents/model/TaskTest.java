package org.roach.intelligentagents.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @author Larry S. Roach
 *
 */
public class TaskTest {

    /**
     * 
     */
    @Test
    public void testGetPrio() {
	Task t1 = new Task(new Location(0, 0));
	assertEquals(t1.getPrio(), 0);
    }

    /**
     * 
     */
    @Test
    public void testExecute() {
	Task t1 = new Task(new Location(0, 0));
	t1.execute();
	assertEquals(t1.getPrio(), 1);
    }

    /**
     * 
     */
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
    @Test
    public void testEqualsObject() {
	Task t1 = new Task(new Location(0, 0));
	Task t2 = new Task(new Location(0, 1));
	Task t3 = new Task(new Location(0, 0));
	assertNotEquals(t1, t2);
	assertEquals(t1, t1);
	assertEquals(t1, t3);
	assertEquals(t3, t1);
    }

    /**
     * 
     */
    @Test
    public void testToString() {
	Task t1 = new Task(new Location(0, 0));
	assertEquals(t1.toString(), "Task: (0,0), prio: 0");
    }

}
