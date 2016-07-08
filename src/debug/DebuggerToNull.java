package debug;

import java.io.File;

public class DebuggerToNull implements Debugger {

	public DebuggerToNull() {

	}

	@Override
	public void start() {

	}

	@Override
	public void writeLine(String info) {

	}

	@Override
	public void stop() {

	}

	@Override
	public boolean isNull() {
		return true;
	}

	@Override
	public void write(String info) {
		// TODO Auto-generated method stub

	}

	@Override
	public File getOutput() {
		// TODO Auto-generated method stub
		return null;
	}

}
