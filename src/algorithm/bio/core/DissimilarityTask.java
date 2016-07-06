package algorithm.bio.core;

import java.util.concurrent.Callable;

import algorithm.ga.core.Genome;

public class DissimilarityTask implements Callable<Double> {

	private int row;
	private Genome genome;
	private double[][] dissimilarity;
	private int jobsNum;

	public DissimilarityTask(Genome genome, double[][] dissimilarity, int row) {
		this.genome = genome;
		this.dissimilarity = dissimilarity;
		this.row = row;
		this.jobsNum = this.genome.getPopulation().get(0).getArray().length;
	}

	@Override
	public Double call() throws Exception {
		double partialSum = 0;

		for (int j = 0; j < this.genome.getSize(); j++) {
			int diff = 0;
			int[] arrayA = this.genome.getOrderedPopulation().get(this.row).getArray();
			int[] arrayB = this.genome.getOrderedPopulation().get(j).getArray();
			/*
			 * Increment diff each time a job is assigned to a different agent.
			 */
			for (int k = 0; k < jobsNum; k++) {
				if (arrayA[k] != arrayB[k]) {
					diff++;
				}
			}
			/*
			 * diff is normalized according to the jobs number.
			 */
			dissimilarity[this.row][j] = ((double) diff / jobsNum);

			partialSum += dissimilarity[this.row][j];
		}
		return partialSum;
	}

}
