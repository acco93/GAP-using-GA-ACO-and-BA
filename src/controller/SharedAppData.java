package controller;

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
