package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

/**
 * 
 * Simple list row renderer.
 * 
 * @author acco
 * 
 * Jul 5, 2016 7:46:53 AM
 *
 */
public class RowRenderer extends JPanel implements ListCellRenderer<File> {


	private static final Color INDEX_FOREGROUND = new Color(228,224,223);

	private static final long serialVersionUID = 1L;
	
	private static final Color SELECTION_COLOR = new Color(158,196,188);
	public static final Color ODD_COLOR = new Color(197,230,186);
	public static final Color EVEN_COLOR = new Color(114,179,94);
	private static final Color INDEX_BACKGROUND = new Color(73,144,51);

	
	private JLabel nameLabel;
	private JLabel pathLabel;
	private JLabel indexLabel;

	public RowRenderer(){

		this.setLayout(new BorderLayout());
		
		JPanel internalPanel = new JPanel();
		internalPanel.setLayout(new BoxLayout(internalPanel, BoxLayout.Y_AXIS));
		internalPanel.setOpaque(false);

		
		nameLabel = new JLabel("");
		nameLabel.setBorder(new EmptyBorder(5,10,0,10));
		pathLabel = new JLabel("");
		pathLabel.setBorder(new EmptyBorder(0,10,5,10));
		pathLabel.setFont(new Font(pathLabel.getFont().getName(), Font.PLAIN, 10));
		
		this.setBackground(R.BACKGROUND_COLOR);
		
		internalPanel.add(nameLabel);
		internalPanel.add(pathLabel);

		this.add(internalPanel, BorderLayout.CENTER);
		
		indexLabel = new JLabel("");
		indexLabel.setOpaque(true);
		indexLabel.setForeground(INDEX_FOREGROUND);
		indexLabel.setBackground(INDEX_BACKGROUND);
		
		this.add(indexLabel, BorderLayout.WEST);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends File> list, File file, int index, boolean isSelected,
			boolean cellHasFocus) {
		
		this.nameLabel.setText(file.getName());
		Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
		this.nameLabel.setIcon(ico);
		
		this.pathLabel.setText(file.getAbsolutePath());
		
		this.indexLabel.setText(" "+index+" ");
		
		if(isSelected){
			this.setBackground(SELECTION_COLOR);
		} else {
			if(index%2==0){
				this.setBackground(R.BACKGROUND_COLOR);
			} else {
				this.setBackground(ODD_COLOR);
			}
			
		}
		
		return this;
	}
	
	

}
