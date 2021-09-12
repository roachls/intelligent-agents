package org.roach.intelligentagents.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.PropertyConstants;
import org.roach.intelligentagents.model.strategy.AgentStrategy;

public class AgentFactory {
    private Class<? extends AgentStrategy> strategyType;
    private int numAgents;
    private AgentAppOpts options;
    private SimulationGrid simGrid;

    public List<Agent> build() {
	List<Agent> agents = new ArrayList<>();

	for (int i = 0; i < numAgents; i++) {
	    @Nullable
	    AgentStrategy strategy = null;
	    try {
		if (strategyType != null) {
		    Constructor<? extends AgentStrategy> strategyConst = strategyType.getConstructor(Agent.class,
			    SimulationGrid.class);
		    strategy = strategyConst.newInstance((Agent) null, simGrid);
		}
		if (strategy != null) {
		    Agent a = new Agent(strategy);
		    a.setSimGrid(simGrid);
		    agents.add(a);
		    strategy.setAgent(a);
		    strategy.setOptions(options);
		    a.firePropertyChange(PropertyConstants.NEW_AGENT, null, null);
		}
	    } catch (InstantiationException ex) {
		System.err.println("Unable to instantiate class.");
		ex.printStackTrace();
		System.exit(2);
	    } catch (IllegalAccessException ex) {
		System.err.println("Illegal access exception.");
		ex.printStackTrace();
		System.exit(3);
	    } catch (NoSuchMethodException e) {
		e.printStackTrace();
		System.exit(4);
	    } catch (SecurityException e) {
		e.printStackTrace();
		System.exit(5);
	    } catch (IllegalArgumentException e) {
		e.printStackTrace();
		System.exit(6);
	    } catch (InvocationTargetException e) {
		e.printStackTrace();
		System.exit(7);
	    }
	}
	return agents;
    }

    public void setSimGrid(SimulationGrid simGrid) {
	this.simGrid = simGrid;
    }

    public void setOptions(AgentAppOpts options) {
	this.options = options;
    }

    public void setStrategyType(Class<? extends AgentStrategy> strategyType) {
	this.strategyType = strategyType;
    }

    public void setNumAgents(int numAgents) {
	this.numAgents = numAgents;
    }

}
