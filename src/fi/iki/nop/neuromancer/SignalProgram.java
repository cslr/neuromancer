package fi.iki.nop.neuromancer;

/**
 * Stimulation program
 * 
 * @author Tomas
 *
 */
public class SignalProgram {
	protected float[] targetSignal;
	protected String signalName;
	
	/**
	 * Returns stimulation program
	 * @return stimulation program
	 */
	public float[] getProgram(){
		return targetSignal;
	}
	
	/**
	 * Sets stimulation program
	 * @param program new stimulation program
	 * @return true if setting new program was successful
	 */
	public boolean setProgram(float[] program){
		if(targetSignal == null){ targetSignal = program; return true; }
		else if(targetSignal.length == program.length){ targetSignal = program; return true; }
		else return false;
	}
	
	
	public float getProgramValue(int second){
		if(second >= 0 && second < targetSignal.length)
			return targetSignal[second];
		else
			return -1.0f;
	}
	
	public void setProgramValue(int second, float value){
		if(second >= 0 && second < targetSignal.length)
			targetSignal[second] = value;
	}

	/**
	 * Sets length of stimulation program
	 * @param seconds length in seconds
	 */
	public void setLength(int seconds){
		if(seconds <= 0) return;
		
		if(targetSignal == null){
			targetSignal = new float[seconds];
			for(int i=0;i<targetSignal.length;i++)
				targetSignal[i] = -1.0f;
			
			return;
		}
		
		float[] signal = new float[seconds];
			
		int min = seconds;
		if(targetSignal.length < min)
			min = targetSignal.length;
			
		for(int i=0;i<min;i++)
			signal[i] = targetSignal[i];
			
		for(int i=min;i<signal.length;i++)
			signal[i] = -1.0f;
			
		targetSignal = signal;
	}
	
	/**
	 * Length of stimulation program in seconds
	 * @return length in seconds
	 */
	public int getLength(){
		if(targetSignal == null) return 0;
		else return targetSignal.length;
	}
}
