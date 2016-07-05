package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class FileSelectionPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private DefaultListModel<File> listModel;
	private JButton addButton;
	private JButton removeButton;

	public FileSelectionPanel() {

		this.setLayout(new BorderLayout());
		this.setBackground(R.BACKGROUND_COLOR);
		JLabel titleLabel = new JLabel("FILE LIST (empty)", SwingUtilities.CENTER);
		this.add(titleLabel,BorderLayout.NORTH);

		listModel = new DefaultListModel<>();
		JList<File> list = new JList<>(listModel);
		list.setOpaque(true);
		list.setCellRenderer(new RowRenderer());
		JScrollPane listScroll = new JScrollPane(list);
		listScroll.setOpaque(true);
		list.setBackground(R.BACKGROUND_COLOR);
		this.add(listScroll, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottomPanel.setOpaque(false);
		
		try {
			BufferedImage addButtonIcon = ImageIO.read(new File("icons/Add.png"));
			addButton = new JButton(new ImageIcon(addButtonIcon));
			addButton.setToolTipText("Add file(s)");
			//addButton.setBorder(BorderFactory.createEmptyBorder());
			addButton.setContentAreaFilled(false);
			addButton.addActionListener((e) -> {
				JFileChooser chose = new JFileChooser();
				chose.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chose.setMultiSelectionEnabled(true);
				int r = chose.showOpenDialog(new JFrame());
				if (r == JFileChooser.APPROVE_OPTION) {
					for (File file : chose.getSelectedFiles()) {
						listModel.addElement(file);
					}

				}
				titleLabel.setText("FILE LIST ("+listModel.size()+" files)");
			});
			bottomPanel.add(addButton);
			
			BufferedImage removeButtonIcon = ImageIO.read(new File("icons/Remove.png"));
			removeButton = new JButton(new ImageIcon(removeButtonIcon));
			removeButton.setToolTipText("Remove selected file(s)");
			//removeButton.setBorder(BorderFactory.createEmptyBorder());
			removeButton.setContentAreaFilled(false);
			removeButton.addActionListener((e) -> {
				int count = list.getSelectedIndices().length;

				for (int i = 0; i < count; i++) {
					listModel.removeElementAt(list.getSelectedIndex());
				}
				
				if(listModel.size()==0){
					titleLabel.setText("FILE LIST (empty)");
				} else {
					titleLabel.setText("FILE LIST ("+listModel.size()+" files)");				
				}
			});
			bottomPanel.add(removeButton);
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		
		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	@SuppressWarnings("unchecked")
	public List<File> getFilePaths() {
		return (List<File>)(Object)Arrays.asList(listModel.toArray());
	}

	public void disableInput() {
		this.addButton.setEnabled(false);
		this.removeButton.setEnabled(false);
	}

	public void enableInput() {
		this.addButton.setEnabled(true);
		this.removeButton.setEnabled(true);
	}
	
}
