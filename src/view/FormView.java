package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import controller.Controller;
import model.State;

public class FormView extends JFrame implements View {

	private static final int HEIGHT = 400;
	private static final int WIDTH = 650;

	

	private static final long serialVersionUID = 1L;
	private JLabel statusLabel;
	private FileSelectionPanel fsPanel;
	private ExecutionPanel ePanel;
	private ResultsPanel rPanel;
	private ParametersPanel pPanel;
	private ToolBar toolBar;

	public FormView(Controller controller) {

		this.setTitle("Heuristics (GA & ACO for GAP)");
		this.setSize(WIDTH, HEIGHT);

		JPanel mainPanel = new JPanel(new BorderLayout());

		statusLabel = new JLabel("Status: processing (65.3 s) ", SwingUtilities.CENTER);
		statusLabel.setOpaque(true);
		statusLabel.setBackground(new Color(108, 158, 150));
		statusLabel.setForeground(new Color(209, 227, 224));
		mainPanel.add(statusLabel, BorderLayout.SOUTH);

		JTabbedPane tabbedPane = new JTabbedPane();

		mainPanel.add(tabbedPane, BorderLayout.CENTER);

		HelpPanel hPanel = new HelpPanel();
		tabbedPane.addTab("Help & instructions", hPanel);

		fsPanel = new FileSelectionPanel();
		// tabbedPane.addTab("File selection", fsPanel);

		pPanel = new ParametersPanel();
		tabbedPane.addTab("Configurations", pPanel);

		ePanel = new ExecutionPanel(controller);
		// tabbedPane.addTab("Execution", ePanel);

		rPanel = new ResultsPanel(controller);
		
		JPanel resExecPanel = new JPanel(new BorderLayout());
		JPanel resExecInPanel = new JPanel(new GridLayout(1,1));
		
		toolBar = new ToolBar(controller);
			
		
		resExecPanel.add(toolBar,BorderLayout.NORTH);
		resExecPanel.add(resExecInPanel,BorderLayout.CENTER);
		
		resExecInPanel.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,rPanel, ePanel));
		
		
		JSplitPane a = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, fsPanel, resExecPanel);
		
		tabbedPane.addTab("File selection & execution", a);


		this.add(mainPanel);

		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);

		this.setVisible(true);
	}

	@Override
	public List<File> getFilePaths() {
		return fsPanel.getFilePaths();

	}

	@Override
	public void setState(State state) {
		this.statusLabel.setText("Status: " + state.toString());

	}

	@Override
	public void disableInput() {
		SwingUtilities.invokeLater(() -> {
			this.fsPanel.disableInput();
			this.pPanel.disableInput();
			this.rPanel.disableInput();
			this.toolBar.disableInput();
		});

	}

	@Override
	public void enableInput() {
		SwingUtilities.invokeLater(() -> {
			this.fsPanel.enableInput();
			this.pPanel.enableInput();
			this.rPanel.enableInput();
			this.toolBar.enableInput();
		});

	}

	@Override
	public void refreshResults() {
		SwingUtilities.invokeLater(() -> {
			this.rPanel.refreshResults();
		});

	}

}
