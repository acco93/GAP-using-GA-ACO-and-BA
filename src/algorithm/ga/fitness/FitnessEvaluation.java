package algorithm.ga.fitness;

import algorithm.ga.core.Genome;

public interface FitnessEvaluation {
	double evaluate(Genome genome, int fitness);
}
