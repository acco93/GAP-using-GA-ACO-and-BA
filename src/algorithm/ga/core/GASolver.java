package algorithm.ga.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import algorithm.ga.crossover.CrossoverMethod;
import algorithm.ga.crossover.DoublePointCrossover;
import algorithm.ga.crossover.SinglePointCrossover;
import algorithm.ga.crossover.UniformCrossover;
import algorithm.ga.selection.LinearRankSelection;
import algorithm.ga.selection.MontecarloSelection;
import algorithm.ga.selection.SelectionMethod;
import algorithm.ga.selection.TournamentSelection;
import controller.SharedAppData;
import logger.Logger;
import model.AppSettings;
import model.Instance;
import model.Result.PartialResult;
import util.Pair;

/**
 * 
 * The GA solver sets up some parameters according to the preferred user
 * configuration then splits the problem into logical tasks processed by a fixed
 * pool of threads.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 9:29:27 PM
 *
 */
public class GASolver {

	private int populationSize;
	private double crossoverProbability;
	private double mutationProbability;

	private int maxIterations;

	private ExecutorService executor;

	private CrossoverMethod crossoverMethod;

	private SelectionMethod selectionMethod;

	private boolean elitism;

	private SharedAppData sd;

	private Genome genome;

	public GASolver(Instance instance, SharedAppData sd) {

		this.sd = sd;

		/*
		 * Read & set parameters.
		 */

		AppSettings s = AppSettings.get();

		this.executor = Executors.newFixedThreadPool(s.threads);

		switch (s.gaSelectionMethod) {
		case MONTECARLO:
			this.selectionMethod = new MontecarloSelection();
			break;
		case TOURNAMENT:
			this.selectionMethod = new TournamentSelection();
			break;
		case RANK:
			this.selectionMethod = new LinearRankSelection();
			break;
		default:
			throw new IllegalStateException();
		}

		switch (s.gaCrossoverMethod) {
		case DOUBLE_POINT:
			this.crossoverMethod = new DoublePointCrossover();
			break;
		case SINGLE_POINT:
			this.crossoverMethod = new SinglePointCrossover();
			break;
		case UNIFORM:
			this.crossoverMethod = new UniformCrossover();
			break;
		default:
			throw new IllegalStateException();
		}

		this.populationSize = s.gaPopulation;
		this.crossoverProbability = s.gaCrossoverProbability;
		this.mutationProbability = s.gaMutationProbability;
		this.elitism = s.gaElitism;

		this.maxIterations = s.gaIterations;

		this.genome = new Genome(instance);

	}

	public PartialResult solve() {
		int i = 0;

		Logger.get().gaInfo("GENETIC ALGORITHM ............. ");

		double startTime = System.currentTimeMillis();

		while (i < this.maxIterations && !sd.isStopped()) {

			// System.out.println(genome.getFitnessStandardDeviation());

			List<Chromosome> children = new ArrayList<Chromosome>();
			LinkedList<Future<Pair<Chromosome, Chromosome>>> tasks = new LinkedList<>();

			/*
			 * Submit the task.
			 */
			for (int p = 0; p < this.populationSize; p += 2) {
				Future<Pair<Chromosome, Chromosome>> future = executor.submit(new GATask(genome, selectionMethod,
						crossoverMethod, crossoverProbability, mutationProbability));
				tasks.add(future);
			}

			/*
			 * Retrieve the results.
			 */
			for (Future<Pair<Chromosome, Chromosome>> task : tasks) {
				try {

					Pair<Chromosome, Chromosome> offsprings = task.get();
					children.add(offsprings.getFirst());
					children.add(offsprings.getSecond());

					Chromosome[] offspringsTmp = new Chromosome[] { offsprings.getFirst(), offsprings.getSecond() };

					for (Chromosome child : offspringsTmp) {
						/*
						 * Update the fittest.
						 */
						if (child.fitnessCombination() > genome.getFittest().fitnessCombination()) {
							genome.setFittest(child);
						}
						/*
						 * Update the weakest.
						 */
						else if (child.fitnessCombination() < genome.getUnfittest().fitnessCombination()) {
							genome.setUnfittest(child);
						}
					}
				} catch (InterruptedException | ExecutionException e) {

				}

			}

			genome.setPopulation(children);

			if (elitism) {
				genome.elitism();
			}

			i++;
		}

		double endTime = System.currentTimeMillis();

		Logger.get().gaInfo("Fittest: " + genome.getFittest());
		// Logger.get().info("Weakest: " + this.weakest);

		PartialResult result = new PartialResult(genome.getFittest().fitnessCombination(), endTime - startTime);

		return result;
	}

}
