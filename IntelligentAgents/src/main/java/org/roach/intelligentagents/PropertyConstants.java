package org.roach.intelligentagents;

import org.eclipse.jdt.annotation.NonNull;

/**
 * global constants
 */
public class PropertyConstants {
    private PropertyConstants() {
    }

    /**
     * property fired when an agent is created
     */
    @NonNull
    public static final String NEW_AGENT = "new_agent";
    /**
     * property fired when an agent sends a message
     */
    @NonNull
    public static final String SEND_MESSAGE = "send_message";
    /**
     * property fired telling an agent to prepare to act
     */
    @NonNull
    public static final String PREPARE_TO_ACT = "prepare_to_act";
    /**
     * property fired to update the grid
     */
    @NonNull
    public static final String UPDATE_GRID = "update_grid";
    /**
     * property fired to increase time by a tick
     */
    @NonNull
    public static final String TIME_TICK = "time";
    /**
     * property fired when a task is complete
     */
    @NonNull
    public static final String TASK_COMPLETE = "task_complete";
    /**
     * property fired when a task is executed
     */
    @NonNull
    public static final String TASK_EXECUTE = "task_execute";
}
