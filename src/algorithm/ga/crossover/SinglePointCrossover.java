package algorithm.ga.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algorithm.ga.core.Chromosome;
import model.Instance;

public class SinglePointCrossover implements CrossoverMethod {
	
	@Override
	public List<Chromosome> apply(Chromosome parentA, Chromosome parentB) {
		
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

		List<Chromosome> offsprings = new ArrayList<>();
		offsprings.add(new Chromosome(instance, childA));
		offsprings.add(new Chromosome(instance, childB));
		return offsprings;
	}

}
