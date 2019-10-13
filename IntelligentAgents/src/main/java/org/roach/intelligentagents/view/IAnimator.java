package org.roach.intelligentagents.view;

public interface IAnimator extends Runnable {

	/**
	 * Initialize and start the simulation thread.
	 */
	void startSim();

	/**
	 * Called when all tasks have been found or when the user presses the
	 * Stop button.
	 */
	void stopSim();

	/**
	 * Sets isRunning to false, thus ending the main loop and the thread.
	 */
	void endProgram();
}