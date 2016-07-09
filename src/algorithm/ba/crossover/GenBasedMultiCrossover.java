package algorithm.ba.crossover;

import java.util.List;
import java.util.Random;

import algorithm.ga.core.Chromosome;
import algorithm.ga.crossover.CrossoverMethod;
import util.Pair;


/**
 * 
 * Crossover among multiple parents.
 * 
 * @author acco
 * 
 * Jul 9, 2016 2:11:39 PM
 *
 */
public class GenBasedMultiCrossover implements MultiCrossoverMethod {

	private CrossoverMethod crossoverMethod;

	public GenBasedMultiCrossover(CrossoverMethod crossoverMethod) {
		this.crossoverMethod = crossoverMethod;
	}

	@Override
	public Chromosome apply(List<Chromosome> parents, int startIndex) {

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

		return chosen;
	}

}
