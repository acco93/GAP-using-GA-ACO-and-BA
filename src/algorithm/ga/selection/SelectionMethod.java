package algorithm.ga.selection;

import algorithm.ga.core.Genome;

public interface SelectionMethod {
	int apply(Genome genome);
}
