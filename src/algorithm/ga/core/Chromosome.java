package algorithm.ga.core;

import java.util.Arrays;
import java.util.Random;

import model.Instance;

/**
 * 
 * Solution representation.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 9:31:41 PM
 *
 */

public class Chromosome {

	private static final int PENALTY = 10;

	private Instance instance;
	private int[] array;

	private int fitness;
	private int unfitness;

	public Chromosome(Instance instance, int[] array) {
		this.instance = instance;
		this.array = array;

		this.evaluate();
	}

	public Chromosome(Instance instance) {
		this.instance = instance;

		Random r = new Random();
		this.array = new int[instance.getJobsNum()];
		for (int i = 0; i < instance.getJobsNum(); i++) {
			this.array[i] = r.nextInt(instance.getAgentsNum());
		}

		this.evaluate();
	}

	public int[] getArray() {
		return this.array;
	}

	private void evaluate() {
		this.fitness = 0;
		this.unfitness = 0;

		int[][] c = this.instance.getCosts();

		for (int i = 0; i < this.instance.getJobsNum(); i++) {
			this.fitness += c[array[i]][i];
		}

		int[][] r = this.instance.getResoucesNeeded();
		int[] b = this.instance.getAgentsCapacity();
		int[] rSum = new int[this.instance.getAgentsNum()];

		for (int a = 0; a < this.instance.getAgentsNum(); a++) {
			rSum[a] = 0;
		}

		for (int j = 0; j < this.instance.getJobsNum(); j++) {
			rSum[this.array[j]] += r[this.array[j]][j];
		}

		for (int a = 0; a < this.instance.getAgentsNum(); a++) {
			this.unfitness += Math.max(0, rSum[a] - b[a]);
		}

	}

	public int fitness() {
		return this.fitness;
	}

	public int unfitness() {
		return this.unfitness;
	}

	public boolean localSearch() {
		int[][] c = this.instance.getCosts();
		int[][] r = this.instance.getResoucesNeeded();
		int[] rSum = new int[this.instance.getAgentsNum()];
		int[] b = this.instance.getAgentsCapacity();
		for (int a = 0; a < this.instance.getAgentsNum(); a++) {
			rSum[a] = 0;
		}
		for (int j = 0; j < this.instance.getJobsNum(); j++) {
			rSum[this.array[j]] += r[this.array[j]][j];
		}

		for (int a = 0; a < this.instance.getAgentsNum(); a++) {
			if (rSum[a] > b[a]) {
				/*
				 * Pick a randomly selected job assigned to the agent and try to
				 * assign it to another one without violating the capacity
				 * constraint.
				 */
				/*
				 * Pick the first one! :)
				 */
				int jobIndex = 0;
				while (array[jobIndex] != a) {
					jobIndex++;
				}
				/*
				 * Try to assign the job to another agent: from a+1, ..., a-1
				 */
				int i = 1;
				boolean assigned = false;
				while (i < this.instance.getAgentsNum() && !assigned) {
					int newAgentIndex = (a + i) % this.instance.getAgentsNum();
					if (rSum[newAgentIndex] + r[newAgentIndex][jobIndex] <= b[newAgentIndex]) {
						/*
						 * The agent could take the task! Before messing up
						 * indexes update rSum because it is used later!
						 * 
						 */
						int oldAgentIndex = this.array[jobIndex];
						rSum[oldAgentIndex] -= r[oldAgentIndex][jobIndex];
						rSum[newAgentIndex] += r[newAgentIndex][jobIndex];
						/*
						 * (Incrementally) Update the fitness.
						 */
						this.fitness -= c[oldAgentIndex][jobIndex];
						this.fitness += c[newAgentIndex][jobIndex];

						this.array[jobIndex] = newAgentIndex;
						assigned = true;
					}
					i++;
				}
			}
		}

		/*
		 * Attempts to improve the cost of the child by reassigning jobs to
		 * agents with higher cost.
		 */

		for (int j = 0; j < this.instance.getJobsNum(); j++) {
			for (int a = 0; a < this.instance.getAgentsNum(); a++) {
				if (array[j] == a) {
					/*
					 * The job is already assigned to this agent then skip it!
					 */
					continue;
				}
				if (c[a][j] > c[array[j]][j] && rSum[a] + r[a][j] <= b[a]) {
					/*
					 * Bingo! The job could be assigned to the agent a!
					 */
					int oldAgentIndex = this.array[j];
					/*
					 * Update rSum, it is used later to update the unfitness.
					 */
					rSum[oldAgentIndex] -= r[oldAgentIndex][j];
					rSum[a] += r[a][j];
					/*
					 * (Incrementally) Update the fitness.
					 */
					this.fitness -= c[oldAgentIndex][j];
					this.fitness += c[a][j];

					this.array[j] = a;

				}
			}
		}

		/*
		 * Update the fitness and the unfitness without calling the method.
		 */

		/*
		 * Fitness update.
		 */

		/*
		 * Unfitness update.
		 */
		this.unfitness = 0;
		for (int a = 0; a < this.instance.getAgentsNum(); a++) {
			this.unfitness += Math.max(0, rSum[a] - b[a]);
		}

		return true;
	}

	public int fitnessCombination() {
		return this.fitness() - PENALTY * this.unfitness();

	}

	public Instance getInstance() {
		return instance;
	}

	@Override
	public String toString() {
		return "Solution [array=" + Arrays.toString(array) + ", fitness=" + fitness() + ", unfitness=" + unfitness()
				+ ", fitness combination=" + this.fitnessCombination() + "]";
	}

	public void mutation() {
		/*
		 * Exchange two randomly chosen agents.
		 */

		int idxA = new Random().nextInt(instance.getJobsNum());
		int idxB = new Random().nextInt(instance.getJobsNum());

		int tmp = this.array[idxA];
		this.array[idxA] = this.array[idxB];
		this.array[idxB] = tmp;

		this.evaluate();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(array);
		result = prime * result + fitness;
		result = prime * result + unfitness;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chromosome other = (Chromosome) obj;
		if (!Arrays.equals(array, other.array))
			return false;
		if (fitness != other.fitness)
			return false;
		if (unfitness != other.unfitness)
			return false;
		return true;
	}

}
