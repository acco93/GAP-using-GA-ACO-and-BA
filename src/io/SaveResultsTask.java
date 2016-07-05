package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.swing.JFileChooser;

import model.Result;

public class SaveResultsTask implements Runnable {
	
	private Map<String, Result> results;

	public SaveResultsTask(Map<String, Result> results) {
		this.results = results;
	}

	@Override
	public void run() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setSelectedFile(new File("results " + new Date() + ".csv"));
		if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			new Thread(() -> {
				BufferedWriter output;
				try {
					output = new BufferedWriter(new FileWriter(file));
					Collection<Result> collection = results.values();
					ArrayList<Result> list = new ArrayList<Result>(collection);
					Collections.sort(list, (a, b) -> {
						return a.getInstance().getLogicalName().compareTo(b.getInstance().getLogicalName());
					});
					output.write("Instance,Runs,GA best value,GA avg value,GA avg time (ms),ANTS Best value, ANTS avg value, ANTS avg time (ms)\n");
					for(Result result:list){
						output.write(result.getInstance().getLogicalName()+",");
						output.write(result.getRuns()+",");
						output.write(result.getGaBestValue()+",");
						output.write(result.getGaAvgValue()+",");
						output.write(result.getGaAvgTime()+",");
						output.write(result.getAntsBestValue()+",");
						output.write(result.getAntsAvgValue()+",");
						output.write(result.getAntsAvgTime()+"\n");
					}						
					output.write("");
					output.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}).start();
		}
		
	}

}
