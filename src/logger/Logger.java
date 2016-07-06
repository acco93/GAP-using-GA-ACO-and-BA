package logger;

import java.util.HashSet;

/**
 * 
 * Simple logger implemented as singleton.
 * 
 * @author acco
 * 
 * Jul 5, 2016 8:27:05 PM
 *
 */
public class Logger {

	private static Logger logger;
	private static HashSet<LogViewer> viewers;
	
	static{
		logger = new Logger();
		viewers = new HashSet<LogViewer>();
	}
	
	private Logger(){ }
	
	public static Logger get(){
		return logger;
	}
	
	public synchronized void info(String text){
		for(LogViewer viewer:viewers){
			viewer.info(".. [INFO] "+text);
		}
	}
	
	public synchronized void err(String text){
		
		for(LogViewer viewer:viewers){
			viewer.err("!! [ERROR] "+text);
		}
	}

	public synchronized void gaInfo(String text){
		for(LogViewer viewer:viewers){
			viewer.gaInfo(".. [GA] "+text);
		}
	}
	
	public synchronized void antsInfo(String text){
		for(LogViewer viewer:viewers){
			viewer.antsInfo(".. [ANTS] "+text);
		}
	}
	
	public synchronized void baInfo(String text){
		for(LogViewer viewer:viewers){
			viewer.baInfo(".. [BA] "+text);
		}
	}
	
	
	public synchronized void addLogViewer(LogViewer viewer) {
		viewers.add(viewer);
	}

}
