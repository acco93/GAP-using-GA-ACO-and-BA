package algorithm.ga.crossover;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import algorithm.ga.core.Chromosome;
import model.Instance;

public class DoublePointCrossover implements CrossoverMethod{

	@Override
	public List<Chromosome> apply(Chromosome parentA, Chromosome parentB) {
		
		Instance instance = parentA.getInstance();
		int jobsNum = instance.getJobsNum();
		
		int th1 = new Random().nextInt(jobsNum);
		int th2 = new Random().nextInt(jobsNum);
		
		if(th1 > th2){
			int tmp = th1;
			th1 = th2;
			th2 = tmp;
		}
		
		int[] arrayA = parentA.getArray();
		int[] arrayB = parentB.getArray();
		
		int[] childA = new int[jobsNum];
		int[] childB = new int[jobsNum];

		for (int i = 0; i < th1; i++) {
			childA[i] = arrayA[i];
			childB[i] = arrayB[i];
		}
		
		for(int i=th1;i<th2;i++){
			childA[i] = arrayB[i];
			childB[i] = arrayA[i];
		}
		
		for(int i=th2;i<jobsNum;i++){
			childA[i] = arrayA[i];
			childB[i] = arrayB[i];
		}
		
		List<Chromosome> offsprings = new ArrayList<>();
		offsprings.add(new Chromosome(instance, childA));
		offsprings.add(new Chromosome(instance, childB));
		
		return offsprings;
	}

}
