package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import controller.Controller;

/**
 * 
 * File list panel
 * 
 * @author acco
 * 
 *         Jul 5, 2016 8:03:07 PM
 *
 */
public class FileSelectionPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<File> listModel;
	private JButton addButton;
	private JButton removeButton;
	private JMenuItem runSelected;
	private JMenuItem recordSelected;
	private JMenuItem removeSelected;
	private JMenuItem openSelected;
	private JList<File> list;
	private JLabel titleLabel;

	public FileSelectionPanel(Controller controller) {

		this.setLayout(new BorderLayout());
		this.setBackground(R.BACKGROUND_COLOR);
		titleLabel = new JLabel("FILE LIST (empty)", SwingUtilities.CENTER);
		this.add(titleLabel, BorderLayout.NORTH);

		listModel = new DefaultListModel<>();
		list = new JList<>(listModel);
		list.setOpaque(true);
		list.setCellRenderer(new RowRenderer());
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setOpaque(true);
		list.setBackground(R.BACKGROUND_COLOR);
		this.add(listScroll, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottomPanel.setOpaque(false);

		addButton = new JButton(R.ADD_ICON);
		addButton.setToolTipText("Add file(s)");
		addButton.setContentAreaFilled(false);
		addButton.addActionListener(this);
		bottomPanel.add(addButton);

		removeButton = new JButton(R.REMOVE_ICON);
		removeButton.setToolTipText("Remove selected file(s)");
		removeButton.setContentAreaFilled(false);
		removeButton.addActionListener(this);
		bottomPanel.add(removeButton);

		this.add(bottomPanel, BorderLayout.SOUTH);

		JPopupMenu jPopupMenu = new JPopupMenu() {

			private static final long serialVersionUID = 1L;

			@Override
			public void show(Component invoker, int x, int y) {

				int row = list.locationToIndex(new Point(x, y));

				if (row != -1) {
					// list.setSelectedIndex(row);
					super.show(invoker, x, y);
				}

			}
		};

		openSelected = new JMenuItem("Open", R.OPEN_ICON);
		openSelected.addActionListener((e) -> {
			int[] indices = list.getSelectedIndices();
			for (int i = 0; i < indices.length; i++) {
				try {
					Desktop.getDesktop().open(listModel.get(indices[i]));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		runSelected = new JMenuItem("Run selected", R.START_ICON_SMALL);
		runSelected.addActionListener((e) -> {
			int[] indices = list.getSelectedIndices();
			List<File> files = new ArrayList<>();
			for (int i = 0; i < indices.length; i++) {
				files.add(listModel.get(indices[i]));
			}
			controller.start(files);
		});

		recordSelected = new JMenuItem("Record selected", R.RECORD_ICON_SMALL);
		recordSelected.addActionListener((e) -> {
			int[] indices = list.getSelectedIndices();
			List<File> files = new ArrayList<>();
			for (int i = 0; i < indices.length; i++) {
				files.add(listModel.get(indices[i]));
			}
			controller.record(files);
		});

		removeSelected = new JMenuItem("Remove selected", R.REMOVE_ICON);
		removeSelected.addActionListener(this);

		jPopupMenu.add(openSelected);
		jPopupMenu.add(new JSeparator());
		jPopupMenu.add(runSelected);
		jPopupMenu.add(recordSelected);
		jPopupMenu.add(new JSeparator());
		jPopupMenu.add(removeSelected);

		list.setComponentPopupMenu(jPopupMenu);

	}

	@SuppressWarnings("unchecked")
	public List<File> getFilePaths() {
		return (List<File>) (Object) Arrays.asList(listModel.toArray());
	}

	public void disableInput() {
		this.addButton.setEnabled(false);
		this.removeButton.setEnabled(false);
		this.openSelected.setEnabled(false);
		this.runSelected.setEnabled(false);
		this.recordSelected.setEnabled(false);
		this.removeSelected.setEnabled(false);
	}

	public void enableInput() {
		this.addButton.setEnabled(true);
		this.openSelected.setEnabled(true);
		this.removeButton.setEnabled(true);
		this.runSelected.setEnabled(true);
		this.recordSelected.setEnabled(true);
		this.removeSelected.setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.removeButton || e.getSource() == this.removeSelected) {
			int count = list.getSelectedIndices().length;

			for (int i = 0; i < count; i++) {
				listModel.removeElementAt(list.getSelectedIndex());
			}

			if (listModel.size() == 0) {
				titleLabel.setText("FILE LIST (empty)");
			} else {
				titleLabel.setText("FILE LIST (" + listModel.size() + " files)");
			}
		} else if (e.getSource() == this.addButton) {
			JFileChooser chose = new JFileChooser();
			chose.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chose.setMultiSelectionEnabled(true);
			int r = chose.showOpenDialog(new JFrame());
			if (r == JFileChooser.APPROVE_OPTION) {
				for (File file : chose.getSelectedFiles()) {
					listModel.addElement(file);
				}

			}
			titleLabel.setText("FILE LIST (" + listModel.size() + " files)");
		}

	}

}
