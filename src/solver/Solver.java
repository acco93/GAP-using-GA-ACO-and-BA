package solver;

import java.util.Optional;

import model.Result.PartialResult;

public interface Solver {

	Optional<PartialResult> solve();

}
