package logger;

import java.util.HashSet;

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
			viewer.gaInfo(".. [INFO] "+text);
		}
	}
	
	public synchronized void antsInfo(String text){
		for(LogViewer viewer:viewers){
			viewer.antsInfo(".. [INFO] "+text);
		}
	}
	
	public synchronized void addLogViewer(LogViewer viewer) {
		viewers.add(viewer);
	}

}
