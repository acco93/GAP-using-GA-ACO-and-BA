package algorithm.ga.selection;

import java.util.List;
import java.util.Random;

import algorithm.ga.core.Chromosome;
import algorithm.ga.core.Genome;

/**
 * 
 * Linear rank selection: it uses an ordered by increasing fitness population.
 * The worst solution has fitness 1 and the best N.
 * 
 * @author acco
 * 
 * Jul 6, 2016 1:25:49 PM
 *
 */
public class LinearRankSelection implements SelectionMethod {

	@Override
	public int apply(Genome genome) {
		List<Chromosome> population = genome.getOrderedPopulation();

		int fitnessSum = (population.size() * (population.size() + 1)) / 2;

		double th = new Random().nextInt(fitnessSum);

		int sum = 0;
		int i = 0;
		for (i = 0; i < population.size(); i++) {
			sum += i;
			if (sum >= th) {
				break;
			}
		}
		return i;

	}

}
