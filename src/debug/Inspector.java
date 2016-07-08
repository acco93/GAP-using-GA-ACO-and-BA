package debug;

import java.io.File;

/**
 * 
 * Component able to get information and write them somewhere.
 * 
 * @author acco
 * 
 *         Jul 8, 2016 3:40:26 PM
 *
 */
public interface Inspector {
	/**
	 * Begin inspecting.
	 */
	void start();

	/**
	 * Write some info.
	 * 
	 * @param info
	 */
	void write(String info);

	/**
	 * Write some info then go to a new line.
	 * 
	 * @param info
	 */
	void writeLine(String info);

	/**
	 * Stop inspecting and save results.
	 */
	void stop();

	/**
	 * Return the results.
	 * @return results
	 */
	File getOutput();
}
