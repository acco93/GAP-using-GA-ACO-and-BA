package algorithm.ga.selection;

import java.util.Random;

import algorithm.ga.core.Genome;

/**
 * 
 * Simple tournament selection: chose the best solution from two randomly picked ones. 
 * 
 * @author acco
 * 
 * Jul 5, 2016 8:40:01 PM
 *
 */
public class TournamentSelection implements SelectionMethod {

	/*
	 * Probability to chose the better.
	 */
	private double k;

	public TournamentSelection() {
		this.k = 0.75;
	}

	@Override
	public int apply(Genome genome) {

		Random r = new Random();

		int index1 = r.nextInt(genome.getSize());
		int index2 = r.nextInt(genome.getSize());

		int fitter, weaker;
		
		if(genome.getPopulation().get(index1).fitness() > genome.getPopulation().get(index2).fitness()){
			fitter = index1;
			weaker = index2;
		} else {
			fitter = index2;
			weaker = index1;
		}
			
		if(r.nextDouble()<k){
			return fitter;
		} else {
			return weaker;
		}
		
	}

}
