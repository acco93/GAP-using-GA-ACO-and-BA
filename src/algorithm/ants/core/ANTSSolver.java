package algorithm.ants.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import controller.SharedAppData;
import logger.Logger;
import model.AppSettings;
import model.Instance;
import model.Result.PartialResult;
import solver.AbstractSolver;

/**
 * 
 * The ANTS solver sets up some parameters according to the preferred user
 * configuration then splits the problem into logical tasks processed by a fixed
 * pool of threads.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 9:33:17 PM
 *
 */
public class ANTSSolver extends AbstractSolver {

	private int maxIterations;
	private int population;
	private ArrayList<Ant> colony;
	private double[][] tau;
	private double rho;
	private Instance instance;
	private Ant fittest;
	private SharedAppData sd;
	private int tasksNum;
	private double minTau;
	private double maxTau;

	public ANTSSolver(Instance instance, SharedAppData sd) {
		this.instance = instance;
		this.sd = sd;
		AppSettings s = AppSettings.get();

		this.tasksNum = s.threads;

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

		int tau0 = 5 * maxCost;

		this.minTau = 1;
		this.maxTau = tau0;

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

	@Override
	protected Optional<PartialResult> process() {

		Logger.get().antsInfo("ANTS ALGORITHM .............");

		debugWriteLine("Computing ants algorithm");

		double startTime = System.currentTimeMillis();

		int iteration;

		int increment = colony.size() / tasksNum;

		for (iteration = 0; iteration < maxIterations && !this.sd.isStopped(); iteration++) {

			debugWriteLine("Iteration: " + iteration);

			LinkedList<Future<Boolean>> tasks = new LinkedList<>();

			/*
			 * Each ant constructs a solution.
			 */
			int current = 0;
			for (int i = 0; i < tasksNum; i++) {
				Future<Boolean> future = executor.submit(new ANTSTask(colony, current, current + increment));
				current += increment;
				tasks.add(future);
			}

			tasks.add(executor.submit(new ANTSTask(colony, current, colony.size())));

			for (Future<Boolean> task : tasks) {
				try {
					task.get();
				} catch (InterruptedException | ExecutionException e) {

				}
			}

			/*
			 * Trace evaporation.
			 */
			for (int i = 0; i < instance.getJobsNum(); i++) {
				for (int j = 0; j < instance.getAgentsNum(); j++) {
					tau[i][j] = rho * tau[i][j];
				}
			}
			/*
			 * Traces update.
			 */
			for (Ant ant : colony) {
				ant.updateTrace();
				if (ant.fitnessCombination() > fittest.fitnessCombination()) {
					fittest = new Ant(ant);
				}
			}

			if (AppSettings.get().antsMinMax) {
				for (int i = 0; i < instance.getJobsNum(); i++) {
					for (int j = 0; j < instance.getAgentsNum(); j++) {
						if (tau[i][j] < this.minTau) {
							tau[i][j] = this.minTau;
						} else if (tau[i][j] > maxTau) {
							tau[i][j] = this.maxTau;
						}
					}
				}
			}

			if (super.areThereDebuggers()) {
				debugWriteLine("Tau:");
				for (int i = 0; i < instance.getJobsNum(); i++) {
					for (int j = 0; j < instance.getAgentsNum(); j++) {
						debugWrite(String.format("%.3f ", tau[i][j]));
					}
					debugWriteLine("");
				}
			}

			debugWriteLine("");

		}

		double endTime = System.currentTimeMillis();

		Optional<PartialResult> result = Optional.empty();

		if (iteration == this.maxIterations) {
			result = Optional.of(new PartialResult(fittest.fitnessCombination(), endTime - startTime));
			Logger.get().antsInfo("Fittest: " + fittest);
		} else {
			Logger.get().antsInfo("K.O.");
		}

		return result;

	}

}
