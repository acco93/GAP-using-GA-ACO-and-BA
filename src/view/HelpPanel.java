package view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class HelpPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String HELP_TEXT = "..:: HOW IT WORKS ::..\n"
			+ "\na. The application works with files in the format described in:\n--> http://people.brunel.ac.uk/~mastjjb/jeb/orlib/gapinfo.html\n"
			+ "\nProblem files are downloadable from: \n--> http://people.brunel.ac.uk/~mastjjb/jeb/orlib/files/ "
			+ "under the name: gap*.txt\n" + "\nb. Add one or more files in the File Selection tab\n"
			+ "\nc. Review (& edit) the algorithms parameters in the Configurations tab\n"
			+ "\nd. Run the algorithms in the Execution tab\n" + "\ne. Get the results in the Results tab!\n"
			+ "\n..:: PROBLEM NOTES ::..\n" + "\na. GAP is solved as a maximization problem.\n"
			+ "\n..:: GA NOTES ::..\n" + "\nA lot of ideas have been taken from: \n"
			+ "P.C.Chu and J.E.Beasley (1996) A genetic algorithm for the generalised assignment problem.\n"
			+ "\nLocal searches performed:\n" + "\n..:: ANTS NOTES ::..\n";

	private StyledDocument doc;

	public HelpPanel() {

		this.setLayout(new BorderLayout());
		this.setBackground(R.BACKGROUND_COLOR);

		JLabel titleLabel = new JLabel("Generalized Assignment Problem using GA & ANTS", SwingUtilities.CENTER);
		titleLabel.setOpaque(false);
		this.add(titleLabel, BorderLayout.NORTH);

		/*
		 * JEditorPane textArea = new JEditorPane();
		 * textArea.setBackground(FormView.BACKGROUND_COLOR);
		 * textArea.setEditable(false); textArea.setText(HELP_TEXT);
		 * textArea.setSize(400, 400);
		 */

		JTextPane textArea = new JTextPane();
		textArea.setBackground(R.BACKGROUND_COLOR);
		textArea.setEditable(false);
		// textArea.setText(HELP_TEXT);

		doc = textArea.getStyledDocument();

		Style titleStyle = textArea.addStyle("Title style", null);
		StyleConstants.setBackground(titleStyle, new Color(247, 200, 207));
		StyleConstants.setBold(titleStyle, true);
		//StyleConstants.setBackground(titleStyle, new Color(161, 57, 71));

		Style linkStyle = textArea.addStyle("Link style", null);
		StyleConstants.setBackground(linkStyle, new Color(158, 196, 188));
		StyleConstants.setItalic(linkStyle, true);
		//StyleConstants.setForeground(linkStyle, new Color(209, 228, 224));

		Style paperStyle = textArea.addStyle("Paper style", null);
		StyleConstants.setBackground(paperStyle, new Color(255, 226, 207));
		StyleConstants.setItalic(paperStyle, true);
		
		
		this.append("..:: HOW IT WORKS ::..", titleStyle);
		this.newLine();
		this.append("a) Add one or more files (see the file structure section below) in the File Selection tab", null);
		this.append("b) Review (& edit) the algorithms parameters in the Configurations tab", null);
		this.append("c) Run the algorithms in the Execution tab", null);
		this.append("d) Get the results in the Results tab!",null);
		this.newLine();
		
		this.append("..:: FILE STRUCTURE ::..", titleStyle);
		this.newLine();
		this.append("The application works with files in the format described in:", null);
		this.append("http://people.brunel.ac.uk/~mastjjb/jeb/orlib/gapinfo.html", linkStyle);
		this.newLine();
		this.append("They are downloadable from:", null);
		this.append("http://people.brunel.ac.uk/~mastjjb/jeb/orlib/files/", linkStyle);
		this.append("under the name: gap*.txt", null);
		this.newLine();
		
		this.append("..:: PROBLEM NOTES ::..", titleStyle);
		this.newLine();
		this.append("GAP is solved as a maximization problem", null);
		this.newLine();
		
		this.append("..:: GA NOTES ::..", titleStyle);
		this.newLine();
		this.append("A lot of ideas have been taken from:", null);
		this.append("P.C.Chu and J.E.Beasley (1996) A genetic algorithm for the generalised assignment problem", paperStyle);
		this.newLine();
		
		this.append("..:: ANTS NOTES ::..", titleStyle);
		this.newLine();
		
		this.append("..:: APP NOTES ::..", titleStyle);
		this.newLine();
		this.append("Public repository available at:",null);
		this.append("https://bitbucket.org/acco93/gap-using-heuristics",linkStyle);
		this.newLine();
		
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setOpaque(false);
		scroll.setBorder(new EmptyBorder(20, 20, 20, 20));

		this.add(scroll, BorderLayout.CENTER);
	}

	private void newLine() {
		this.append("", null);

	}

	private void append(String text, Style style) {
		try {
			doc.insertString(doc.getLength(), text + "\n", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
