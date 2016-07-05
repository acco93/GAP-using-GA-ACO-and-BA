package algorithm.ants.core;

import java.util.concurrent.Callable;

/**
 * 
 * ANTS step (iteratively build a solution) implemented as a logical task (async computation)
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:32:27 PM
 *
 */

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
