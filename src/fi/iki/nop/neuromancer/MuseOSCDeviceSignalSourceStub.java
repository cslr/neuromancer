package fi.iki.nop.neuromancer;

public class MuseOSCDeviceSignalSourceStub implements SignalSource {

	@Override
	public int getNumberOfSignals() {
		return 5;
	}

	@Override
	public String getSignalName(int index) {
		if(index < 0) return null;
		
		if(index == 0)      return "Muse: Relative Alpha";
		else if(index == 1) return "Muse: Relative Beta";
		else if(index == 2) return "Muse: Relative Delta";
		else if(index == 3) return "Muse: Relative Gamma";
		else if(index == 4) return "Muse: Relative Theta";		
		else return "";	
		
	}

	@Override
	public int getSignalNameNumber(String name) {
		
		if(name == null) return -1;
		
		if     (name.compareTo("Muse: Relative Alpha") == 0) return 0;
		else if(name.compareTo("Muse: Relative Beta") == 0)  return 1;
		else if(name.compareTo("Muse: Relative Delta") == 0) return 2;
		else if(name.compareTo("Muse: Relative Gamma") == 0) return 3;
		else if(name.compareTo("Muse: Relative Theta") == 0) return 4;

		else return -1;
	}

	@Override
	public float getSignalValue(int index) {
		return (float)Math.random();
	}

}
