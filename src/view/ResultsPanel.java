package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import controller.Controller;
import model.Result;

/**
 * 
 * JTable wrapper.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 7:58:59 AM
 *
 */
public class ResultsPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private TableModel tableModel;
	private JLabel resultsLabel;
	private JButton clearButton;
	private Controller controller;

	public ResultsPanel(Controller controller) {
		this.controller = controller;

		this.setBackground(R.BACKGROUND_COLOR);

		this.setLayout(new BorderLayout());

		resultsLabel = new JLabel("..:: RESULTS & STATISTICS (empty) ::..", SwingConstants.CENTER);
		this.add(resultsLabel, BorderLayout.NORTH);

		tableModel = new TableModel(controller);

		JTable table = new JTable(tableModel);
		table.setDefaultRenderer(Object.class, new CellRenderer());

		JScrollPane scroll = new JScrollPane(table);
		scroll.setOpaque(false);
		scroll.setBorder(new EmptyBorder(5, 5, 5, 5));

		scroll.getViewport().setBackground(R.BACKGROUND_COLOR);
		this.add(scroll, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setOpaque(false);

		clearButton = new JButton(R.CLEAR_ICON);
		clearButton.setToolTipText("Definitely clear current results");
		clearButton.setContentAreaFilled(false);
		clearButton.addActionListener((e) -> {
			controller.clearResults();
			this.refreshResults();
		});
		bottomPanel.add(clearButton);

		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	/**
	 * Refresh the jtable as soon as new results are available.
	 */
	public void refreshResults() {
		this.tableModel.refreshModel();
		tableModel.fireTableDataChanged();
		int size = controller.getResults().size();
		if (size == 0) {
			this.resultsLabel.setText("..:: RESULTS & STATISTICS (empty) ::..");
		} else {
			this.resultsLabel.setText("..:: RESULTS & STATISTICS (" + controller.getResults().size() + ") ::..");
		}

	}

	/**
	 * Disable buttons while processing.
	 */
	public void disableInput() {
		this.clearButton.setEnabled(false);
	}

	/**
	 * Re-enable them once finished
	 */
	public void enableInput() {
		this.clearButton.setEnabled(true);
	}

	/**
	 * 
	 * Simple Table model implementation as inner class.
	 * 
	 * @author acco
	 * 
	 * Jul 5, 2016 8:04:26 AM
	 *
	 */
	public class TableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private String[] columnNames = { "Instance", "Runs", "GA best value", "GA avg value", "GA avg time (ms)",
				"ANTS Best value", "ANTS avg value", "ANTS avg time (ms)" };

		private List<Result> list;

		private Controller controller;

		public TableModel(Controller controller) {
			this.controller = controller;
			this.refreshModel();

		}

		/**
		 * Get the results (map), transform them into a list in order to display them in fixed order.
		 */
		public void refreshModel() {
			Collection<Result> collection = this.controller.getResults().values();
			this.list = new ArrayList<Result>(collection);
			Collections.sort(list, (a, b) -> {
				return a.getInstance().getLogicalName().compareTo(b.getInstance().getLogicalName());
			});
		}

		@Override
		public int getRowCount() {
			return list.size();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex > this.list.size() - 1) {
				return null;
			}
			Result result = this.list.get(rowIndex);
			switch (columnIndex) {
			case 0:
				return result.getInstance().getLogicalName();
			case 1:
				return result.getRuns();
			case 2:
				return result.getGaBestValue();
			case 3:
				return result.getGaAvgValue();
			case 4:
				return result.getGaAvgTime();
			case 5:
				return result.getAntsBestValue();
			case 6:
				return result.getAntsAvgValue();
			case 7:
				return result.getAntsAvgTime();
			default:
				return null;
			}

		}

	}

	/**
	 * JTable Cell renderer as inner class.
	 * 
	 * @author acco
	 * 
	 * Jul 5, 2016 8:06:48 AM
	 *
	 */
	public class CellRenderer extends JLabel implements TableCellRenderer {

		
		
		private static final long serialVersionUID = 1L;

		public CellRenderer() {
			this.setOpaque(true);
			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			this.setText(value.toString() + " ");

			if (isSelected) {
				this.setBackground(new Color(189, 128, 171));
			} else {
				if (column >= 2 && column <= 4) {
					this.setBackground(R.GA_BACKGROUND_COLOR);
				} else if (column > 4 & column <= 7) {
					this.setBackground(R.ANTS_BACKGROUND_COLOR);
				} else {
					this.setBackground(R.BACKGROUND_COLOR);
				}
			}

			return this;
		}

	}

}
