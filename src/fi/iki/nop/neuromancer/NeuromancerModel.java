package fi.iki.nop.neuromancer;

public class NeuromancerModel
{
	protected final String SOFTWARE_NAME = "Neuromancer";
	protected final String VERSION = "0.01 alpha";
	
	protected String pictureDirectory;
	protected String keywordsFile;
	protected String modelDirectory;
	
	private int programLength; // length of program in seconds
	
	private SignalProgram[] programs; // Emotiv meta-signal programs
	
	
	public NeuromancerModel(){
		programLength = 60;
		programs = new SignalProgram[2];
		
		for(int i=0;i<programs.length;i++){
			programs[i] = new SignalProgram(programLength);
			programs[i].setProgramValue(0, 1.0f);
			programs[i].setProgramValue(programLength-1, 1.0f);
		}
	}
	
	
	/**
	 * Returns software name
	 * @return software name
	 */
	public String getSoftwareName(){ return SOFTWARE_NAME; }
	
	/**
	 * Returns software version number
	 * @return version number
	 */
	public String getVersion(){ return VERSION; }
	
	public String getPictureDirectory(){ return pictureDirectory; }
	public void setPictureDirectory(String picdir){ pictureDirectory = picdir; }
	
	public String getKeywordsFile(){ return keywordsFile; }
	public void setKeywordsFile(String keywords){ keywordsFile = keywords; }
	
	public String getModelDirectory(){ return modelDirectory; }
	public void setModelDirectory(String model){ modelDirectory = model; }
	
	public int getProgramLength(){ return programLength; }
	
	public boolean setProgramLength(int seconds){
		if(seconds < 0) return false;
		
		programLength = seconds;
		for(int i=0;i<programs.length;i++)
			programs[i].resizeProgram(seconds);
		
		return true;
	}
	
	
	public SignalProgram getProgram(int index){
		return programs[index];
	}
	
	
}
