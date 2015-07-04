package fi.iki.nop.neuromancer;

public class WildDivineSignalSourceStub implements SignalSource {

	@Override
	public int getNumberOfSignals() {
		return 2;
	}

	@Override
	public String getSignalName(int index) {
		if(index < 0) return null;
		
		if(index == 0)      return "Lightstone: Heart Rate";
		else if(index == 1) return "Lightstone: Conductance";
		else return "";
	}

	@Override
	public int getSignalNameNumber(String name) {		
		if(name == null) return -1;
		
		if     (name.compareTo("Lightstone: Heart Rate") == 0)  return 0;
		else if(name.compareTo("Lightstone: Conductance") == 0) return 1;
		else return -1;
	}

	@Override
	public float getSignalValue(int index) {
		return (float)Math.random();
	}

}
