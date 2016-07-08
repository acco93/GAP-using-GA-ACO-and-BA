package solver;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import debug.Debuggable;
import debug.Debugger;
import model.AppSettings;
import model.Result.PartialResult;

public abstract class AbstractSolver implements Solver, Debuggable {

	private Set<Debugger> debuggers;
	protected ExecutorService executor;

	public AbstractSolver() {
		this.debuggers = new HashSet<>();
		this.executor = Executors.newFixedThreadPool(AppSettings.get().threads);
	}

	@Override
	public abstract Optional<PartialResult> solve();

	@Override
	public void attach(Debugger debugger) {
		this.debuggers.add(debugger);
	}

	public void debugWrite(String text) {
		for (Debugger d : debuggers) {
			d.write(text);
		}
	}

	public void debugWriteLine(String text) {
		for (Debugger d : debuggers) {
			d.writeLine(text);
		}
	}

	public boolean areThereDebuggers() {
		return this.debuggers.size() > 0;
	}

}
