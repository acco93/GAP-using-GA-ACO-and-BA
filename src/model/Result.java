package model;

/**
 * 
 * A result object contains aggregate info describing the results of each
 * algorithm. NB: stopping the processing will cause an inconsistent result. The
 * info about the missing algorithm will be zeroed.
 * 
 * @author acco
 * 
 *         Jul 5, 2016 8:21:17 PM
 *
 */
public class Result {

	private Instance instance;
	private int runs;

	private int gaBestValue;
	private int antsBestValue;
	private int bioBestValue;

	private int gaValuesSum;
	private int antsValuesSum;
	private int bioValuesSum;

	private double gaTimeSum;
	private double antsTimeSum;
	private double bioTimeSum;

	public Result(Instance instance) {
		this.instance = instance;
		this.runs = 0;

		this.gaBestValue = 0;
		this.antsBestValue = 0;
		this.bioBestValue = 0;

		this.gaValuesSum = 0;
		this.antsValuesSum = 0;
		this.bioValuesSum = 0;

		this.gaTimeSum = 0;
		this.antsTimeSum = 0;
		this.bioTimeSum = 0;
	}

	/**
	 * Merge results
	 * 
	 * @param gaResult
	 * @param antsResult
	 * @param bioResult
	 */
	public void merge(PartialResult gaResult, PartialResult antsResult, PartialResult bioResult) {

		/*
		 * ANTS
		 */
		this.antsValuesSum += antsResult.getValue();
		this.antsTimeSum += antsResult.getTimeElapsed();
		if (antsResult.getValue() > this.antsBestValue) {
			this.antsBestValue = antsResult.getValue();
		}

		/*
		 * GA
		 */

		this.gaValuesSum += gaResult.getValue();
		this.gaTimeSum += gaResult.getTimeElapsed();
		if (gaResult.getValue() > this.gaBestValue) {
			this.gaBestValue = gaResult.getValue();
		}

		/*
		 * BIO
		 */

		this.bioValuesSum += bioResult.getValue();
		this.bioTimeSum += bioResult.getTimeElapsed();
		if (bioResult.getValue() > this.bioBestValue) {
			this.bioBestValue = bioResult.getValue();
		}

		/*
		 * General
		 */
		this.runs += 1;

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

	public int getBaBestValue() {
		return bioBestValue;
	}

	public double getGaAvgValue() {
		if (runs == 0) {
			return 0;
		}
		return (double) gaValuesSum / runs;
	}

	public double getAntsAvgValue() {
		if (runs == 0) {
			return 0;
		}
		return (double) antsValuesSum / runs;
	}

	public double getBaAvgValue() {
		if (runs == 0) {
			return 0;
		}
		return (double) bioValuesSum / runs;
	}

	public double getGaAvgTime() {
		if (runs == 0) {
			return 0;
		}
		return gaTimeSum / runs;
	}

	public double getAntsAvgTime() {
		if (runs == 0) {
			return 0;
		}
		return antsTimeSum / runs;
	}

	public double getBaAvgTime() {
		if (runs == 0) {
			return 0;
		}
		return bioTimeSum / runs;
	}

	public int getRuns() {
		return this.runs;
	}

	/**
	 * Info actually produced by a single run.
	 * 
	 * @author acco
	 * 
	 *         Jul 5, 2016 8:25:16 PM
	 *
	 */
	public static class PartialResult {

		private int value;
		private double timeElapsed;

		public PartialResult(int value, double timeElapsed) {
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
