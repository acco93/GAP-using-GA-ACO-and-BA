package algorithm.ga.core;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import algorithm.ga.crossover.CrossoverMethod;
import algorithm.ga.selection.SelectionMethod;
import util.Pair;

/**
 * 
 * GA step (parents to offsprings) implemented as a logical task (async computation)
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:28:49 PM
 *
 */
public class GATask implements Callable<Pair<Chromosome, Chromosome>> {

	private SelectionMethod selection;
	private CrossoverMethod crossover;
	private double crossoverProbability;
	private double mutationProbability;

	private Genome genome;
	private List<Chromosome> population;

	public GATask(Genome genome, SelectionMethod selection, CrossoverMethod crossover, double crossoverProbability,
			double mutationProbability) {
		this.genome = genome;
		this.selection = selection;
		this.crossover = crossover;
		this.crossoverProbability = crossoverProbability;
		this.mutationProbability = mutationProbability;

		this.population = this.genome.getPopulation();
	}

	@Override
	public Pair<Chromosome, Chromosome> call() throws Exception {

		/*
		 * Selection.
		 */
		Chromosome parentA = this.population.get(this.selection.apply(this.genome));
		Chromosome parentB = this.population.get(this.selection.apply(this.genome));

		/*
		 * Crossover.
		 */
		Pair<Chromosome, Chromosome> offsprings;
		if (new Random().nextDouble() < this.crossoverProbability) {
			offsprings = this.crossover.apply(parentA, parentB);
		} else {
			offsprings = new Pair<>(parentA, parentB);
		}

		/*
		 * Mutation.
		 */
		if (new Random().nextDouble() < this.mutationProbability) {
			offsprings.getFirst().mutation();
			offsprings.getSecond().mutation();
		}
		/*
		 * Local search.
		 */
		offsprings.getFirst().localSearch();
		offsprings.getSecond().localSearch();

		return offsprings;
	}

}
