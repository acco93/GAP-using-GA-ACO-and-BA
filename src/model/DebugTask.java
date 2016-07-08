package model;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;

import algorithm.ants.core.ANTSSolver;
import algorithm.ba.core.BASolverConcurrent;
import algorithm.ga.core.GASolver;
import controller.Controller;
import controller.SharedAppData;
import debug.Inspector;
import debug.DebuggerToFile;
import logger.Logger;

public class DebugTask implements Runnable {

	private List<File> filePaths;
	private Controller controller;
	private SharedAppData sd;
	private Inspector debugger;

	public DebugTask(List<File> filePaths, Controller controller, SharedAppData sd) {
		this.filePaths = filePaths;
		this.controller = controller;
		this.sd = sd;

		this.debugger = new DebuggerToFile();

		this.debugger.start();
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
			Logger.get().info("Logging to file ... ");
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
						debugger.writeLine("Problem: " + instance.getName());

						/*
						 * Run the algorithms r times
						 */
						for (int r = 0; r < AppSettings.get().runs && !this.sd.isStopped(); r++) {

							Logger.get().info("Run " + (r + 1) + "/" + AppSettings.get().runs);

							GASolver ga = new GASolver(instance, sd);
							ga.attach(debugger);
							ga.solve();

							ANTSSolver ants = new ANTSSolver(instance, sd);
							ants.attach(debugger);
							ants.solve();

							BASolverConcurrent bio = new BASolverConcurrent(instance, sd);
							bio.attach(debugger);
							bio.solve();

						}

					}

				}
			}

			/*
			 * Print some status info...
			 */
			if (sd.isStopped()) {
				this.controller.setStatus(State.STOPPED);
				Logger.get().info("Done (stopped)");
			} else {
				if (errors) {
					this.controller.setStatus(State.COMPLETED_WITH_ERRORS);
					Logger.get().info("Done (with errors)");
				} else {
					this.controller.setStatus(State.COMPLETED);
					Logger.get().info("Done :]");
				}
			}

			Logger.get().info("Check the file " + this.debugger.getOutput() + "for detailed info.");

			this.debugger.stop();
			this.controller.reset();
			try {
				Desktop.getDesktop().open(this.debugger.getOutput());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
