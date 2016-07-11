package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import controller.Controller;
import logger.LogViewer;
import logger.Logger;

/**
 * 
 * Log panel.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 8:04:26 PM
 *
 */
public class ExecutionPanel extends JPanel implements LogViewer {

	private static final long serialVersionUID = 1L;
	private StyledDocument doc;
	private JTextPane log;
	private Style errStyle;
	private Style infoStyle;
	private Style gaStyle;
	private Style antsStyle;
	private JScrollPane scroll;
	private Style baStyle;

	public ExecutionPanel(Controller controller) {

		Logger.get().addLogViewer(this);

		this.setLayout(new BorderLayout());
		this.setBackground(R.BACKGROUND_COLOR);

		JLabel titleLabel = new JLabel(":: EXECUTION LOG ::", SwingUtilities.CENTER);
		this.add(titleLabel, BorderLayout.NORTH);

		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setOpaque(false);
		this.add(bottomPanel);

		JButton clearButton = new JButton(R.CLEAR_ICON);
		clearButton.setToolTipText("Clear the log");
		clearButton.setContentAreaFilled(false);
		clearButton.addActionListener((e) -> {
			SwingUtilities.invokeLater(() -> {
				log.setText("");
			});
		});
		bottomPanel.add(clearButton);

		this.add(bottomPanel, BorderLayout.SOUTH);

		log = new JTextPane();
		log.setBorder(BorderFactory.createLoweredSoftBevelBorder());
		log.setEditable(false);
		log.setBackground(R.BACKGROUND_COLOR);
		doc = log.getStyledDocument();

		DefaultCaret caret = (DefaultCaret) log.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		errStyle = log.addStyle("Error style", null);
		StyleConstants.setForeground(errStyle, Color.white);
		StyleConstants.setBackground(errStyle, Color.red);

		infoStyle = log.addStyle("Info style", null);
		StyleConstants.setForeground(infoStyle, Color.black);

		gaStyle = log.addStyle("GA style", infoStyle);
		StyleConstants.setBackground(gaStyle, R.GA_BACKGROUND_COLOR);

		antsStyle = log.addStyle("ANTS style", infoStyle);
		StyleConstants.setBackground(antsStyle, R.ANTS_BACKGROUND_COLOR);

		baStyle = log.addStyle("BA style", infoStyle);
		StyleConstants.setBackground(baStyle, R.BA_BACKGROUND_COLOR);

		scroll = new JScrollPane(log);
		scroll.setOpaque(false);
		scroll.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(scroll, BorderLayout.CENTER);

	}

	private void append(String text, Style style) {
		try {
			doc.insertString(doc.getLength(), text + "\n", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void info(String text) {
		this.append(text, this.infoStyle);
	}

	@Override
	public void err(String text) {
		this.append(text, this.errStyle);
	}

	@Override
	public void gaInfo(String text) {
		this.append(text, this.gaStyle);
	}

	@Override
	public void antsInfo(String text) {
		this.append(text, this.antsStyle);
	}

	@Override
	public void baInfo(String text) {
		this.append(text, this.baStyle);

	}

}
