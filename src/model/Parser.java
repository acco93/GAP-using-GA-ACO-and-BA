package model;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import controller.Controller;
import logger.Logger;

/**
 * Simple problem parser.
 * See http://people.brunel.ac.uk/~mastjjb/jeb/orlib/gapinfo.html for the file structure.
 * @author acco
 * 
 * Jul 5, 2016 8:19:43 PM
 *
 */
public class Parser {

	private BufferedReader br;
	private boolean errors;
	private String filePath;
	private List<Instance> instances;

	public Parser(String filePath, Controller controller) {
		try {
			this.filePath = filePath;
			this.br = new BufferedReader(new FileReader(filePath));
			this.parse();
		} catch (FileNotFoundException e) {
			Logger.get().info("File not found!");
			this.errors = true;
		}
	}

	public boolean correclyRead() {
		return !this.errors;
	}

	public List<Instance> getInstances(){
		return this.instances;
	}
	
	private void parse() {
		
		instances = new LinkedList<>();
		try {
			/*
			 * Parsing the number of different problem sets (P)
			 */
			int problems = Integer.parseInt(br.readLine().trim());
			for (int i = 0; i < problems; i++) {
				/*
				 * Parsing the number of agents (m), number of jobs (n)
				 */
				String[] tokens = br.readLine().trim().split(" ");
				int m = Integer.parseInt(tokens[0]);
				int n = Integer.parseInt(tokens[1]);
				/*
				 * Cost of allocating job j to agent i (j=1,...,n)
				 */
				int[][] costs = new int[m][n];
				/*
				 * Resource consumed in allocating job j to agent i (j=1,...,n)
				 */
				int[][] resourcesNeeded = new int[m][n];
				/*
				 * Resource capacity of agent i (i=1,...,m)
				 */
				int[] agentsCapacity = new int[m];
				/*
				 * For each agent i (i=1,...,m) in turn: cost of allocating job
				 * j to agent i (j=1,...,n)
				 */

				for (int a = 0; a < m; a++) {
					int elementsRead = 0;
					while(elementsRead < n){
						tokens = br.readLine().trim().split(" ");
						for (int t=0; t < tokens.length; elementsRead++, t++) {
							costs[a][elementsRead] = Integer.parseInt(tokens[t]);
						}	
					}
				}
				/*
				 * For each agent i (i=1,...,m) in turn: resource consumed in
				 * allocating job j to agent i (j=1,...,n)
				 */
				for (int a = 0; a < m; a++) {
					int elementsRead = 0;
					while(elementsRead < n){
						tokens = br.readLine().trim().split(" ");
						for (int t=0; t < tokens.length; elementsRead++, t++) {
							resourcesNeeded[a][elementsRead] = Integer.parseInt(tokens[t]);
						}	
					}
				}
				/*
				 * Resource capacity of agent i (i=1,...,m)
				 */
				tokens = br.readLine().trim().split(" ");
				for (int a = 0; a < m; a++) {
					agentsCapacity[a] = Integer.parseInt(tokens[a]);
				}
				
				instances.add(new Instance(this.filePath, i+1, m, n, costs, resourcesNeeded, agentsCapacity));
				
			}

		} catch (NumberFormatException e) {
			Logger.get().err("Please provide an input file as described in: http://people.brunel.ac.uk/~mastjjb/jeb/orlib/gapinfo.html");
			this.errors = true;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
