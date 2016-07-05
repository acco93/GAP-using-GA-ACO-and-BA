package model;

public enum State {

	IDLE, PROCESSING, ERROR, COMPLETED, COMPLETED_WITH_ERRORS, STOPPED, STOPPING;

	@Override
	public String toString() {
		switch (this) {
		case IDLE:
			return "idle (zZzZzzZzZ)";
		case PROCESSING:
			return "processing ...";
		case ERROR:
			return "oops an error occurred :( Please check the log in the Execution tab";
		case COMPLETED:
			return "successfully finished!";
		case COMPLETED_WITH_ERRORS:
			return "completed with errors! Please check the log in the Execution tab";
		case STOPPING:
			return "stopping the computation, please wait!";
		case STOPPED:
			return "stopped";
		default:
			throw new IllegalArgumentException();
		}
	}

}
