package debug;

/**
 * 
 * Interface implemented by those that could be inspected.
 * 
 * @author acco
 * 
 *         Jul 8, 2016 3:37:14 PM
 *
 */
public interface Inspectable {
	void attach(Inspector inspector);
}
