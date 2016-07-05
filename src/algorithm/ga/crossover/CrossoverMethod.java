package algorithm.ga.crossover;

import algorithm.ga.core.Chromosome;
import util.Pair;

/**
 * 
 * Methods implemented by classes that could interchangeably be used as GA crossover methods.
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:04:00 PM
 *
 */
public interface CrossoverMethod {
	/**
	 * Given two parents returns two offspring.
	 * @param parentA
	 * @param parentB
	 * @return
	 */
	Pair<Chromosome,Chromosome> apply(Chromosome parentA, Chromosome parentB);
}
