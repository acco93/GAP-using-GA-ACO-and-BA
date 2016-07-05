package controller;

/**
 * 
 * Shared data used to abort the computation.
 * 
 * @author acco
 * 
 * Jul 5, 2016 8:35:55 PM
 *
 */
public class SharedAppData {

	private boolean isStopped;

	public SharedAppData(){
		this.isStopped=false;
	}

	public synchronized boolean isStopped() {
		return isStopped;
	}

	public synchronized void setStopped() {
		this.isStopped = true;
	}

	public void reset() {
		this.isStopped = false;
	}
	
}
