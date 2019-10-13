package org.roach.intelligentagents.view.console;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jdt.annotation.NonNull;
import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.controller.AgentApp;
import org.roach.intelligentagents.view.AAnimator;

public class ConsoleAnimator extends AAnimator implements PropertyChangeListener {

	public ConsoleAnimator(@NonNull final AgentApp agentApp) {
		super(agentApp);
		agentApp.getSimgrid().addPropertyChangeListener(this);
	}

	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			step();
		}
	}

	@Override
	public void step() {
		simUpdate(); // update sim state
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(PropertyConstants.TASK_COMPLETE)) {
			Integer numTasksComplete = (Integer) evt.getNewValue();
			if (numTasksComplete >= agentApp.getSimgrid().getNumTasks() * (float) agentApp.getPercentFinished()
					/ 100.0f) {
				System.out.println(agentApp.getSimgrid().getGridSize() + " " + agentApp.getSimgrid().getNumTasks() + " "
						+ agentApp.getSimgrid().getNumAgents() + " " + agentApp.getStrategyType() + " "
						+ AAnimator.getTime());
				endProgram();
			}
		}
	}

}
