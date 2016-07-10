package controller;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Model;
import model.Result;
import model.State;
import view.FormView;
import view.View;

/**
 * 
 * MVC Controller implementation.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 8:35:34 PM
 *
 */
public class Controller {

	private View view;
	private Model model;
	private SharedAppData sd;

	public Controller() {

		sd = new SharedAppData();

		model = new Model(this, sd);
		view = new FormView(this);
		view.setState(State.IDLE);
	}

	public void start() {
		List<File> filePaths = this.view.getFilePaths();
		this.start(filePaths);
	}

	public void start(List<File> files) {
		this.view.disableInput();
		model.setFilePaths(files);
		model.compute();
	}

	public void record() {
		List<File> filePaths = this.view.getFilePaths();
		this.record(filePaths);
	}

	public void record(List<File> files) {
		this.view.disableInput();
		model.setFilePaths(files);
		model.record();

	}

	public void stop() {
		sd.setStopped();
	}

	public void reset() {
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
