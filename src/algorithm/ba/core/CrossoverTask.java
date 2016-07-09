package algorithm.ba.core;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import algorithm.ba.crossover.MultiCrossoverMethod;
import algorithm.ga.core.Chromosome;

/**
 * 
 * Crossover among multiple parents.
 * 
 * @author acco
 * 
 *         Jul 8, 2016 3:44:37 PM
 *
 */
public class CrossoverTask implements Callable<Chromosome> {

	private int startIndex;
	private List<Chromosome> parents;
	private double mutationProbability;
	private MultiCrossoverMethod crossoverMethod;

	public CrossoverTask(MultiCrossoverMethod crossoverMethod, int startIndex, List<Chromosome> parents,
			double mutationProbability) {
		this.crossoverMethod = crossoverMethod;
		this.startIndex = startIndex;
		this.parents = parents;
		this.mutationProbability = mutationProbability;
	}

	@Override
	public Chromosome call() throws Exception {

		Chromosome offspring = this.crossoverMethod.apply(parents, startIndex);

		if (new Random().nextDouble() < this.mutationProbability) {
			offspring.mutate();
		}

		offspring.localSearch();

		return offspring;
	}

}
