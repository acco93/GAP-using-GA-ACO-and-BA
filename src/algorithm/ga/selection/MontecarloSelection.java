package algorithm.ga.selection;

import java.util.Random;

import algorithm.ga.core.Genome;

/**
 * 
 * Montecarlo selection (roulette wheel selection): chose a solution with a probability
 * proportional to its fitness
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:01:14 PM
 *
 */
public class MontecarloSelection implements SelectionMethod {

	@Override
	public int apply(Genome genome) {

		double th = new Random().nextDouble()*genome.getFitnessSum();

		int sum = 0;
		int i = 0;
		for (i = 0; i < genome.getSize(); i++) {
			sum +=  genome.getPopulation().get(i).fitness();
			if (sum >= th) {
				break;
			}
		}
		return i;
	}

}
