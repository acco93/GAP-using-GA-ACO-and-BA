package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * 
 * Compound component factory.
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:28:26 AM
 *
 */
public class ViewFactory {

	public static final Color BORDER_COLOR = new Color(197,230,186);
	
	public static JPanel configPanel(String title, JComponent component, String hint) {

		JPanel panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS) );
		
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topPanel.setOpaque(false);
		topPanel.add(new JLabel(title));
		topPanel.add(component);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		bottomPanel.setOpaque(false);
		bottomPanel.add(new InfoLabel(hint));
		
		panel.add(topPanel);
		panel.add(bottomPanel);
		
		Border matteBorder = BorderFactory.createMatteBorder(0, 5, 5, 5, BORDER_COLOR);
		Border border = BorderFactory.createCompoundBorder(matteBorder, new EmptyBorder(5,5,5,5));
		
		panel.setBorder(border);
		
		return panel;
	}

	public static class InfoLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		public InfoLabel(String string) {
			this.setText(string);
			this.setFont(new Font(this.getFont().getName(), Font.PLAIN, 11));


		}

	}


	public static Component titleLabel(String string) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		panel.setOpaque(true);
		panel.setBackground(BORDER_COLOR);
		JLabel label = new JLabel(string);

		panel.add(label);
		
		return panel;
	}

}
