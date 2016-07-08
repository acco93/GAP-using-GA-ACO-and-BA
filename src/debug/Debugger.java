package debug;

import java.io.File;

public interface Debugger {
	void start();
	void write(String info);
	void writeLine(String info);
	void stop();

	boolean isNull();
	File getOutput();
}
