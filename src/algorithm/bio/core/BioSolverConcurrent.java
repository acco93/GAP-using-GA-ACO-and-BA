package algorithm.bio.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import algorithm.ga.core.Chromosome;
import algorithm.ga.core.Genome;
import algorithm.ga.crossover.CrossoverMethod;
import algorithm.ga.crossover.DoublePointCrossover;
import algorithm.ga.crossover.SinglePointCrossover;
import algorithm.ga.crossover.UniformCrossover;
import controller.SharedAppData;
import logger.Logger;
import model.AppSettings;
import model.Instance;
import model.Result.PartialResult;

public class BioSolverConcurrent {

	private SharedAppData sd;
	private ExecutorService executor;
	private int maxIterations;
	private Genome genome;
	private CrossoverMethod crossoverMethod;
	private int tasksNum;
	private double mutationProbability;

	public BioSolverConcurrent(Instance instance, SharedAppData sd) {
		this.sd = sd;

		AppSettings s = AppSettings.get();

		this.executor = Executors.newFixedThreadPool(s.threads);

		this.maxIterations = s.baIterations;

		switch (s.baCrossoverMethod) {
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

		this.genome = new Genome(instance, s.baPopulation);

		this.mutationProbability = s.baMutationProbability;

		this.tasksNum = s.threads;
	}

	public Optional<PartialResult> solve() {

		int it = 0;

		Logger.get().baInfo("BIONOMIC ALGORITHM ............. ");

		double startTime = System.currentTimeMillis();

		while (it < this.maxIterations && !sd.isStopped()) {

			int current = 0;

			/*
			 * Compute the (dis)similarity function (concurrently).
			 */

			// System.out.print("Computing dissimilarity matrix...");

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

//			for (int i = 0; i < this.genome.getSize(); i++) {
//				for (int j = 0; j < this.genome.getSize(); j++) {
//					System.out.printf("%.2f ", dissimilarity[i][j]);
//				}
//				System.out.println();
//			}

			// System.out.println("DONE");

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

			/*
			 * Define the similarity threshold. Two solutions A, B are
			 * considered similar if dissimilarityA - dissimilarityB < delta
			 */
			double delta = dissimilarityAvg - 0.7 * dissimilarityStd;

			// System.out.println("Inclusion frequencies:");
			int[] inclusionFrequency = new int[this.genome.getSize()];
			for (int i = 0; i < this.genome.getSize(); i++) {
				/*
				 * the worst solution has inclusion frequency = 1 ... the best
				 * solution has inclusion frequency = 5
				 */
				inclusionFrequency[i] = (int) Math.ceil(((i - 1) * 5 / (genome.getSize() - 1)) + 1);
				// System.out.println(inclusionFrequency[i]);
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


				// System.out.print("Crossing over...");
				List<Future<Chromosome>> crossoverTasks = new ArrayList<Future<Chromosome>>();
				for (int i = 0; i < parents.size(); i++) {
					Future<Chromosome> task = this.executor
							.submit(new CrossoverTask(i, parents, crossoverMethod, this.mutationProbability));
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
