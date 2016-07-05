package algorithm.ants.core;

import java.util.Arrays;
import java.util.Random;

import model.AppSettings;
import model.Instance;

public class Ant {

	private static final int PENALTY = 10;
	
	private Instance instance;
	private double[][] tau;
	private double alpha;
	private double[][] eta;
	private double beta;
	private int[] solution;
	
	private int infeasibility;
	private int cost;

	public Ant(Instance instance, double[][] tau, double alpha, double[][] eta, double beta) {

		this.instance = instance;
		this.tau = tau;
		this.alpha = alpha;
		this.eta = eta;
		this.beta = beta;

		this.infeasibility = 0;
		this.cost = 0;
		
		this.solution = new int[instance.getJobsNum()];

	}

	public Ant(Ant ant) {
		this.solution = Arrays.copyOf(ant.solution, ant.solution.length);
		this.cost = ant.cost;
		this.infeasibility = ant.infeasibility;
	}

	public void construct() {

		int[] agentsSum = new int[instance.getAgentsNum()];
		double[] values = new double[instance.getAgentsNum()];
		int[][] r = instance.getResoucesNeeded();

		for (int i = 0; i < instance.getJobsNum(); i++) {
			for (int j = 0; j < instance.getAgentsNum(); j++) {
				values[j] = Math.pow(tau[i][j], alpha) * Math.pow(eta[i][j], beta);
			}

			int agent = montecarlo(values);
			this.solution[i] = agent;
			agentsSum[agent] += r[agent][i];
		}

		this.evaluate();
		this.localSearch();

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
			rSum[this.solution[j]] += r[this.solution[j]][j];
		}

		for(int a=0;a<this.instance.getAgentsNum();a++){
			if(rSum[a]>b[a]){
				/*
				 * Pick a randomly selected job assigned to the agent and try to assign it to another one
				 * without violating the capacity constraint.
				 */
				/*
				 * Pick the first one! :)
				 */
				int jobIndex=0;
				while(solution[jobIndex]!=a){
					jobIndex++;
				}
				/*
				 * Try to assign the job to another agent:
				 * from a+1, ..., a-1
				 */
				int i = 1;
				boolean assigned = false;
				while(i<this.instance.getAgentsNum() && !assigned){
					int newAgentIndex = (a+i)%this.instance.getAgentsNum();
					if(rSum[newAgentIndex]+r[newAgentIndex][jobIndex]<=b[newAgentIndex]){
						/*
						 * The agent could take the task!
						 * Before messing up indexes update rSum because it is used later!
						 * 
						 */
						int oldAgentIndex = this.solution[jobIndex];
						rSum[oldAgentIndex]-=r[oldAgentIndex][jobIndex];
						rSum[newAgentIndex]+=r[newAgentIndex][jobIndex];
						/*
						 * (Incrementally) Update the fitness.
						 */
						this.cost-=c[oldAgentIndex][jobIndex];
						this.cost+=c[newAgentIndex][jobIndex];
						
						this.solution[jobIndex] = newAgentIndex;
						assigned = true;
					}
					i++;
				}
			}
		}
		
		/*
		 * Attempts to improve the cost of the child by reassigning jobs to agents with higher cost.
		 */
		
		for(int j=0;j<this.instance.getJobsNum();j++){
			for(int a=0;a<this.instance.getAgentsNum(); a++){
				if(solution[j]==a){
					/*
					 * The job is already assigned to this agent then skip it!
					 */
					continue;
				}
				if(c[a][j]>c[solution[j]][j] && rSum[a]+r[a][j]<=b[a]){
					/*
					 * Bingo! The job could be assigned to the agent a!
					 */
					int oldAgentIndex = this.solution[j];
					/*
					 * Update rSum, it is used later to update the unfitness.
					 */
					rSum[oldAgentIndex]-=r[oldAgentIndex][j];
					rSum[a]+=r[a][j];
					/*
					 * (Incrementally) Update the fitness.
					 */
					this.cost-=c[oldAgentIndex][j];
					this.cost+=c[a][j];
					
					this.solution[j] = a;
					
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
		this.infeasibility = 0;
		for (int a = 0; a < this.instance.getAgentsNum(); a++) {
			this.infeasibility += Math.max(0, rSum[a] - b[a]);
		}
		
		return true;
	}


	private int montecarlo(double[] values) {

		double sum = 0;
		for (int j = 0; j < values.length; j++) {
			sum += values[j];
		}

		Random r = new Random();
		double cutPoint = r.nextDouble() * sum;

		sum = 0;
		int i;

		for (i = 0; i < values.length; i++) {
			sum += values[i];
			if (sum >= cutPoint) {
				break;
			}
		}

		return i;

	}

	private void evaluate() {
		this.cost = 0;
		this.infeasibility = 0;

		int[][] c = this.instance.getCosts();

		for (int i = 0; i < this.instance.getJobsNum(); i++) {
			this.cost += c[solution[i]][i];
		}

		int[][] r = this.instance.getResoucesNeeded();
		int[] b = this.instance.getAgentsCapacity();
		int[] rSum = new int[this.instance.getAgentsNum()];

		for (int a = 0; a < this.instance.getAgentsNum(); a++) {
			rSum[a] = 0;
		}

		for (int j = 0; j < this.instance.getJobsNum(); j++) {
			rSum[this.solution[j]] += r[this.solution[j]][j];
		}

		for (int a = 0; a < this.instance.getAgentsNum(); a++) {
			this.infeasibility += Math.max(0, rSum[a] - b[a]);
		}

	}

	public void updateTrace() {

		for (int i = 0; i < instance.getJobsNum(); i++) {
			// invece che 1/costs si potrebbe fare qualcosa di più raffinato	
			tau[i][solution[i]] += (1.0/AppSettings.get().antsPopulation) * (double)this.cost;
		}
		

	}

	public int fitnessCombination() {
		return this.cost - PENALTY * this.infeasibility;
	}

	@Override
	public String toString() {
		return "Ant [solution=" + Arrays.toString(solution) + ", cost=" + cost
				+ ", infeasibility=" + infeasibility +"]";
	}

	

}
