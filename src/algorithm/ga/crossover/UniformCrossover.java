package algorithm.ga.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algorithm.ga.core.Chromosome;
import model.Instance;

public class UniformCrossover implements CrossoverMethod {

	@Override
	public List<Chromosome> apply(Chromosome parentA, Chromosome parentB) {

		Instance instance = parentA.getInstance();
		int jobsNum = instance.getJobsNum();

		Random r = new Random();
		
		int[] arrayA = parentA.getArray();
		int[] arrayB = parentB.getArray();
		
		int[] childA = new int[jobsNum];
		int[] childB = new int[jobsNum];
		
		for(int i=0;i<jobsNum;i++){
			if(r.nextBoolean()){
				childA[i] = arrayB[i];
				childB[i] = arrayA[i];
			} else {
				childA[i] = arrayA[i];
				childB[i] = arrayB[i];
			}
		}
		
		List<Chromosome> offsprings = new ArrayList<>();
		offsprings.add(new Chromosome(instance, childA));
		offsprings.add(new Chromosome(instance, childB));
		return offsprings;
	}

}
