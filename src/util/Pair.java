package util;

/**
 * 
 * Simple immutable pair.
 * 
 * @author acco
 * 
 * Jul 5, 2016 9:10:14 PM
 *
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> {

	private final T first;
	private final U second;

	public Pair(T first, U second){
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public U getSecond() {
		return second;
	}
	
	
}
