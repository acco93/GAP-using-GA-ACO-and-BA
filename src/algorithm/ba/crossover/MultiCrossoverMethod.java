package algorithm.ba.crossover;

import java.util.List;

import algorithm.ga.core.Chromosome;

/**
 * 
 * Perform the crossover operation among a group of parents.
 * 
 * @author acco
 * 
 *         Jul 9, 2016 2:04:22 PM
 *
 */
public interface MultiCrossoverMethod {
	Chromosome apply(List<Chromosome> parents, int startIndex);
}
