package algorithm.ga.crossover;

import java.util.Random;

import algorithm.ga.core.Chromosome;
import model.Instance;
import util.Pair;

/**
 * 
 * Uniform crossover: for each genes, pick a gene from one parent or the other with the same probability.
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:26:38 PM
 *
 */
public class UniformCrossover implements CrossoverMethod {

	@Override
	public Pair<Chromosome, Chromosome> apply(Chromosome parentA, Chromosome parentB) {

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
		

		return new Pair<>(new Chromosome(instance, childA),new Chromosome(instance, childB));
	}

}
