package solver;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import debug.Inspectable;
import debug.Inspector;
import model.AppSettings;
import model.Result.PartialResult;

/**
 * 
 * Solver interface implementation. It defines a generic solver implementation
 * providing some useful methods & components (executor).
 * 
 * @author acco
 * 
 *         Jul 8, 2016 3:35:17 PM
 *
 */
public abstract class AbstractSolver implements Solver, Inspectable {

	private Set<Inspector> debuggers;
	protected ExecutorService executor;

	public AbstractSolver() {
		this.debuggers = new HashSet<>();
		this.executor = Executors.newFixedThreadPool(AppSettings.get().threads);
	}

	@Override
	public Optional<PartialResult> solve() {
		preProcess();
		Optional<PartialResult> result = process();
		postProcess();
		this.executor.shutdown();

		return result;
	}

	protected void postProcess() {

	}

	protected void preProcess() {

	}

	protected abstract Optional<PartialResult> process();

	@Override
	public void attach(Inspector debugger) {
		this.debuggers.add(debugger);
	}

	public void debugWrite(String text) {
		for (Inspector d : debuggers) {
			d.write(text);
		}
	}

	public void debugWriteLine(String text) {
		for (Inspector d : debuggers) {
			d.writeLine(text);
		}
	}

	public boolean areThereDebuggers() {
		return this.debuggers.size() > 0;
	}

}
