package fi.iki.nop.neuromancer;

public class MuseOSCDeviceSignalSourceStub implements SignalSource {

	@Override
	public int getNumberOfSignals() {
		return 6;
	}

	@Override
	public String getSignalName(int index) {
		if(index < 0) return null;
		
		if(index == 0)      return "Muse: Delta";
		else if(index == 1) return "Muse: Theta";
		else if(index == 2) return "Muse: Alpha";
		else if(index == 3) return "Muse: Beta";
		else if(index == 4) return "Muse: Gamma";
		else if(index == 5) return "Muse: Total Power";
		else return "";	
		
	}

	@Override
	public int getSignalNameNumber(String name) {
		
		if(name == null) return -1;
		
		if     (name.compareTo("Muse: Delta") == 0)       return 0;
		else if(name.compareTo("Muse: Theta") == 0)       return 1;
		else if(name.compareTo("Muse: Alpha") == 0)       return 2;
		else if(name.compareTo("Muse: Beta")  == 0)       return 3;
		else if(name.compareTo("Muse: Gamma") == 0)       return 4;
		else if(name.compareTo("Muse: Total Power") == 0) return 5;

		else return -1;
	}

	@Override
	public float getSignalValue(int index) {
		return (float)Math.random();
	}

}
