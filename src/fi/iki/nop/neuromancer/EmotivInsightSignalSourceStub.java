package fi.iki.nop.neuromancer;

public class EmotivInsightSignalSourceStub implements SignalSource {

	@Override
	public int getNumberOfSignals() {
		return 8;
	}

	@Override
	public String getSignalName(int index) {
		if(index < 0) return null;
		
		if(index == 0)      return "Insight: Attention";
		else if(index == 1) return "Insight: Focus";
		else if(index == 2) return "Insight: Engagement";
		else if(index == 3) return "Insight: Interest";
		else if(index == 4) return "Insight: Excitement";
		else if(index == 5) return "Insight: Affinity";
		else if(index == 6) return "Insight: Relaxation";
		else if(index == 7) return "Insight: Stress";
		else return "";
	}

	@Override
	public float getSignalValue(int index) {
		
		if(index < 0 || index >= 8) return -1.0f;
		else return (float)Math.random();
	}

}
