package algorithm.ga.selection;

import java.util.Random;

import algorithm.ga.core.Genome;

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
