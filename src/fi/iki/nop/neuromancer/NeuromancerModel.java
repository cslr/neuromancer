package fi.iki.nop.neuromancer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
	
	
	/**
	 * Saves neuromancer signal program preset
	 * @param filename filename into which the program is stored
	 * @return true if save was successfully, false otherwise
	 */
	public boolean saveProgramPreset(String filename)
	{
		String  signal1Name = "<disabled>";
		float[] signal1Data = new float[0];
		String  signal2Name = "<disabled>";
		float[] signal2Data = new float[0];
		int dataLength      = 0;
		
		// loads program data into variables
		signal1Name = this.getProgram(0).getSignalName();
		signal1Data = this.getProgram(0).getProgram();
		signal2Name = this.getProgram(1).getSignalName();
		signal2Data = this.getProgram(1).getProgram();
		
		if(signal1Name == null) signal1Name = "<disabled>";
		if(signal2Name == null) signal2Name = "<disabled>";
		
		if(signal1Data.length != signal2Data.length)
			return false;
		
		dataLength = signal1Data.length;		

		// converts everything into byte arrays which are then written to disk
		
		if(signal1Name.length() > 32) signal1Name = signal1Name.substring(0, 32);
		while(signal1Name.length() < 32)
			signal1Name = signal1Name + " ";
		
		byte[] name1;
		try{ name1 = signal1Name.getBytes(StandardCharsets.UTF_16LE); }
		catch(Exception e){ return false; }
		
		if(signal2Name.length() > 32) signal2Name = signal2Name.substring(0, 32);
		while(signal2Name.length() < 32)
			signal2Name = signal2Name + " ";
		
		byte[] name2;
		try{ name2 = signal2Name.getBytes(StandardCharsets.UTF_16LE); }
		catch(Exception e){ return false; }
		
		ByteBuffer b = ByteBuffer.allocate(4);
		b.order(ByteOrder.LITTLE_ENDIAN);
		b.putInt(dataLength);
		
		byte[] length = b.array();
		
		b = ByteBuffer.allocate(4*signal1Data.length);
		b.order(ByteOrder.LITTLE_ENDIAN);
		
		for(int i=0;i<signal1Data.length;i++)
			b.putFloat(signal1Data[i]);
		
		byte[] signal1 = b.array();
		
		b = ByteBuffer.allocate(4*signal2Data.length);
		b.order(ByteOrder.LITTLE_ENDIAN);
		
		for(int i=0;i<signal2Data.length;i++)
			b.putFloat(signal2Data[i]);
		
		byte[] signal2 = b.array();
		
		// stores to file: { name1, name2, length, signal1, signal2 }
		
		try{
			Path file = Paths.get(filename);
			System.out.println("A");
			Files.write(file, name1);
			System.out.println("A");
			Files.write(file, name2, StandardOpenOption.APPEND);
			System.out.println("A");
			Files.write(file, length, StandardOpenOption.APPEND);
			System.out.println("A");
			Files.write(file, signal1, StandardOpenOption.APPEND);
			System.out.println("A");
			Files.write(file, signal2, StandardOpenOption.APPEND);
			System.out.println("A");
			
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	
	/**
	 * Loads neuromancer signal program preset
	 * @param filename filename from which the program is loaded
	 * @return true if load was successfully, false otherwise
	 */
	public boolean loadProgramPreset(String filename){
		// TODO implement me!
		return false;
	}
	
}
