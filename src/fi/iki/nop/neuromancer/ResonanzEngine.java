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
	public native boolean startOptimizeModel(String pictureDir, String keywordsFile, String modelDirectory);
	
	
	/**
	 * Starts executing stimulation program according to target values
	 * 
	 * @param pictureDir source picture dir to show pictures from
	 * @param keywordsFile source keyword file to show keywords from
	 * @param modelDir model directory to estimate reactions to a model
	 * @param targetNames signal target names (Emotiv metasignal names)
	 * @param programs float valued targets for program names
	 * @return true if starting showing the target command was successful and false otherwise
	 */
	public native boolean startExecuteProgram(String pictureDir, String keywordsFile, String modelDir, 
			String[] targetNames, float[][] programs);
	/*
	{
		
		System.out.println("EXECUTE COMMAND 1: " + pictureDir + " " + keywordsFile);
		
		if(targetNames.length != programs.length)
			return false;
		
		System.out.println("EXECUTE COMMAND 2: ");
		
		for(int i=0;i<targetNames.length;i++)
			System.out.println(targetNames[i]);
		
		for(int i=0;i<programs.length;i++)
			System.out.println(programs[i].length);
		
		return true;
	}
	*/
	
	
	/**
	 * Stops current resonanz-engine activity and resets into idle state
	 * @return false if there was an error and true otherwise
	 */
	public native boolean stopCommand();
	
	
	/**
	 * Returns true if resonanz-engine is executing some command which will be overriden 
	 * if a new command is given
	 * @return true if engine is executing or about to execute new command (incoming command) and false if the engine is idle
	 */
	public native boolean isBusy();
	
	
	/**
	 * Gets current status of the resonanz-engine: optimization status etc.
	 * @return current status of the resonanz-engine or empty string (rarely)
	 */
	public native String getStatusLine();
	
	
	/**
	 * Returns database size and model performance information
	 * @param modelDir database and model directory
	 * @return text string about database size and model performace
	 */
	public native String getAnalyzeModel(String modelDir);
	
}
