package org.roach.intelligentagents.controller;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.AgentFactory;
import org.roach.intelligentagents.model.SimulationGrid;
import org.roach.intelligentagents.model.strategy.AgentStrategy;
import org.roach.intelligentagents.view.swing.GUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

/**
 * Main entry point of application
 */
@SpringBootApplication
@PropertySource("classpath:IntelligentAgents.properties")
public class SpringMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringMain.class);

    @Value("${percentFinished}")
    private int percentFinished;

    @Value("${strategyType}")
    private String strategyType;

    @Value("${gridSize:50}")
    private int gridSize;

    @Value("${numTasks}")
    private int numTasks;

    @Value("${numAgents:15}")
    private int numAgents;

    /**
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder(SpringMain.class).web(WebApplicationType.NONE).headless(false)
                .bannerMode(Banner.Mode.OFF).run(args);
    }

    /**
     * @return the {@link Class} of strategy
     */
    @SuppressWarnings("unchecked")
    @Bean
    public Class<? extends AgentStrategy> strategyClass() {
        try {
            return (Class<? extends AgentStrategy>) Class.forName(strategyType);
        } catch (ClassNotFoundException e) {
            LOGGER.atError().setCause(e).setMessage("Unable to find {}").addArgument(() -> strategyType).log();
            return null;
        }
    }

    /**
     * @return the application
     */
    @Bean
    public AgentApp agentApp() {
        AgentApp agentApp = new AgentApp();
        agentApp.setPercentFinished(percentFinished);
        agentApp.setStrategyType(strategyClass());
        agentApp.setSimgrid(simGrid());
        return agentApp;
    }

    /**
     * @return the simulation grid
     */
    @Bean
    public SimulationGrid simGrid() {
        SimulationGrid simGrid = new SimulationGrid(gridSize, numTasks);
        simGrid.initTaskGrid();
        AgentFactory agentFactory = agentFactory();
        agentFactory.setSimGrid(simGrid);
        simGrid.setAgents(agentFactory.build());
        return simGrid;
    }

    /**
     * @return the UI
     */
    @Bean
    public GUI gui() {
        AgentApp agentApp = agentApp();
        GUI gui = new GUI(agentApp, 3, gridSize, false);
        gui.setShowGraphics(true);
        gui.setShowHelperGraphics(true);
        agentApp.getSimgrid().addPropertyChangeListener(gui);
        return gui;
    }

    /**
     * @return options
     */
    @Bean
    public AgentAppOpts options() {
        AgentAppOpts options = new AgentAppOpts();
        options.commTime = 6;
        return options;
    }

    /**
     * @return the factory that makes agents
     */
    @Bean
    public AgentFactory agentFactory() {
        AgentFactory factory = new AgentFactory();
        factory.setNumAgents(numAgents);
        factory.setOptions(options());
        factory.setStrategyType(strategyClass());
        return factory;
    }
}
