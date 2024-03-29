package org.roach.intelligentagents.model.strategy;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.model.Agent;
import org.roach.intelligentagents.model.Location;
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.model.Task;
import org.roach.intelligentagents.model.TaskToDo;

/**
 * @author Larry S. Roach
 *
 */
public class MailboxStrategy extends CommunicatingAgentStrategy {
    /** The shared mailbox associated with all Mailbox agents. */
    static Mailbox mailbox = new Mailbox();
    private TaskToDo taskToDo;

    /**
     * No-arg constructor used only by the resource loader
     */
    public MailboxStrategy() {

    }

    /**
     * @param agent
     * 
     */
    public MailboxStrategy(@NonNull final Agent agent, @NonNull final SimulationGrid simGrid) {
	super(agent, simGrid);
	state = RANDOM;
    }

    @Override
    public Optional<TaskToDo> getTaskToDo() {
	return taskToDo != null ? Optional.of(taskToDo) : Optional.empty();
    }

    /**
     * Search function. Move one step randomly. If a new task is found, execute it.
     * If the task isn't complete, post a message about it.
     */
    private void search() {
	agent.randomMove();
	if (agent.foundNewTask()) {
	    agent.executeTask();
	    if (!simGrid.isTaskComplete(agent.getLoc())) {
		Location locold = agent.getLoc();
		mailbox.postMessage(locold);
	    }
	}
    }

    /**
     * Determine if this agent is subjectively "near" (within communication distance
     * of) the location of other
     * 
     * @param other The taskToDo to check
     * @return True if within communication distance
     */
    private boolean near(Location other) {
	int dist = agent.getLoc()
			.getManDist(other);
	return (dist <= commDist);
    }

    /**
     * A mailbox is a FIFO deque of message shared by all MailboxAgents
     * 
     * @author L. Stephen Roach
     */
    static class Mailbox {

	/** The deque of messages */
	Deque<MailMessage> messages = new ArrayDeque<>();

	/**
	 * Add a message to the deque
	 * 
	 * @param loc Location to include in message
	 */
	public void postMessage(@NonNull Location loc) {
	    MailMessage m = new MailMessage(loc);
	    synchronized (messages) {
		if (!messages.contains(m)) {
		    messages.addLast(m);
		}
	    }
	}

	/**
	 * Get the next message
	 * 
	 * @return Next message
	 */
	public MailMessage getMessage() {
	    synchronized (messages) {
		return messages.removeFirst();
	    }
	}

	/**
	 * Determine if there are messages to be gotten
	 * 
	 * @return True if message deque is not empty
	 */
	public boolean messagesExist() {
	    return (!messages.isEmpty());
	}

	/**
	 * Message class for use in Mailbox
	 */
	class MailMessage {

	    /** Location associated with this message */
	    @NonNull
	    Location loc;

	    /**
	     * Create a new MailMessage
	     * 
	     * @param loc Location to be stored in the message
	     */
	    MailMessage(@NonNull Location loc) {
		this.loc = loc;
	    }

	    /**
	     * Get the location of this message
	     * 
	     * @return Location of message
	     */
	    @NonNull
	    public Location location() {
		return loc;
	    }

	    /**
	     * Overloaded equals operator
	     * 
	     * @param o Object to compare
	     * @return True if the locations of the messages are equal
	     */
	    @Override
	    public boolean equals(Object o) {
		if (o instanceof MailMessage) {
		    return (loc == ((MailMessage) o).loc);
		}
		throw new IllegalArgumentException();
	    }

	    /**
	     * Hashcode for the message
	     * 
	     * @return Hashcode of the location
	     */
	    @Override
	    public int hashCode() {
		return loc.hashCode();
	    }
	}
    }

    /**
     * 
     * @see org.roach.intelligentagents.model.strategy.CommunicatingAgentStrategy#initStates()
     */
    @Override
    protected void initStates() {
	super.initStates();
	RANDOM.setAgent(this.agent);
	RANDOM.setAlgorithm(a -> {
	    if (mailbox.messagesExist()) {
		Mailbox.MailMessage msg = mailbox.getMessage();
		@NonNull
		Location l = msg.location();
		if (a.hasDoneAlready(simGrid.getTask(l))) {
		    mailbox.postMessage(l);
		    search();
		} else {
		    if (near(msg.location())) {
			taskToDo = new TaskToDo(msg.location());
			state = GOTO;
		    } else {
			mailbox.postMessage(l);
			search();
		    }
		}
	    } else {
		search();
	    }

	});

	GOTO.setAgent(this.agent);
	GOTO.setAlgorithm(a -> {
	    getTaskToDo().ifPresent((t) -> a.moveTowards(t.getLocation()));
	    if (reachedTask()) {
		a.executeTask();
		Task t = simGrid.getTask(a.getLoc());
		if (t != null && !t.isComplete()) {
		    mailbox.postMessage(a.getLoc());
		}
		state = RANDOM;
	    } else if (a.foundNewTask()) {
		a.executeTask();
		if (!simGrid.isTaskComplete(a.getLoc())) {
		    mailbox.postMessage(a.getLoc());
		}
	    }
	});
    }

    @Override
    public @NonNull String getDescription() {
	return "Mailbox strategy";
    }
}