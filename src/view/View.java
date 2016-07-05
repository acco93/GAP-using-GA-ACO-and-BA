package view;

import java.io.File;
import java.util.List;

import model.State;

/**
 * 
 * View interface
 * 
 * @author acco
 * 
 * Jul 5, 2016 7:29:02 AM
 *
 */
public interface View {

	/**
	 * Get the list of file to process.
	 * @return files list
	 */
	List<File> getFilePaths();
	
	/**
	 * Update the label state.
	 * @param state
	 */
	void setState(State state);

	/**
	 * Disable all the buttons during processing to avoid race conditions.
	 */
	void disableInput();

	/**
	 * The computation is finished so re-enable all the buttons!
	 */
	void enableInput();

	/**
	 * Ask the model for the updated results.
	 */
	void refreshResults();

}
