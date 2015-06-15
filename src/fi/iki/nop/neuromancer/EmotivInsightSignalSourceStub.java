package fi.iki.nop.neuromancer;

public class EmotivInsightSignalSourceStub implements SignalSource {

	@Override
	public int getNumberOfSignals() {
		return 8;
	}

	@Override
	public String getSignalName(int index) {
		if(index < 0) return null;
		
		if(index == 0)      return "Insight: Excitement";
		else if(index == 1) return "Insight: Relaxation";
		else if(index == 2) return "Insight: Stress";
		else if(index == 3) return "Insight: Engagement";
		else if(index == 4) return "Insight: Interest";
		else return "";
	}

	
	@Override
	public int getSignalNameNumber(String name) {
		if(name == null) return -1;
		
		if     (name.compareTo("Insight: Excitement") == 0) return 0;
		else if(name.compareTo("Insight: Relaxation") == 0) return 1;
		else if(name.compareTo("Insight: Stress") == 0)     return 2;
		else if(name.compareTo("Insight: Engagement") == 0) return 3;
		else if(name.compareTo("Insight: Interest") == 0)   return 4;
		else return -1;
	}

	@Override
	public float getSignalValue(int index) {
		
		if(index < 0 || index >= 8) return -1.0f;
		else return (float)Math.random();
	}

}
