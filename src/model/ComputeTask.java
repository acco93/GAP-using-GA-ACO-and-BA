package model;

import java.io.File;
import java.util.List;
import java.util.Map;

import algorithm.ants.core.ANTSSolver;
import algorithm.bio.core.BioSolverConcurrent;
import algorithm.bio.core.BioSolverSequential;
import algorithm.ga.core.GASolver;
import controller.Controller;
import controller.SharedAppData;
import logger.Logger;
import model.Result.Algorithm;
import model.Result.PartialResult;

/**
 * 
 * Processing request implemented as runnable.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 8:05:07 PM
 *
 */
public class ComputeTask implements Runnable {

	private List<File> filePaths;
	private Controller controller;
	private Map<String, Result> results;
	private SharedAppData sd;

	public ComputeTask(List<File> filePaths, Controller controller, Map<String, Result> results, SharedAppData sd) {
		this.filePaths = filePaths;
		this.controller = controller;
		this.results = results;
		this.sd = sd;
	}

	@Override
	public void run() {
		boolean errors = false;

		/*
		 * Check the file list.
		 */
		if (filePaths.isEmpty()) {
			Logger.get().err("Please provide at leat one file!");
			this.controller.setStatus(State.ERROR);
			this.controller.reset();
		} else {
			/*
			 * If there is at least one file try to process it.
			 */
			Logger.get().info("Processing ... ");
			this.controller.setStatus(State.PROCESSING);

			for (File file : filePaths) {
				/*
				 * Check if the user has asked for the termination.
				 */
				if (sd.isStopped()) {
					break;
				}
				/*
				 * If not, try to parse the file
				 */
				Logger.get().info("Parsing " + file.getName() + " ... ");
				Parser reader = new Parser(file.getAbsolutePath(), controller);

				if (!reader.correclyRead()) {
					Logger.get().err("File [" + file.getName() + "] not correcly read... Skipping...");
					errors = true;
				} else {
					/*
					 * File successfully parsed! Retrieve the problem instances
					 * NB: a file may contain more than one instance
					 */
					List<Instance> instances = reader.getInstances();
					Logger.get().info("It contains " + instances.size() + " instances.");

					for (Instance instance : instances) {
						/*
						 * Process each instance checking if the user has ask
						 * for the termination.
						 */
						if (sd.isStopped()) {
							break;
						}

						Logger.get().info("Problem: " + instance.getName());

						Result result;
						/*
						 * Results are stored into a map. The key is the
						 * instance name and the value the result. If the user
						 * asks to process more than once the same problem the
						 * results will be merged. See the Result class for more
						 * details.
						 */
						if (this.results.containsKey(instance.getName())) {
							/*
							 * The map contains the problem. It has been solved
							 * previously.
							 */
							result = this.results.get(instance.getName());
						} else {
							/*
							 * New problem -> new Result
							 */
							result = new Result(instance);
							results.put(instance.getName(), result);
						}

						/*
						 * Run the algorithms r times
						 */
						for (int r = 0; r < AppSettings.get().runs && !this.sd.isStopped(); r++) {

							Logger.get().info("Run " + (r + 1) + "/" + AppSettings.get().runs);

							GASolver ga = new GASolver(instance, sd);
							PartialResult gaResult = ga.solve();

							ANTSSolver ants = new ANTSSolver(instance, sd);
							PartialResult antResult = ants.solve();

							BioSolverSequential bio = new BioSolverSequential(instance, sd);
							bio.solve();

							result.merge(gaResult, Algorithm.GA);
							result.merge(antResult, Algorithm.ANTS);

							controller.refreshResults();
						}

					}

				}
			}

			Logger.get().info("Done :)");

			/*
			 * Print some status info...
			 */
			if (sd.isStopped()) {
				this.controller.setStatus(State.STOPPED);
			} else {
				if (errors) {
					this.controller.setStatus(State.COMPLETED_WITH_ERRORS);
				} else {
					this.controller.setStatus(State.COMPLETED);
				}
			}

			this.controller.reset();
		}

	}

}
