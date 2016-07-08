package algorithm.ants.core;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 
 * ANTS step (iteratively build a solution) implemented as a logical task (async
 * computation)
 * 
 * @author acco
 * 
 *         Jul 5, 2016 9:32:27 PM
 *
 */

public class ANTSTask implements Callable<Boolean> {

	private List<Ant> colony;
	private int start;
	private int stop;

	public ANTSTask(List<Ant> colony, int start, int stop) {
		this.colony = colony;
		this.start = start;
		this.stop = stop;
	}

	@Override
	public Boolean call() throws Exception {
		for (int i = start; i < stop; i++) {
			colony.get(i).construct();
		}
		return true;
	}

}
