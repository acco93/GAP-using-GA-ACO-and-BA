package model;

import java.io.File;
import java.util.List;
import java.util.Map;

import algorithm.ants.core.ANTSSolver;
import algorithm.ga.core.GASolver;
import controller.Controller;
import controller.SharedAppData;
import logger.Logger;
import model.Result.Algorithm;
import model.Result.PartialResult;

public class ComputeTask implements Runnable{

	private List<File> filePaths;
	private Controller controller;
	private Map<String, Result> results;
	private SharedAppData sd;

	public ComputeTask(List<File> filePaths, Controller controller, Map<String,Result> results, SharedAppData sd){
		this.filePaths = filePaths;
		this.controller = controller;
		this.results = results;
		this.sd = sd;
	}
	
	@Override
	public void run() {
		boolean errors = false;
		
		if (filePaths.isEmpty()) {
			Logger.get().err("Please provide at leat one file!");
			this.controller.setStatus(State.ERROR);
			this.controller.reset();
		} else {
			Logger.get().info("Processing ... ");
			this.controller.setStatus(State.PROCESSING);

			for (File file : filePaths) {
				if(sd.isStopped()){
					break;
				}
				Logger.get().info("Parsing "+file.getName()+" ... ");
				Parser reader = new Parser(file.getAbsolutePath(), controller);
				if (!reader.correclyRead()) {
					Logger.get().err("File not correcly read... Skipping...");
					errors = true;
				} else {
					List<Instance> instances = reader.getInstances();
					Logger.get().info("It contains " + instances.size() + " instances.");
					for (Instance instance : instances) {
						
						if(sd.isStopped()){
							break;
						}
						
						Logger.get().info("Problem: " + instance.getName());
						
						Result result;
						
						if(this.results.containsKey(instance.getName())){
							result = this.results.get(instance.getName());
						} else {
							result = new Result(instance);
							results.put(instance.getName(), result);
						}
						
						for(int r=0;r<AppSettings.get().runs && !this.sd.isStopped();r++){
							
							Logger.get().info("Run " + (r+1)+"/"+AppSettings.get().runs);
							
							GASolver ga = new GASolver(instance, sd);
							PartialResult gaResult = ga.solve();
							
							ANTSSolver ants = new ANTSSolver(instance, sd);
							PartialResult antResult = ants.solve();	
							
							result.merge(gaResult, Algorithm.GA);
							result.merge(antResult, Algorithm.ANTS);
							
							controller.refreshResults();
						}
						
					}

				}
			}

			Logger.get().info("Done :)");
			
			if(sd.isStopped()){
				this.controller.setStatus(State.STOPPED);
			} else {
				if(errors){
					this.controller.setStatus(State.COMPLETED_WITH_ERRORS);
				} else {
					this.controller.setStatus(State.COMPLETED);				
				}	
			}

			this.controller.reset();
		}

	}

}
