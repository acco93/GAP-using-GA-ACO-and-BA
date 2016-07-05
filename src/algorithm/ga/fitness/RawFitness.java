package algorithm.ga.fitness;

import algorithm.ga.core.Genome;

public class RawFitness implements FitnessEvaluation {

	@Override
	public double evaluate(Genome genome, int fitness) {
		return fitness;
	}

}
