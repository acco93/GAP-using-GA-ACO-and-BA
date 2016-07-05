package controller;

import java.io.File;
import java.util.List;
import java.util.Map;

import model.Model;
import model.Result;
import model.State;
import view.FormView;
import view.View;

public class Controller {

	private View view;
	private Model model;
	private SharedAppData sd;

	public Controller(){
		
		sd = new SharedAppData();
		
		model = new Model(this, sd);
		view = new FormView(this);
		view.setState(State.IDLE);
	}

	public void start() {
		this.view.disableInput();
		List<File> filePaths = this.view.getFilePaths();
		model.setFilePaths(filePaths);
		model.compute();
	}

	public void stop() {
		sd.setStopped();
	}
	
	public void reset(){
		this.view.enableInput();
		this.sd.reset();
	}

	public void setStatus(State state) {
		this.view.setState(state);
		
	}

	public Map<String, Result> getResults() {
		return model.getResults();
	}

	public void refreshResults() {
		view.refreshResults();
		
	}

	public void clearResults() {
		this.model.clearResults();
		
	}


	
}
