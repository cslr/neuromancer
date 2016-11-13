package fi.iki.nop.neuromancer;

public class RandomEEGSignalSourceStub implements SignalSource {

	@Override
	public int getNumberOfSignals() {
		return 6;
	}

	@Override
	public String getSignalName(int index) {
		if(index < 0) return null;
		
		if(index == 0)      return "Random EEG 1";
		else if(index == 1) return "Random EEG 2";
		else if(index == 2) return "Random EEG 3";
		else if(index == 3) return "Random EEG 4";
		else if(index == 4) return "Random EEG 5";		
		else if(index == 5) return "Random EEG 6";
		else return "";		
	}

	@Override
	public int getSignalNameNumber(String name) {
		
		if(name == null) return -1;
		
		if     (name.compareTo("Random EEG 1") == 0) return 0;
		else if(name.compareTo("Random EEG 2") == 0) return 1;
		else if(name.compareTo("Random EEG 3") == 0) return 2;
		else if(name.compareTo("Random EEG 4") == 0) return 3;
		else if(name.compareTo("Random EEG 5") == 0) return 4;
		else if(name.compareTo("Random EEG 6") == 0) return 5;

		else return -1;
	}

	@Override
	public float getSignalValue(int index) {
		return (float)Math.random();
	}

}
