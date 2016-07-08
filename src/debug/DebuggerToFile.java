package debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class DebuggerToFile implements Debugger {

	private File file;
	private BufferedWriter output;

	public DebuggerToFile() {
		file = new File("debug_log_" + new Date() + ".txt");
	}

	@Override
	public void write(String info) {
		try {
			output.write(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void writeLine(String info) {
		try {
			output.write(info + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		try {
			output = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean isNull() {
		return false;
	}

	@Override
	public File getOutput() {
		return this.file.getAbsoluteFile();
	}

}
