package org.roach.intelligentagents.view.swing;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.roach.intelligentagents.AgentAppOpts;
import org.roach.intelligentagents.model.strategy.AgentStrategy;

public class ConfigurationDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConfigurationDialog(final Frame frame, final boolean isModal, final AgentAppOpts options) {
		super(frame, isModal);
		setTitle("Intelligent Agents configuration");
		setLayout(new BorderLayout());
//		setBorder(BorderFactory.createTitledBorder("Parameters"));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		var strategies = findAllStrategies();
		var displayStrategies = strategies.stream().map(s -> s.getClass().getSimpleName()).toArray(String[]::new);
		var listbox = new JComboBox<>(displayStrategies);
		JTextArea roomSize = new JTextArea(Integer.toString(options.roomsize));
		JTextArea numAgents = new JTextArea(Integer.toString(options.agents));
		JTextArea numTasks = new JTextArea(Integer.toString(options.tasks));

		JButton okButton = new JButton("OK");
		okButton.addActionListener(e -> {
			options.strategy = strategies.get(listbox.getSelectedIndex()).getClass();
			options.agents = Integer.parseInt(numAgents.getText());
			options.roomsize = Integer.parseInt(roomSize.getText());
			options.tasks = Integer.parseInt(numTasks.getText());
			dispose();
		});
		JTextArea description = new JTextArea(3, 40);
		description.setEditable(false);
		
		ItemListener il = e -> description.setText(strategies.get(listbox.getSelectedIndex()).getDescription());
		il.itemStateChanged(null);
		listbox.addItemListener(il);
		
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		northPanel.add(listbox, BorderLayout.WEST);
		northPanel.add(description, BorderLayout.EAST);
		
		JPanel centerPanel = new JPanel();
		GridLayout gridLayout = new GridLayout(0, 2);
		gridLayout.setVgap(5);
		gridLayout.setHgap(5);
		centerPanel.setLayout(gridLayout);
		
		centerPanel.add(new JLabel("Grid size:"));
		centerPanel.add(roomSize);
		centerPanel.add(new JLabel("Number of agents:"));
		centerPanel.add(numAgents);
		centerPanel.add(new JLabel("Number of tasks:"));
		centerPanel.add(numTasks);
		
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(okButton, BorderLayout.SOUTH);
		pack();
	}
	
	public static void main(String[] args) {
		AgentAppOpts options = new AgentAppOpts();
		ConfigurationDialog dialog = new ConfigurationDialog(null, true, options);
		SwingUtilities.invokeLater(() -> {
			dialog.setVisible(true);
		});
		while (options.strategy == null) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(options);
	}
	
	private static List<AgentStrategy> findAllStrategies() {
		ServiceLoader<AgentStrategy> serviceLoader = ServiceLoader.load(AgentStrategy.class);
		List<AgentStrategy> list = new ArrayList<>();
		for (AgentStrategy s : serviceLoader) {
			list.add(s);
		}
		return list;
	}

}
