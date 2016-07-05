package model;

/**
 * 
 * A result object contains aggregate info describing the results of each algorithm.
 * NB: stopping the processing will cause an inconsistent result. The info about the missing algorithm will be zeroed.
 * 
 * @author acco
 * 
 * Jul 5, 2016 8:21:17 PM
 *
 */
public class Result {

	enum Algorithm {
		GA, ANTS
	}
		
	private Instance instance;
	private int gaBestValue;
	private int antsBestValue;

	private int gaValuesSum;
	private int antsValuesSum;
	private int runs;
	private double gaTimeSum;
	private double antsTimeSum;

	public Result(Instance instance){
		this.instance = instance;
		this.runs = 0;
		
		this.gaBestValue = 0;
		this.antsBestValue = 0;
		
		this.gaValuesSum = 0;
		this.antsValuesSum = 0;
		
		this.gaTimeSum = 0;
		this.antsTimeSum = 0;
	}
	
	/**
	 * Merge a partial result.
	 * @param result
	 * @param algorithm
	 */
	public void merge(PartialResult result, Algorithm algorithm){
		switch(algorithm){
		case ANTS:
			this.antsValuesSum+=result.getValue();
			this.antsTimeSum+=result.getTimeElapsed();
			if(result.getValue()>this.antsBestValue){
				this.antsBestValue = result.getValue();
			}
			break;
		case GA:
			this.runs +=1;
			this.gaValuesSum+=result.getValue();
			this.gaTimeSum+=result.getTimeElapsed();
			if(result.getValue()>this.gaBestValue){
				this.gaBestValue = result.getValue();
			}
			break;
		default:
			throw new IllegalStateException();
			
		}
	}
	
	
	
	
	public Instance getInstance() {
		return instance;
	}

	public int getGaBestValue() {
		return gaBestValue;
	}

	public int getAntsBestValue() {
		return antsBestValue;
	}

	
	public double getGaAvgValue() {
		if(runs == 0){
			return 0;
		}
		return (double)gaValuesSum/runs;
	}

	public double getAntsAvgValue() {
		if(runs == 0){
			return 0;
		}
		return (double)antsValuesSum/runs;
	}

	public double getGaAvgTime() {
		if(runs == 0){
			return 0;
		}
		return gaTimeSum/runs;
	}
	
	public double getAntsAvgTime() {
		if(runs == 0){
			return 0;
		}
		return antsTimeSum/runs;
	}


	public int getRuns() {
		return this.runs;
	}
	
	/**
	 * Info actually produced by a single run.
	 * 
	 * @author acco
	 * 
	 * Jul 5, 2016 8:25:16 PM
	 *
	 */
	public static class PartialResult{
		
		private int value;
		private double timeElapsed;
		
		public PartialResult(int value, double timeElapsed){
			this.value = value;
			this.timeElapsed = timeElapsed;
		}
		
		public int getValue() {
			return value;
		}
		
		public double getTimeElapsed() {
			return timeElapsed;
		}
		
	}







	
	
	
}
