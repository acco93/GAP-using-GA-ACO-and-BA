package solver;

import java.util.Optional;

import model.Result.PartialResult;

/**
 * 
 * Solver interface.
 * 
 * @author acco
 * 
 *         Jul 8, 2016 3:33:16 PM
 *
 */
public interface Solver {

	/**
	 * Process an instance once and returns the result.
	 * 
	 * @return run result
	 */
	Optional<PartialResult> solve();

}
