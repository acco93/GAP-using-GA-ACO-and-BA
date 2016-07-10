package view;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import controller.Controller;
import io.ExportResultsTask;

/**
 * 
 * Toolbar component.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 7:44:49 AM
 *
 */
public class ToolBar extends JToolBar {

	private static final long serialVersionUID = 1L;

	private JButton startButton;
	private JButton stopButton;
	private JButton exportButton;

	private JButton recordButton;

	public ToolBar(Controller controller) {

		this.setFloatable(false);
		this.setBackground(R.BACKGROUND_COLOR);

		/*
		 * Start button.
		 */
		startButton = new JButton(R.START_ICON);
		startButton.setToolTipText("Start batch processing");
		startButton.setContentAreaFilled(false);

		startButton.addActionListener((e) -> {
			stopButton.setEnabled(true);
			recordButton.setEnabled(false);
			controller.start();
		});
		this.add(startButton);

		/*
		 * Record button.
		 */
		recordButton = new JButton(R.RECORD_ICON);
		recordButton.setToolTipText(
				"Process & write detailed log information to file (please make sure to use few iterations & small population)");
		recordButton.setContentAreaFilled(false);

		recordButton.addActionListener((e) -> {
			int reply = JOptionPane.showConfirmDialog(null,
					"Would you like to record detailed information about the runs?\nThe resulting file will be rather huge, please make sure you've\nchosen a reasonable amount of iterations. The file will be created\nin your current directory and will be automatically opened once\nthe computation is finished.\n\nWould you like to continue?",
					"Warning", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
				stopButton.setEnabled(true);
				startButton.setEnabled(false);
				controller.record();
			} else {

			}

		});
		this.add(recordButton);

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
			if (controller.getResults().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Mmmmm no results?!", "Ops... Nothing to export",
						JOptionPane.WARNING_MESSAGE);
			} else {
				new Thread(new ExportResultsTask(controller.getResults())).start();
			}
		});
		this.add(exportButton);

	}

	/**
	 * Disable all buttons while processing.
	 */
	public void disableInput() {
		this.startButton.setEnabled(false);
		this.exportButton.setEnabled(false);
		this.recordButton.setEnabled(false);
		this.stopButton.setEnabled(true);
	}

	/**
	 * Re-enable them once finished.
	 */
	public void enableInput() {
		this.startButton.setEnabled(true);
		this.exportButton.setEnabled(true);
		this.recordButton.setEnabled(true);
		this.stopButton.setEnabled(false);
	}

}
