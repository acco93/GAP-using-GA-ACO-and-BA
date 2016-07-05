package algorithm.ga.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import algorithm.ga.crossover.CrossoverMethod;
import algorithm.ga.selection.SelectionMethod;


public class GATask implements Callable<List<Chromosome>>{

	private SelectionMethod selection;
	private CrossoverMethod crossover;
	private double crossoverProbability;
	private double mutationProbability;

	private Genome genome;
	private List<Chromosome> population;

	public GATask(Genome genome, SelectionMethod selection, CrossoverMethod crossover, double crossoverProbability, double mutationProbability){
		this.genome = genome;
		this.selection = selection;
		this.crossover = crossover;
		this.crossoverProbability = crossoverProbability;
		this.mutationProbability = mutationProbability;
		
		this.population = this.genome.getPopulation();
	}
	

	@Override
	public List<Chromosome> call() throws Exception {
		
		/*
		 * Selection.
		 */
		Chromosome parentA = this.population.get(this.selection.apply(this.genome));
		Chromosome parentB = this.population.get(this.selection.apply(this.genome));
		
		/*
		 * Crossover.
		 */
		List<Chromosome> offsprings;
		if (new Random().nextDouble() < this.crossoverProbability) {
			offsprings = this.crossover.apply(parentA, parentB);
		} else {
			offsprings = new ArrayList<>();
			offsprings.add(parentA);
			offsprings.add(parentB);
		}
			
		
		for (Chromosome child : offsprings) {
			/*
			 * Mutation.
			 */
			if (new Random().nextDouble() < this.mutationProbability) {
				child.mutation();
			}
			/*
			 * Local search.
			 */
			child.localSearch();
		}
		
		
		
		return offsprings;
	}

}
