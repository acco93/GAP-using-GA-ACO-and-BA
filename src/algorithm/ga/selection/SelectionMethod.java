package algorithm.ga.selection;

import algorithm.ga.core.Genome;

/**
 * 
 * Methods implemented by classes that could interchangeably be used as GA selection methods.
 * 
 * @author acco
 * 
 * Jul 5, 2016 8:36:39 PM
 *
 */
public interface SelectionMethod {
	/**
	 * Given the genome (all the chromosomes of the population) returns the index of the chosen one.
	 * @param genome
	 * @return
	 */
	int apply(Genome genome);
}
