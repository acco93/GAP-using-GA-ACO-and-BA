package algorithm.bio.core;

import java.util.concurrent.Callable;

import algorithm.ga.core.Genome;

public class DissimilarityTask implements Callable<Double> {

	private Genome genome;
	private double[][] dissimilarity;
	private int jobsNum;
	private int start;
	private int stop;

	public DissimilarityTask(Genome genome, double[][] dissimilarity, int start, int stop) {
		this.genome = genome;
		this.dissimilarity = dissimilarity;
		this.start = start;
		this.stop = stop;
		this.jobsNum = this.genome.getPopulation().get(0).getArray().length;
	}

	@Override
	public Double call() throws Exception {
		double partialSum = 0;

		for (int i = start; i < stop; i++) {
			for (int j = 0; j < this.genome.getSize(); j++) {
				int diff = 0;
				int[] arrayA = this.genome.getOrderedPopulation().get(i).getArray();
				int[] arrayB = this.genome.getOrderedPopulation().get(j).getArray();
				/*
				 * Increment diff each time a job is assigned to a different
				 * agent.
				 */
				for (int k = 0; k < jobsNum; k++) {
					if (arrayA[k] != arrayB[k]) {
						diff++;
					}
				}
				/*
				 * diff is normalized according to the jobs number.
				 */
				dissimilarity[i][j] = ((double) diff / jobsNum);

				partialSum += dissimilarity[i][j];
			}
		}

		return partialSum;
	}

}
