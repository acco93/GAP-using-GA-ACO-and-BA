package debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Inspector implementation as logger to file.
 * 
 * @author acco
 * 
 *         Jul 8, 2016 3:43:02 PM
 *
 */
public class DebuggerToFile implements Inspector {

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
	public File getOutput() {
		return this.file.getAbsoluteFile();
	}

}
