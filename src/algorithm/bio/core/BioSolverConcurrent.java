package algorithm.bio.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import algorithm.ga.core.Chromosome;
import algorithm.ga.core.Genome;
import algorithm.ga.crossover.CrossoverMethod;
import algorithm.ga.crossover.DoublePointCrossover;
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

	public BioSolverConcurrent(Instance instance, SharedAppData sd) {
		this.sd = sd;

		AppSettings s = AppSettings.get();

		this.executor = Executors.newFixedThreadPool(s.threads);

		this.maxIterations = s.gaIterations;

		this.crossoverMethod = new DoublePointCrossover();

		this.genome = new Genome(instance);
	}

	public PartialResult solve() {

		List<Future<Boolean>> localSearchTasks = new ArrayList<Future<Boolean>>();
		List<Future<Double>> dissimilarityTasks = new ArrayList<Future<Double>>();
		List<Future<Chromosome>> crossoverTasks = new ArrayList<Future<Chromosome>>();
		int it = 0;

		Logger.get().baInfo("BIONOMIC ALGORITHM ............. ");

		double startTime = System.currentTimeMillis();

		while (it < this.maxIterations && !sd.isStopped()) {
			
			
			/*
			 * Start concurrent local searches.
			 */
			for (Chromosome c : this.genome.getPopulation()) {
				Future<Boolean> localSearchTask = this.executor.submit(() -> {
					c.localSearch();
					return true;
				});
				localSearchTasks.add(localSearchTask);
			}

			/*
			 * Wait for results.
			 */
			for (int i = 0; i < localSearchTasks.size(); i++) {
				try {
					localSearchTasks.get(i).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}

			/*
			 * Force the statistics update.
			 */
			this.genome.updateStatistics();

			/*
			 * Compute the (dis)similarity function (concurrently).
			 */

			double[][] dissimilarity = new double[genome.getSize()][genome.getSize()];
			double dissimilaritySum = 0;
			for (int i = 0; i < this.genome.getSize(); i++) {
				Future<Double> task = this.executor.submit(new DissimilarityTask(genome, dissimilarity, i));
				dissimilarityTasks.add(task);
				// if (it > 2500)
				// System.out.printf("%.2f", dissimilarity[i][j]);
				// dissimilaritySum += dissimilarity[i][j];

				// if (it > 2500)
				// System.out.println();
			}

			/*
			 * Wait for the async computation
			 */
			for (int i = 0; i < dissimilarityTasks.size(); i++) {
				try {
					dissimilaritySum += dissimilarityTasks.get(i).get();
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
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

			/*
			 * Define the similarity threshold. Two solutions A, B are
			 * considered similar if dissimilarityA - dissimilarityB < delta
			 */
			double delta = dissimilarityAvg - 0.7 * dissimilarityStd;
			// System.out.println("Delta " + delta);
			/*
			 * 
			 */

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

				// System.out.println("parents set size: " + parents.size());

				for (int i = 0; i < parents.size(); i++) {
					Future<Chromosome> task = this.executor.submit(new CrossoverTask(i, parents, crossoverMethod));
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

						chosen.mutation();
					} catch (InterruptedException | ExecutionException e) {

						e.printStackTrace();
					}
				}

			}

			// System.out.println("before replacing: " + offsprings.size());

			/*
			 * Replace the population
			 */
			this.genome.setPopulation(offsprings);

			it++;
		}

		Logger.get().baInfo("Fittest: " + genome.getFittest());

		double stopTime = System.currentTimeMillis();

		return null;
	}
}
