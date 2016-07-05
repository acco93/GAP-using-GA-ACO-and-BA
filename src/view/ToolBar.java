package view;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JToolBar;

import controller.Controller;
import io.SaveResultsTask;

/**
 * 
 * Toolbar component.
 * 
 * @author acco
 * 
 * Jul 5, 2016 7:44:49 AM
 *
 */
public class ToolBar extends JToolBar {

	private static final long serialVersionUID = 1L;
	
	private JButton startButton;
	private JButton stopButton;
	private JButton exportButton;

	public ToolBar(Controller controller) {

		this.setFloatable(false);
		this.setBackground(R.BACKGROUND_COLOR);

		/*
		 * Start button.
		 */
		startButton = new JButton(R.START_ICON);
		startButton.setToolTipText("Start processing");
		startButton.setContentAreaFilled(false);

		startButton.addActionListener((e) -> {
			stopButton.setEnabled(true);
			controller.start();
		});
		this.add(startButton);

		/*
		 * Stop button.
		 */
		stopButton = new JButton(R.STOP_ICON);
		stopButton.setEnabled(false);
		stopButton.setToolTipText("Stop processing");
		stopButton.setContentAreaFilled(false);

		stopButton.addActionListener((e) -> {
			controller.stop();
			startButton.setEnabled(true);
		});
		this.add(stopButton);

		/*
		 * Separator.
		 */
		this.add(Box.createHorizontalGlue());


		/*
		 * Export button.
		 */
		exportButton = new JButton(R.EXPORT_ICON);
		exportButton.setToolTipText("Export results as CSV");
		exportButton.setContentAreaFilled(false);

		exportButton.addActionListener((e) -> {
			new Thread(new SaveResultsTask(controller.getResults()));
		});
		this.add(exportButton);

	}

	/**
	 * Disable all buttons while processing.
	 */
	public void disableInput() {
		this.startButton.setEnabled(false);
		this.exportButton.setEnabled(false);
		this.stopButton.setEnabled(true);
	}

	/**
	 * Re-enable them once finished.
	 */
	public void enableInput() {
		this.startButton.setEnabled(true);
		this.exportButton.setEnabled(true);
		this.stopButton.setEnabled(false);
	}

}
