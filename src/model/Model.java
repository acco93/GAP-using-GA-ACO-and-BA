package model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.Controller;
import controller.SharedAppData;

/**
 * MVC model implementation.
 * 
 * @author acco
 * 
 * Jul 5, 2016 8:17:50 PM
 *
 */
public class Model {

	private List<File> filePaths;
	private Controller controller;
	private Map<String, Result> results;
	private SharedAppData sd;

	public Model(Controller controller, SharedAppData sd) {
		this.controller = controller;
		this.filePaths = new ArrayList<>();
		this.results = new HashMap<>();
		this.sd = sd;
	}

	public void setFilePaths(List<File> filePaths) {
		this.filePaths = filePaths;

	}

	public void compute() {
		/*
		 * Run the compute task in a separate thread otherwise the ui will freeze
		 */
		ComputeTask cp = new ComputeTask(filePaths,controller, results, sd);
		new Thread(cp).start();
		
	}

	public Map<String, Result> getResults() {
		return this.results;
	}

	public void clearResults() {
		this.results = new HashMap<>();
		
	}


}
