package fi.iki.nop.neuromancer;

public class NoEEGDeviceSignalSourceStub implements SignalSource {

	@Override
	public int getNumberOfSignals() {
		return 1;
	}

	@Override
	public String getSignalName(int index) {
		return "Empty Signal";
	}

	@Override
	public int getSignalNameNumber(String name) {
		return 0;
	}

	@Override
	public float getSignalValue(int index) {
		return 0.5f;
	}

}
