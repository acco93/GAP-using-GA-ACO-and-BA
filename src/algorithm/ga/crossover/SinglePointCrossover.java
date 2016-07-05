package algorithm.ga.crossover;

import java.util.Random;

import algorithm.ga.core.Chromosome;
import model.Instance;
import util.Pair;

/**
 * 
 * Single point crossover: exchange genes after a randomly chosen point.
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:26:11 PM
 *
 */
public class SinglePointCrossover implements CrossoverMethod {
	
	@Override
	public Pair<Chromosome, Chromosome> apply(Chromosome parentA, Chromosome parentB) {
		
		Instance instance = parentA.getInstance();
		int jobsNum = instance.getJobsNum();

		int[] arrayA = parentA.getArray();
		int[] arrayB = parentB.getArray();

		int[] childA = new int[jobsNum];
		int[] childB = new int[jobsNum];

		int point = new Random().nextInt(jobsNum);

		for (int i = 0; i < point; i++) {
			childA[i] = arrayA[i];
			childB[i] = arrayB[i];
		}

		for (int i = point; i < jobsNum; i++) {
			childA[i] = arrayB[i];
			childB[i] = arrayA[i];
		}


		return new Pair<>(new Chromosome(instance, childA),new Chromosome(instance, childB));
	}

}
