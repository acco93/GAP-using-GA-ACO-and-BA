package algorithm.bio.core;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import algorithm.ga.core.Chromosome;
import algorithm.ga.crossover.CrossoverMethod;
import util.Pair;

public class CrossoverTask implements Callable<Chromosome> {

	private int startIndex;
	private List<Chromosome> parents;
	private CrossoverMethod crossoverMethod;

	public CrossoverTask(int startIndex, List<Chromosome> parents, CrossoverMethod crossoverMethod){
		this.startIndex = startIndex;
		this.parents = parents;
		this.crossoverMethod = crossoverMethod;
	}
	
	@Override
	public Chromosome call() throws Exception {
		Chromosome chosen = parents.get(startIndex);
		int nextOffspring = startIndex + 1;
		int offspringCount = 0;
		
		/*
		 * Consider all the parents (but not me!).
		 */
		while (offspringCount < parents.size()) {
			/*
			 * Generate two children.
			 */
			Pair<Chromosome, Chromosome> children = this.crossoverMethod.apply(chosen,
					parents.get((nextOffspring) % parents.size()));
			/*
			 * Choose one randomly.
			 */
			if (new Random().nextBoolean()) {
				chosen = children.getFirst();
			} else {
				chosen = children.getSecond();
			}
			nextOffspring++;
			offspringCount++;
		}
		/*
		 * Eventually "chosen" is actually the chosen one!
		 */
		chosen.localSearch();
		return chosen;
	}

}
