package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import algorithm.ga.crossover.CrossoverType;
import algorithm.ga.selection.SelectionType;
import model.AppSettings;

/**
 * 
 * Parameters panel.
 * 
 * @author acco
 * 
 * Jul 5, 2016 8:10:30 AM
 *
 */
public class ParametersPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JSpinner gaIterations;

	private JSpinner gaCrossoverProbability;

	private JSpinner gaMutationProbability;

	private JCheckBox gaElitism;

	private CrossoverType gaCrossoverMethod = CrossoverType.SINGLE_POINT;

	private JSpinner threads;

	private JSpinner runs;

	private JSpinner gaPopulation;

	private SelectionType gaSelectionMethod;

	private JSpinner antsIterations;

	private JSpinner antsPopulation;

	private JSpinner antsAlpha;

	private JSpinner antsBeta;

	private JSpinner antsRho;

	private JRadioButton montecarlo;

	private JRadioButton tournament;

	private JRadioButton singlePoint;

	private JRadioButton doublePoint;

	private JRadioButton uniform;

	private JButton resetButton;

	private JButton applyButton;

	private JRadioButton rank;

	public ParametersPanel() {

		this.setLayout(new BorderLayout());

		/*
		 * General panel
		 */
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.add(ViewFactory.titleLabel("GENERAL"));
		mainPanel.setOpaque(true);
		mainPanel.setBackground(R.BACKGROUND_COLOR);

		/*
		 * Row 1: threads.
		 */
		String threadsTitle = "Threads:";
		threads = new JSpinner(new SpinnerNumberModel(Runtime.getRuntime().availableProcessors(), 1, 32, 1));
		String threadsHint = "Threads used during the computation (default: available processors+1)";
		mainPanel.add(ViewFactory.configPanel(threadsTitle, threads, threadsHint));

		/*
		 * Row 2: runs.
		 */
		String runsTitle = "Runs:";
		runs = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
		String runsHint = "# of executions for each problem";
		mainPanel.add(ViewFactory.configPanel(runsTitle, runs, runsHint));

		/*
		 * GA panel.
		 */
		
		mainPanel.add(ViewFactory.titleLabel("GENETIC ALGORITHM"));


		/*
		 * Row 1: iterations
		 */
		String iterationsTitle = "Iterations:";
		gaIterations = new JSpinner(new SpinnerNumberModel(1000, 100, 1000000, 100));
		String iterationsHint = "Iterations performed (min:100; max:1 000 000)";
		mainPanel.add(ViewFactory.configPanel(iterationsTitle, gaIterations, iterationsHint));

		/*
		 * Population
		 */
		String populationTitle = "Population:";
		gaPopulation = new JSpinner(new SpinnerNumberModel(200, 10, 1000, 2));
		String populationHint = "# of chromosomes";
		mainPanel.add(ViewFactory.configPanel(populationTitle, gaPopulation, populationHint));
		
		/*
		 * Row 2: crossover probability
		 */
		String crossoverProbTitle = "Crossover probability:";
		gaCrossoverProbability = new JSpinner(new SpinnerNumberModel(0.9, 0.5, 1, 0.01));
		String crossoverProbHint = "# of chromosomes";
		mainPanel.add(ViewFactory.configPanel(crossoverProbTitle, gaCrossoverProbability, crossoverProbHint));

		/*
		 * Row 3: elitism
		 */
		String elitismTitle = "Elitism:";
		gaElitism = new JCheckBox();
		gaElitism.setOpaque(false);
		gaElitism.setSelected(true);
		String elitismHint = "Copy the fittest solution of the previous iteration to the new population replacing the current worst one";
		mainPanel.add(ViewFactory.configPanel(elitismTitle, gaElitism, elitismHint));

		
		/*
		 * Selection method
		 */

		ButtonGroup selectionMethods = new ButtonGroup();
		montecarlo = new JRadioButton("Montecarlo");
		montecarlo.setSelected(true);
		montecarlo.setOpaque(false);
		montecarlo.addActionListener((e) -> {
			gaSelectionMethod = SelectionType.MONTECARLO;
		});
		tournament = new JRadioButton("Tournament");
		tournament.setOpaque(false);

		tournament.addActionListener((e) -> {
			gaSelectionMethod = SelectionType.TOURNAMENT;
		});
		rank = new JRadioButton("(Linear) rank");
		rank.setOpaque(false);

		rank.addActionListener((e) -> {
			gaSelectionMethod = SelectionType.RANK;
		});
		
		selectionMethods.add(montecarlo);
		selectionMethods.add(tournament);
		selectionMethods.add(rank);
		
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new BoxLayout(selectionPanel,BoxLayout.Y_AXIS));
		selectionPanel.setOpaque(false);
		selectionPanel.add(montecarlo);
		selectionPanel.add(new InfoLabel("Select a chromosome proportionally to its fitness"));
		selectionPanel.add(tournament);
		selectionPanel.add(new InfoLabel("Pick 2 random chromosome and select the fittest with a high probability"));
		selectionPanel.add(rank);
		selectionPanel.add(new InfoLabel("Select an individual considering its rank rather than the raw fitness. It tries to avoid premature convergence."));

		String selectionTitle = "Selection method:";
		mainPanel.add(ViewFactory.configPanel(selectionTitle, selectionPanel, null));


		/*
		 * Row 4: crossover methods
		 */

		ButtonGroup crossoverMethods = new ButtonGroup();
		singlePoint = new JRadioButton("Single point");
		singlePoint.setSelected(true);
		singlePoint.setOpaque(false);
		singlePoint.addActionListener((e) -> {
			gaCrossoverMethod = CrossoverType.SINGLE_POINT;
		});
		doublePoint = new JRadioButton("Double point");
		doublePoint.setOpaque(false);
		doublePoint.addActionListener((e) -> {
			gaCrossoverMethod = CrossoverType.DOUBLE_POINT;
		});
		uniform = new JRadioButton("Uniform");
		uniform.setOpaque(false);
		uniform.addActionListener((e) -> {
			gaCrossoverMethod = CrossoverType.UNIFORM;
		});
		crossoverMethods.add(singlePoint);
		crossoverMethods.add(doublePoint);
		crossoverMethods.add(uniform);

		JPanel crossoverPanel = new JPanel();
		crossoverPanel.setLayout(new BoxLayout(crossoverPanel, BoxLayout.Y_AXIS));
		
		crossoverPanel.setOpaque(false);
		crossoverPanel.add(singlePoint);
		crossoverPanel.add(new InfoLabel("Exchange parents genes after a randomly choosen position"));
		crossoverPanel.add(doublePoint);
		crossoverPanel.add(new InfoLabel("Exchange parents genes between two randomly choosen positions"));
		crossoverPanel.add(uniform);
		crossoverPanel.add(new InfoLabel("Pick random genes from the parents with equal probability"));
		
		String crossoverTitle = "Crossover method:";
		mainPanel.add(ViewFactory.configPanel(crossoverTitle, crossoverPanel, null));
		

		/*
		 * Row 5: mutation probability
		 */
		
		String mutationProbTitle = "Mutation probability:";
		gaMutationProbability = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1, 0.01));
		gaMutationProbability.setPreferredSize(new Dimension(50, 20));
		String mutationProbHint = "Mutation probabiliy (min: 0.0; max:1)";
		mainPanel.add(ViewFactory.configPanel(mutationProbTitle, gaMutationProbability, mutationProbHint));


		/*
		 * ANTS
		 */

		mainPanel.add(ViewFactory.titleLabel("ANTS ALGORITHM"));
	

		/*
		 * Iterations
		 */
		
		antsIterations = new JSpinner(new SpinnerNumberModel(1000, 100, 1000000, 100));
		mainPanel.add(ViewFactory.configPanel(iterationsTitle, antsIterations, iterationsHint));

		/*
		 * Population
		 */
		
		antsPopulation = new JSpinner(new SpinnerNumberModel(200, 10, 1000, 2));
		String antPopulationHint = "# of chromosomes";
		mainPanel.add(ViewFactory.configPanel(populationTitle, gaPopulation, antPopulationHint));
		

		/*
		 * Alpha
		 */

		String alphaTitle = "\u03B1";
		antsAlpha = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1, 0.01));
		antsAlpha.setPreferredSize(new Dimension(50, 20));
		antsAlpha.addChangeListener((e)->{
			antsBeta.setValue(1.0-(double)antsAlpha.getValue());
		});
		String alphaHint = "Attractiveness Importance";
		mainPanel.add(ViewFactory.configPanel(alphaTitle, antsAlpha, alphaHint));

		/*
		 * Beta
		 */

		String betaTitle = "\u03B2";
		antsBeta = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1, 0.01));
		antsBeta.setEnabled(false);
		antsBeta.setPreferredSize(new Dimension(50, 20));
		String betaHint = "Trace importance";
		mainPanel.add(ViewFactory.configPanel(betaTitle, antsBeta, betaHint));

		/*
		 * Rho
		 */

		String rhoTitle = "\u03C1";
		antsRho = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 1, 0.01));
		antsRho.setPreferredSize(new Dimension(50, 20));
		String rhoHint = "Evaporation factor (0: remove all; 1:retain all)";
		mainPanel.add(ViewFactory.configPanel(rhoTitle, antsRho, rhoHint));
		

		JScrollPane scroll = new JScrollPane(mainPanel);
		scroll.setOpaque(true);
		this.add(scroll, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new GridLayout(1, 2));

		resetButton = new JButton("Reset default settings");
		resetButton.addActionListener((e)->{
			AppSettings.resetDefault();
			this.loadSettings();
		});
		bottomPanel.add(resetButton);

		applyButton = new JButton("Apply");
		applyButton.addActionListener((e)->{
			this.updateSettings();
			applyButton.setText("Successfully updated!!!");
			applyButton.setBackground(new Color(114,179,94));
			applyButton.setForeground(new Color(228,244,223));
			new Timer().schedule(new TimerTask(){

				@Override
				public void run() {
					SwingUtilities.invokeLater(()->{
						applyButton.setText("Apply");
						applyButton.setBackground(null);
						applyButton.setForeground(null);
					});
					
					
				}}, 3000);
		});
		bottomPanel.add(applyButton);

		this.add(bottomPanel, BorderLayout.SOUTH);
		
		this.loadSettings();
	}


	public class InfoLabel extends JLabel {

		private static final long serialVersionUID = 1L;

		public InfoLabel(String string) {
			this.setText(string);
			this.setFont(new Font(this.getFont().getName(), Font.PLAIN, 11));
			
			
		}

	}

	private void loadSettings() {

		AppSettings s = AppSettings.get();

		this.threads.setValue(s.threads);
		this.runs.setValue(s.runs);

		this.gaIterations.setValue(s.gaIterations);
		this.gaPopulation.setValue(s.gaPopulation);
		this.gaCrossoverProbability.setValue(s.gaCrossoverProbability);
		
		this.gaElitism.setSelected(s.gaElitism);
		this.gaSelectionMethod = s.gaSelectionMethod;
		switch (gaSelectionMethod) {
		case MONTECARLO:
			montecarlo.doClick();
			break;
		case TOURNAMENT:
			tournament.doClick();
			break;
		case RANK:
			rank.doClick();
		default:
			break;
		}
		this.gaCrossoverMethod = s.gaCrossoverMethod;
		switch(gaCrossoverMethod){
		case DOUBLE_POINT:
			doublePoint.doClick();
			break;
		case SINGLE_POINT:
			singlePoint.doClick();
			break;
		case UNIFORM:
			uniform.doClick();
			break;
		default:
			break;}
		
		this.gaMutationProbability.setValue(s.gaMutationProbability);
		
		/*
		 * ANTS
		 */
		
		this.antsIterations.setValue(s.antsIterations);
		this.antsPopulation.setValue(s.antsPopulation);
		this.antsAlpha.setValue(s.antsAlpha);
		this.antsBeta.setValue(s.antsBeta);
		this.antsRho.setValue(s.antsRho);
	}
	
	private void updateSettings() {
		
		AppSettings s = AppSettings.get();

		s.threads = (int) this.threads.getValue();
		s.runs = (int) this.runs.getValue();

		s.gaIterations = (int) this.gaIterations.getValue();
		s.gaPopulation = (int) this.gaPopulation.getValue();
		s.gaCrossoverProbability = (double) this.gaCrossoverProbability.getValue();
		
		s.gaElitism = this.gaElitism.isSelected();
		this.gaSelectionMethod = s.gaSelectionMethod;
		
		this.gaCrossoverMethod = s.gaCrossoverMethod;
			
		s.gaMutationProbability = (double) this.gaMutationProbability.getValue();
		
		/*
		 * ANTS
		 */
		
		s.antsIterations = (int) this.antsIterations.getValue();
		s.antsPopulation = (int) this.antsPopulation.getValue();
		s.antsAlpha = (double) this.antsAlpha.getValue();
		s.antsBeta = (double) this.antsBeta.getValue();
		s.antsRho = (double) this.antsRho.getValue();
		
	}

	public void disableInput() {
		this.resetButton.setEnabled(false);
		this.applyButton.setEnabled(false);
	}

	public void enableInput() {
		this.resetButton.setEnabled(true);
		this.applyButton.setEnabled(true);
		
	}
	
}
