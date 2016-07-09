package algorithm.ba.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import algorithm.ba.crossover.GenBasedMultiCrossover;
import algorithm.ba.crossover.MultiCrossoverMethod;
import algorithm.ba.crossover.StandardMultiCrossover;
import algorithm.ga.core.Chromosome;
import algorithm.ga.core.Genome;
import algorithm.ga.crossover.DoublePointCrossover;
import algorithm.ga.crossover.SinglePointCrossover;
import algorithm.ga.crossover.UniformCrossover;
import controller.SharedAppData;
import logger.Logger;
import model.AppSettings;
import model.Instance;
import model.Result.PartialResult;
import solver.AbstractSolver;

/**
 * 
 * Bionomic solver, it tries to solve the instance using a bionomic approach.
 * 
 * @author acco
 * 
 *         Jul 8, 2016 3:43:48 PM
 *
 */
public class BASolverConcurrent extends AbstractSolver {

	private SharedAppData sd;
	private int maxIterations;
	private Genome genome;
	private int tasksNum;
	private double mutationProbability;
	private int maxInclusionFrequency;
	private double similarityConstant;
	private MultiCrossoverMethod crossoverMethod;

	public BASolverConcurrent(Instance instance, SharedAppData sd) {
		this.sd = sd;

		AppSettings s = AppSettings.get();

		this.maxIterations = s.baIterations;

		switch (s.baCrossoverMethod) {
		case MULTI_DOUBLE_POINT:
			this.crossoverMethod = new GenBasedMultiCrossover(new DoublePointCrossover());
			break;
		case MULTI_SINGLE_POINT:
			this.crossoverMethod = new GenBasedMultiCrossover(new SinglePointCrossover());
			break;
		case MULTI_UNIFORM:
			this.crossoverMethod = new GenBasedMultiCrossover(new UniformCrossover());
			break;
		case STANDARD:
			this.crossoverMethod = new StandardMultiCrossover();
			break;
		default:
			throw new IllegalStateException();
		}

		this.genome = new Genome(instance, s.baPopulation);

		this.maxInclusionFrequency = s.baMaxInclusionFrequency;
		this.similarityConstant = s.baSimilarityConstant;
		this.mutationProbability = s.baMutationProbability;

		this.tasksNum = s.threads;
	}

	@Override
	protected Optional<PartialResult> process() {
		int it = 0;

		Logger.get().baInfo("BIONOMIC ALGORITHM ............. ");

		debugWriteLine("Computing bionomic algorithm");

		double startTime = System.currentTimeMillis();

		while (it < this.maxIterations && !sd.isStopped()) {

			debugWriteLine("Iteration: " + it);
			debugWriteLine("Fitness mean: " + genome.getFitnessMean());
			debugWriteLine("Fitness variance: " + genome.getFitnessVariance());
			debugWriteLine("Fitness std: " + genome.getFitnessStandardDeviation());

			int current = 0;

			/*
			 * Compute the (dis)similarity function (concurrently).
			 */

			List<Future<Double>> dissimilarityTasks = new ArrayList<Future<Double>>();

			double[][] dissimilarity = new double[genome.getSize()][genome.getSize()];

			int increment = this.genome.getSize() / this.tasksNum;
			current = 0;
			for (int i = 0; i < this.tasksNum - 1; i++) {
				Future<Double> task = this.executor
						.submit(new DissimilarityTask(genome, dissimilarity, current, current + increment));
				dissimilarityTasks.add(task);
				current += increment;
			}

			dissimilarityTasks.add(
					this.executor.submit(new DissimilarityTask(genome, dissimilarity, current, this.genome.getSize())));

			/*
			 * Wait for the async computation
			 */
			double dissimilaritySum = 0;

			for (int i = 0; i < dissimilarityTasks.size(); i++) {
				try {
					dissimilaritySum += dissimilarityTasks.get(i).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			if (super.areThereDebuggers()) {
				debugWriteLine("Dissimilarity matrix:");
				for (int i = 0; i < this.genome.getSize(); i++) {
					for (int j = 0; j < this.genome.getSize(); j++) {
						debugWrite(String.format("%.3f ", dissimilarity[i][j]));
					}
					debugWriteLine("");
				}
			}

			/*
			 * Compute the avg & std avoiding the matrix diagonal...
			 */
			double dissimilarityAvg = dissimilaritySum
					/ (this.genome.getSize() * this.genome.getSize() - this.genome.getSize());
			double dissimilarityStd = 0;
			for (int i = 0; i < genome.getSize(); i++) {
				for (int j = 0; j < genome.getSize(); j++) {
					dissimilarityStd = Math.pow(dissimilarity[i][j] - dissimilarityAvg, 2);
				}
			}
			dissimilarityStd /= this.genome.getSize();
			dissimilarityStd = Math.sqrt(dissimilarityStd);

			debugWriteLine("Dissimilarity avg: " + dissimilarityAvg);
			debugWriteLine("Dissimilarity std: " + dissimilarityStd);

			/*
			 * Define the similarity threshold. Two solutions A, B are
			 * considered similar if dissimilarityA - dissimilarityB < delta
			 */
			double delta = dissimilarityAvg - this.similarityConstant * dissimilarityStd;

			debugWriteLine("Delta: " + delta);

			int[] inclusionFrequency = new int[this.genome.getSize()];
			for (int i = 0; i < this.genome.getSize(); i++) {
				/*
				 * the worst solution has inclusion frequency = 1 ... the best
				 * solution has inclusion frequency = maxInclusionFrequences
				 */
				inclusionFrequency[i] = (int) Math
						.ceil(((i - 1) * this.maxInclusionFrequency / (genome.getSize() - 1)) + 1);
			}

			List<Chromosome> offsprings = new ArrayList<>();

			while (offsprings.size() < this.genome.getSize()) {
				/*
				 * Parents set definition.
				 */
				List<Chromosome> parents = new ArrayList<>();

				/*
				 * Choose a random starting point.
				 */
				int index = new Random().nextInt(genome.getSize());
				int count = 0;

				/*
				 * Build an independent set of solutions.
				 */
				boolean finished = false;
				while (count <= genome.getSize() && !finished) {
					boolean skip = false;

					/*
					 * Check if it has already been taken in previous iterations
					 * or in the current one
					 */
					if (inclusionFrequency[index] == 0) {
						/*
						 * The solution can't be choosen again! Skip it
						 */
						skip = true;
					}
					/*
					 * Check if it is linked (similar) to one of the solution
					 * already present in the current parents set.
					 */
					for (int j = 0; j < parents.size() && !skip; j++) {
						if (dissimilarity[index][j] < delta) {
							skip = true;
						}
					}

					if (!skip) {
						/*
						 * This solution could be added :)
						 */

						if (offsprings.size() + parents.size() == genome.getSize()) {
							finished = true;
						} else {
							inclusionFrequency[index]--;
							parents.add(this.genome.getOrderedPopulation().get(index));
						}
					}

					index = (index + 1) % this.genome.getSize();
					count++;
				}

				/*
				 * The parents set is built now I've to generate some children
				 */

				debugWriteLine("Parents set cardinality: " + parents.size());

				List<Future<Chromosome>> crossoverTasks = new ArrayList<Future<Chromosome>>();
				for (int i = 0; i < parents.size(); i++) {
					// Future<Chromosome> task = this.executor
					// .submit(new CrossoverTask(i, parents, crossoverMethod,
					// this.mutationProbability));
					Future<Chromosome> task = this.executor
							.submit(new CrossoverTask(this.crossoverMethod, i, parents, this.mutationProbability));
					crossoverTasks.add(task);
				}

				for (int i = 0; i < crossoverTasks.size(); i++) {

					try {
						Chromosome chosen = crossoverTasks.get(i).get();

						offsprings.add(chosen);
						/*
						 * Update the fittest.
						 */
						if (chosen.fitnessCombination() > genome.getFittest().fitnessCombination()) {
							genome.setFittest(chosen);
						}
						/*
						 * Update the weakest.
						 */
						else if (chosen.fitnessCombination() < genome.getUnfittest().fitnessCombination()) {
							genome.setUnfittest(chosen);
						}

					} catch (InterruptedException | ExecutionException e) {

						e.printStackTrace();
					}
				}

			}

			/*
			 * Replace the population
			 */
			this.genome.setPopulation(offsprings);

			it++;

			debugWriteLine("");
		}

		double endTime = System.currentTimeMillis();

		Optional<PartialResult> result = Optional.empty();

		if (it == this.maxIterations) {
			result = Optional.of(new PartialResult(genome.getFittest().fitnessCombination(), endTime - startTime));
			Logger.get().baInfo("Fittest: " + genome.getFittest());
		} else {
			Logger.get().baInfo("K.O.");
		}

		return result;
	}

}
