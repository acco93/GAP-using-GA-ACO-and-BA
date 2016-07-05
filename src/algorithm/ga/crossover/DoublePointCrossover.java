package algorithm.ga.crossover;

import java.util.Random;

import algorithm.ga.core.Chromosome;
import model.Instance;
import util.Pair;

/**
 * 
 * Double point crossover: chose two random points and exchange genes between parents
 * inside the chosen interval.
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:24:58 PM
 *
 */
public class DoublePointCrossover implements CrossoverMethod{

	@Override
	public Pair<Chromosome,Chromosome> apply(Chromosome parentA, Chromosome parentB) {
		
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

		return new Pair<>(new Chromosome(instance, childA),new Chromosome(instance, childB));
	}

}
