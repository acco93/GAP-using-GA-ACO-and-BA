package algorithm.bio.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import algorithm.ga.core.Chromosome;
import algorithm.ga.core.Genome;
import algorithm.ga.crossover.SinglePointCrossover;
import controller.SharedAppData;
import logger.Logger;
import model.AppSettings;
import model.Instance;
import model.Result.PartialResult;
import util.Pair;

public class BioSolver {

	private Instance instance;
	private SharedAppData sd;
	private ExecutorService executor;
	private int maxIterations;
	private Genome genome;
	private SinglePointCrossover crossoverMethod;

	public BioSolver(Instance instance, SharedAppData sd) {
		this.instance = instance;
		this.sd = sd;

		AppSettings s = AppSettings.get();

		this.executor = Executors.newFixedThreadPool(s.threads);

		this.maxIterations = s.gaIterations;

		this.crossoverMethod = new SinglePointCrossover();

		this.genome = new Genome(instance);
	}

	public PartialResult solve() {

		int it = 0;

		Logger.get().gaInfo("BIONOMIC ALGORITHM ............. ");

		double startTime = System.currentTimeMillis();

		while (it < this.maxIterations && !sd.isStopped()) {
			System.out.println("Iteration: " + it);
			/*
			 * Perform local searches.
			 */
			for (Chromosome c : this.genome.getPopulation()) {
				c.localSearch();
			}
			/*
			 * Force the statistics update.
			 */
			this.genome.updateStatistics();

			/*
			 * Compute the (dis)similarity function.
			 */

			double[][] dissimilarity = new double[genome.getSize()][genome.getSize()];
			double dissimilaritySum = 0;
			for (int i = 0; i < this.genome.getSize(); i++) {
				for (int j = 0; j < this.genome.getSize(); j++) {
					int diff = 0;
					int[] arrayA = this.genome.getOrderedPopulation().get(i).getArray();
					int[] arrayB = this.genome.getOrderedPopulation().get(j).getArray();
					/*
					 * Increment diff each time a job is assigned to a different
					 * agent.
					 */
					for (int k = 0; k < this.instance.getJobsNum(); k++) {
						if (arrayA[k] != arrayB[k]) {
							diff++;
						}
					}
					/*
					 * diff is normalized according to the jobs number.
					 */
					dissimilarity[i][j] = ((double) diff / this.instance.getJobsNum());
					// if (it > 2500)
					// System.out.printf("%.2f", dissimilarity[i][j]);
					dissimilaritySum += dissimilarity[i][j];
				}
				// if (it > 2500)
				// System.out.println();
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
			System.out.println("Delta " + delta);
			/*
			 * 
			 */

			System.out.println("Inclusion frequencies:");
			int[] inclusionFrequency = new int[this.genome.getSize()];
			for (int i = 0; i < this.genome.getSize(); i++) {
				/*
				 * worst solution has inclusion frequency = 1 ... best solution
				 * has inclusion frequency = 4
				 */
				inclusionFrequency[i] = (int) Math.ceil(((i - 1) * 4 / (genome.getSize() - 1)) + 1);
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

				System.out.println("parents set size: " + parents.size());

				for (int i = 0; i < parents.size(); i++) {
					int iOffspring = i + 1;
					int offspringCount = 0;
					Chromosome chosen = parents.get(i);
					/*
					 * Consider all the parents (but not me!).
					 */
					while (offspringCount < parents.size()) {
						/*
						 * Generate two children.
						 */
						Pair<Chromosome, Chromosome> children = this.crossoverMethod.apply(chosen,
								parents.get((iOffspring) % parents.size()));
						/*
						 * Choose one randomly.
						 */
						if (new Random().nextBoolean()) {
							chosen = children.getFirst();
						} else {
							chosen = children.getSecond();
						}
						iOffspring++;
						offspringCount++;
					}
					/*
					 * Eventually "chosen" is actually the chosen one!
					 */
					chosen.localSearch();
					offsprings.add(chosen);

					/*
					 * Update fittest & unfittest
					 */
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
				}

			}

			System.out.println("before replacing: " + offsprings.size());

			/*
			 * Replace the population
			 */
			this.genome.setPopulation(offsprings);

			it++;
		}

		Logger.get().gaInfo("Fittest: " + genome.getFittest());

		double stopTime = System.currentTimeMillis();

		return null;
	}
}
