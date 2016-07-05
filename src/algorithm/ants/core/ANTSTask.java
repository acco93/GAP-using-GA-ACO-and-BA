package algorithm.ants.core;

import java.util.concurrent.Callable;

public class ANTSTask implements Callable<Boolean> {

	private Ant ant;

	public ANTSTask(Ant ant) {
		this.ant = ant;
	}

	@Override
	public Boolean call() throws Exception {
		ant.construct();
		return true;
	}



}
