package algorithm.ga.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
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
import solver.AbstractSolver;
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
public class GASolver extends AbstractSolver {

	private int populationSize;
	private double crossoverProbability;
	private double mutationProbability;

	private int maxIterations;

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

		this.genome = new Genome(instance, s.gaPopulation);

	}

	@Override
	protected Optional<PartialResult> process() {
		int i = 0;

		Logger.get().gaInfo("GENETIC ALGORITHM ............. ");

		debugWriteLine("Computing genetic algorithm");

		double startTime = System.currentTimeMillis();

		while (i < this.maxIterations && !sd.isStopped()) {

			debugWriteLine("Iteration: " + i);
			debugWriteLine("Fitness mean: " + genome.getFitnessMean());
			debugWriteLine("Fitness variance: " + genome.getFitnessVariance());
			debugWriteLine("Fitness std: " + genome.getFitnessStandardDeviation());

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

			debugWriteLine("");

			i++;
		}

		double endTime = System.currentTimeMillis();

		Optional<PartialResult> result = Optional.empty();

		if (i == this.maxIterations) {
			result = Optional.of(new PartialResult(genome.getFittest().fitnessCombination(), endTime - startTime));
			Logger.get().gaInfo("Fittest: " + genome.getFittest());
		} else {
			Logger.get().gaInfo("K.O.");
		}
		
		return result;
	}

}
