### ..:: GENERAL DESCRIPTION ::.. ###
This application tries to solve simple generalized assignment problem (GAP) instances using:

* a genetic algorithm GA

* a bionomic algorithm BA (GA variant)

* an ACO-based algorithm ANTS

	
GA, BA and ANTS algorithm parameters are configurable up to a certain level.


Public repository available at:
https://bitbucket.org/acco93/gap-using-heuristics


### ..:: HOW IT WORKS ::.. ###
1. Tune the parameters as you wish in the Configuration tab
2. Switch to the Workspace tab
3. Add one or more files (see the file structure section below) in the file list
4. Run the algorithms
5. Get (& export to CSV) the results


### ..:: FILE STRUCTURE ::.. ###
The application works with files in the format described in:
[http://people.brunel.ac.uk/~mastjjb/jeb/orlib/gapinfo.html](http://people.brunel.ac.uk/~mastjjb/jeb/orlib/gapinfo.html)

Some instances are downloadable from:
[http://people.brunel.ac.uk/~mastjjb/jeb/orlib/Link URLb/files/)
under the name: gap*.txt


### ..:: PROBLEM NOTES ::.. ###
GAP is solved as a maximization problem

### ..:: GA NOTES ::.. ###
A lot of ideas have been taken from:
*P.C.Chu and J.E.Beasley (1996) A Genetic Algorithm for the Generalised Assignment Problem*
		
### ..:: BA NOTES ::.. ###
For more information:
*V. Maniezzo, A. Mingozzi and R.Baldacci (1998) A Bionomic Approach to the Capacitated p-Median Problem*
		
### ..:: ANTS NOTES ::.. ###
...