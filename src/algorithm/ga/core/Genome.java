package algorithm.ga.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.AppSettings;
import model.Instance;

/**
 * 
 * The set of solutions & some statistics.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 9:27:57 PM
 *
 */
public class Genome {

	private int fitnessSum;
	private double fitnessVariance;
	private List<Chromosome> population;
	private List<Chromosome> orderedPopulation;
	private int size;
	private Chromosome fittest;
	private Chromosome unfittest;
	private Chromosome lastFittest;

	public Genome(Instance instance, int population) {
		this.size = population;

		this.fitnessSum = 0;
		this.fitnessVariance = 0;

		this.population = new ArrayList<>(this.size);
		this.orderedPopulation = new ArrayList<>(this.population.size());
		/*
		 * Generating the initial solutions.
		 */

		fittest = new Chromosome(instance);
		lastFittest = fittest;
		unfittest = new Chromosome(instance);

		this.population.add(fittest);
		this.population.add(unfittest);

		for (int i = 0; i < size - 2; i++) {
			Chromosome solution = new Chromosome(instance);
			this.population.add(solution);
			if (solution.fitnessCombination() > fittest.fitnessCombination()) {
				fittest = solution;
			} else if (solution.fitnessCombination() < unfittest.fitnessCombination()) {
				unfittest = solution;
			}
		}

		this.updateStatistics();
	}

	private void updateStatistics() {

		this.fitnessSum = this.population.stream().mapToInt(c -> c.fitness()).sum();

		double fitnessMean = fitnessSum / size;

		this.fitnessVariance = this.population.stream().mapToDouble(c -> Math.pow(c.fitness() - fitnessMean, 2)).sum()
				/ size;

		this.orderedPopulation = new ArrayList<>(this.population);

		/*
		 * Sort them in increasing order of fitness.
		 */
		Collections.sort(this.orderedPopulation, (a, b) -> {
			return Integer.compare(a.fitnessCombination(), b.fitnessCombination());
		});

	}

	public int getFitnessSum() {
		return fitnessSum;
	}

	public double getFitnessVariance() {
		return fitnessVariance;
	}

	public double getFitnessMean() {
		return fitnessSum / size;
	}

	public double getFitnessStandardDeviation() {
		return Math.sqrt(this.fitnessVariance);
	}

	public List<Chromosome> getPopulation() {
		return population;
	}

	public List<Chromosome> getOrderedPopulation() {
		return orderedPopulation;
	}

	public int getSize() {
		return size;
	}

	public Chromosome getFittest() {
		return fittest;
	}

	public Chromosome getUnfittest() {
		return unfittest;
	}

	public void setPopulation(List<Chromosome> population) {
		this.population = population;
		this.updateStatistics();
	}

	public void setFittest(Chromosome fittest) {
		this.fittest = fittest;
	}

	public void setUnfittest(Chromosome unfittest) {
		this.unfittest = unfittest;
	}

	public void elitism() {
		this.population.remove(this.unfittest);
		this.population.add(lastFittest);
		this.lastFittest = fittest;
	}

}
