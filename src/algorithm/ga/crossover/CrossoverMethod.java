package algorithm.ga.crossover;

import java.util.List;

import algorithm.ga.core.Chromosome;

public interface CrossoverMethod {
	List<Chromosome> apply(Chromosome parentA, Chromosome parentB);
}
