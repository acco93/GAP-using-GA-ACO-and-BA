package algorithm.ba.crossover;

import java.util.List;

import algorithm.ga.core.Chromosome;

/**
 * 
 * Given k parents and n jobs define a child using n/k agents from each parent
 * 
 * @author acco
 * 
 *         Jul 9, 2016 2:09:05 PM
 *
 */

public class StandardMultiCrossover implements MultiCrossoverMethod {

	@Override
	public Chromosome apply(List<Chromosome> parents, int startIndex) {

		int jobsNum = parents.get(0).getArray().length;

		int[] offspringArray = new int[jobsNum];
		int offspringIndex = 0;

		int jobsForParents = (int) Math.ceil((double) jobsNum / parents.size());

		int i = 0;

		while (offspringIndex < jobsNum) {

			int[] parentArray = parents.get(startIndex).getArray();

			for (int count = 0; count < jobsForParents && offspringIndex < jobsNum; i++, offspringIndex++, count++) {
				offspringArray[offspringIndex] = parentArray[i];
			}
			startIndex = (startIndex + 1) % parents.size();
		}

		Chromosome offspring = new Chromosome(parents.get(0).getInstance(), offspringArray);

		return offspring;
	}

}
