package algorithm.bio.core;

import java.util.concurrent.Callable;

import algorithm.ga.core.Genome;

public class LocalSearchTask implements Callable<Boolean> {

	private Genome genome;
	private int start;
	private int stop;

	public LocalSearchTask(Genome genome, int start, int stop) {
		this.genome = genome;
		this.start = start;
		this.stop = stop;
	}

	@Override
	public Boolean call() {

		for (int i = start; i < stop; i++) {
			this.genome.getPopulation().get(i).localSearch();
		}

		return true;
	}

}
