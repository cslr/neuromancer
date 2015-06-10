package fi.iki.nop.neuromancer;

/**
 * Interface class for EEG signal source devices
 * @author Tomas
 *
 */
public interface SignalSource {
	
	/**
	 * Returns the number of output signals from the device
	 * @return the number of signals (>0)
	 */
	public int getNumberOfSignals();
	
	/**
	 * Returns index:th signal's signal name
	 * @param index signal index
	 * @return name of the signal
	 */
	public String getSignalName(int index);
	
	
	/**
	 * Returns index for signal name (signal number)
	 * @param name the name of the signal
	 * @return signal index
	 */
	public int getSignalNameNumber(String name);
	
	/**
	 * Returns current value of the signal
	 * @param index signal index
	 * @return value of the signal [0,1]
	 */
	public float getSignalValue(int index);

}
