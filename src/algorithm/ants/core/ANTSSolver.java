package algorithm.ants.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import controller.SharedAppData;
import logger.Logger;
import model.AppSettings;
import model.Instance;
import model.Result.PartialResult;

/**
 * 
 * The ANTS solver sets up some parameters according to the preferred user configuration then
 * splits the problem into logical tasks processed by a fixed pool of threads. 
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:33:17 PM
 *
 */
public class ANTSSolver {

	private int maxIterations;
	private int population;
	private ArrayList<Ant> colony;
	private double[][] tau;
	private double rho;
	private Instance instance;
	private ExecutorService executor;
	private Ant fittest;
	private SharedAppData sd;

	public ANTSSolver(Instance instance, SharedAppData sd) {
		this.instance = instance;
		this.sd = sd;

		AppSettings s = AppSettings.get();

		this.executor = Executors.newFixedThreadPool(s.threads);

		/*
		 * Read & set parameters.
		 */

		maxIterations = s.antsIterations;
		population = s.antsPopulation;
		tau = new double[instance.getJobsNum()][instance.getAgentsNum()];
		double alpha = s.antsAlpha;
		double[][] eta = new double[instance.getJobsNum()][instance.getAgentsNum()];
		double beta = s.antsBeta;
		rho = s.antsRho;

		int maxCost = Arrays.stream(instance.getCosts()).flatMapToInt(Arrays::stream).max().getAsInt();

		int tau0 = 2 * maxCost;

		for (int i = 0; i < instance.getJobsNum(); i++) {
			for (int j = 0; j < instance.getAgentsNum(); j++) {
				tau[i][j] = tau0;
				eta[i][j] = instance.getCosts()[j][i];
			}
		}

		colony = new ArrayList<>();
		for (int i = 0; i < population; i++) {
			colony.add(new Ant(instance, tau, alpha, eta, beta));
		}

		fittest = colony.get(0);

	}

	public PartialResult solve() {

		Logger.get().antsInfo("ANTS ALGORITHM .............");

		double startTime = System.currentTimeMillis();

		for (int iteration = 0; iteration < maxIterations && !this.sd.isStopped(); iteration++) {

			/*
			 * for(int x=0;x<this.instance.getJobsNum();x++){ for(int
			 * y=0;y<this.instance.getAgentsNum();y++){
			 * System.out.print(this.tau[x][y]+" "); } System.out.println(""); }
			 * 
			 * System.out.println("");
			 */

			LinkedList<Future<Boolean>> tasks = new LinkedList<>();
			/*
			 * Each ant constructs a solution.
			 */
			for (Ant ant : colony) {

				Future<Boolean> future = executor.submit(new ANTSTask(ant));
				tasks.add(future);
			}

			for (Future<Boolean> task : tasks) {
				try {
					task.get();
				} catch (InterruptedException | ExecutionException e) {

				}
			}

			/*
			 * Traces update.
			 */
			/*
			 * Trace evaporation.
			 */
			for (int i = 0; i < instance.getJobsNum(); i++) {
				for (int j = 0; j < instance.getAgentsNum(); j++) {
					tau[i][j] = rho * tau[i][j];
				}
			}

			for (Ant ant : colony) {
				ant.updateTrace();
				if (ant.fitnessCombination() > fittest.fitnessCombination()) {
					fittest = new Ant(ant);
				}
			}
		}

		double endTime = System.currentTimeMillis();

		Logger.get().antsInfo("Fittest: " + fittest);

		PartialResult result = new PartialResult(fittest.fitnessCombination(), endTime - startTime);

		return result;
	}

}
