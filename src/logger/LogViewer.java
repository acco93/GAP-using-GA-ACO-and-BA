package logger;

/**
 * 
 * Interface implemented by whom want to receive log info.
 * 
 * @author acco
 * 
 * Jul 5, 2016 8:27:28 PM
 *
 */
public interface LogViewer {

	/**
	 * General application info.
	 * @param text
	 */
	void info(String text);
	
	/**
	 * General error info.
	 * @param text
	 */
	void err(String text);
	
	/**
	 * Info related to GA.
	 * @param text
	 */
	void gaInfo(String text);
	
	/**
	 * Info related to ANTS.
	 * @param text
	 */
	void antsInfo(String text);
	
	/**
	 * Info related to BA.
	 * @param text
	 */
	void baInfo(String text);
}
