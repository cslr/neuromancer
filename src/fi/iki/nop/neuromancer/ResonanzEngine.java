package fi.iki.nop.neuromancer;

/**
 * Java Native Interface (JNI) 64-bit interface between NeuromancerUI and ResonanzEngine
 * @author Tomas
 *
 */
public class ResonanzEngine {
	
	static {
		System.loadLibrary("resonanz-engine");
	}
	
	
	public ResonanzEngine(){
		System.out.println("Starting resonanz-engine..");
		// FIXME write routine to initialize (start) engine thread which is called here
	}
	


	/**
	 * Starts showing random stimulation (pictures, keywords) from the given picture directory and keywords file
	 * 
	 * @param pictureDir folder containing pictures to show (png, jpg)
	 * @param keywordsFile text file containing keywords to show together with keywords (one keyword per line)
	 * @return true if stimulation started successfully, false if there was error and stimulation didn't start
	 */
	public native boolean startRandomStimulation(String pictureDir, String keywordsFile);
	
	/**
	 * Starts measuring (Emotiv Affectiv) responses to random stimulation (pictures, keywords).
	 * Measurements are stored to the model directory.
	 * 
	 * @param pictureDir folder containing pictures (png, jpg)
	 * @param keywordsFile text file with keywords (one per line)
	 * @param modelDirectory directory where measurements are stored
	 * @return true if measurements and stimulation started successfully and false if there was an error
	 */
	public native boolean startMeasureStimulation(String pictureDir, String keywordsFile, String modelDirectory);
	
	
	/**
	 * Starts computing computer model to predict responses given pictures. f(current_eeg,picture) = response_eeg
	 * 
	 * @param modelDirectory directory into which measurements and optimized models are stored
	 * @return true if optimization process was started successfully and false if it did not start
	 */
	public native boolean startOptimizeModel(String modelDirectory);
	
	
	/**
	 * Gets current status of the optimization
	 * @return null if there is no running optimization process, otherwise current status (including ETA) of the process
	 */
	public native String getOptimizeModelStatus(); // deprecated WILL BE REMOVED
	
	/**
	 * Stops model optimization
	 * @return true if running optimization process was successfully stopped and false there was an error or no process to stop 
	 */
	public native boolean stopOptimizeModel(); // deprecated WILL BE REMOVED
	
	
	/**
	 * Gets current status of the resonanz-engine: optimization status etc.
	 * @return current status of the resonanz-engine or empty string (rarely)
	 */
	public native String getStatusLine();
	
	
	/**
	 * Stops current resonanz-engine activity and resets into idle state
	 * @return false if there was an error and true otherwise
	 */
	public native boolean stopCommand();
}
