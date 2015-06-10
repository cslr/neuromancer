package fi.iki.nop.neuromancer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class NeuromancerModel
{
	protected final String SOFTWARE_NAME = "Neuromancer";
	protected final String VERSION = "0.02 alpha";
	
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
		signal1Name = this.getProgram(0).getSignalName().trim();
		signal1Data = this.getProgram(0).getProgram();
		signal2Name = this.getProgram(1).getSignalName().trim();
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
			Files.write(file, name1);
			Files.write(file, name2, StandardOpenOption.APPEND);
			Files.write(file, length, StandardOpenOption.APPEND);
			Files.write(file, signal1, StandardOpenOption.APPEND);
			Files.write(file, signal2, StandardOpenOption.APPEND);
			
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
		byte[] input;
		
		try{
			Path file = Paths.get(filename);
			input = Files.readAllBytes(file);
		}
		catch(Exception e){ return false; }
		
		if(input.length < (64+64+4))
			return false; // doesn't contain signal names and signal length information
		
		try{
			byte[] name1  = Arrays.copyOfRange(input, 0, 64);
			byte[] name2  = Arrays.copyOfRange(input, 64, 128);
			byte[] length = Arrays.copyOfRange(input, 128, 128+4);
			
			String n1      = ByteBuffer.wrap(name1).order(ByteOrder.LITTLE_ENDIAN).asCharBuffer().toString().trim();
			String n2      = ByteBuffer.wrap(name2).order(ByteOrder.LITTLE_ENDIAN).asCharBuffer().toString().trim();
			int dataLength = ByteBuffer.wrap(length).order(ByteOrder.LITTLE_ENDIAN).getInt();
			
			byte[] data1  = Arrays.copyOfRange(input, 128+4, 128+4+dataLength*4);
			byte[] data2  = Arrays.copyOfRange(input, 128+4+dataLength*4, 128+4+dataLength*4+dataLength*4);
			
			FloatBuffer f1 = ByteBuffer.wrap(data1).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			FloatBuffer f2 = ByteBuffer.wrap(data2).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
			
			float[] s1 = new float[f1.limit()];
			float[] s2 = new float[f2.limit()];
			
			if(dataLength != s1.length || dataLength != s2.length)
				return false; // something went wrong
			
			for(int i=0;i<s1.length;i++){
				s1[i] = f1.get(i);
				s2[i] = f2.get(i);
			}
			
			this.setProgramLength(dataLength);
			this.getProgram(0).setSignalName(n1);
			this.getProgram(0).setProgram(s1);
			this.getProgram(1).setSignalName(n2);
			this.getProgram(1).setProgram(s2);
		}
		catch(Exception e){ 
			return false; 
		}
		
		return true;
	}
	
}
