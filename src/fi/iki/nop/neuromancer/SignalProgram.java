package fi.iki.nop.neuromancer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;

/**
 * Stimulation program
 * 
 * @author Tomas
 *
 */
public class SignalProgram {
	protected float[] targetSignal;
	protected String signalName;
	
	public final int SEC_WIDTH_GUI = 12;
	
	public SignalProgram(){
		targetSignal = new float[0];
	}
	
	
	public SignalProgram(int seconds){
		signalName = "<disabled>";
		
		if(seconds >= 0)
			targetSignal = new float[seconds];
		
		for(int i=0;i<targetSignal.length;i++)
			targetSignal[i] = -1.0f;
	}
	
	
	/**
	 * Returns signal name
	 * @return signal name
	 */
	public String getSignalName(){ return signalName; }
	
	
	/**
	 * Sets signal name
	 * @param name signal name
	 */
	public void setSignalName(String name){ signalName = name; }
	
	
	/**
	 * Draws new stimulation program to a canvas
	 * @param canvas canvas
	 */
	public Image draw(Canvas canvas){
		Point size = canvas.getSize();
		
		int imWidth = SEC_WIDTH_GUI*(targetSignal.length + 1);
		imWidth = imWidth + size.x;
		if(imWidth < size.x) imWidth = size.x;
		Image im = new Image(canvas.getDisplay(), imWidth, size.y);
		
		// Rectangle b = im.getBounds();
		
		// draws current simulation program to the image
		GC gc = new GC(im);
		gc.setAntialias(SWT.ON);
		gc.setForeground(canvas.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		
		int latestSecs = 0;
		float latestValue = 1.0f;
		
		for(int i=0;i<targetSignal.length;i++){
			if(targetSignal[i] >= 0.0){
				int startX = SEC_WIDTH_GUI/2 + latestSecs*SEC_WIDTH_GUI;
				int endX   = SEC_WIDTH_GUI/2 + i*SEC_WIDTH_GUI;
				
				int startY = (int)Math.floor((size.y - 20)*(1.0f - latestValue));
				int endY   = (int)Math.floor((size.y - 20)*(1.0f - targetSignal[i]));
				
				gc.drawLine(startX, startY, endX, endY);
				gc.fillOval(endX - SEC_WIDTH_GUI/4, endY - SEC_WIDTH_GUI/4, SEC_WIDTH_GUI/2, SEC_WIDTH_GUI/2);
				
				latestSecs = i;
				latestValue = targetSignal[i];
			}
		}
		
		// draws final line from the latest position to the end
		{
			int startX = SEC_WIDTH_GUI/2 + latestSecs*SEC_WIDTH_GUI;
			int endX   = SEC_WIDTH_GUI/2 + (targetSignal.length-1)*SEC_WIDTH_GUI;
			
			int startY = (int)Math.floor((size.y-20)*(1.0f - latestValue));
			int endY   = (int)Math.floor((size.y-20)*(1.0f - latestValue));
			
			gc.drawLine(startX, startY, endX, endY);
		}
		
		gc.dispose();
		
		return im;
	}
	
	/**
	 * Transforms image coordinates (canvas) into stimulation program second
	 * @param x image x coordinate
	 * @return program second (s)
	 */
	public int coordinateToSeconds(Canvas canvas, int x){
		int seconds = (x - SEC_WIDTH_GUI/2)/SEC_WIDTH_GUI;
		if(seconds < 0) seconds = 0;
		return seconds;
	}
	
	
	/**
	 * Transforms image coordinate y (canvas) into stimulation program value
	 * @param y image y coordinate
	 * @return stimulation program value [0,1]
	 */
	public float coordinateToValue(Canvas canvas, int y){
		Point size = canvas.getSize();
		float value = (float)(size.y - 20 - y)/((float)size.y - 20);
		if(value < 0.0f) value = 0.0f;
		else if(value > 1.0f) value = 1.0f;
		return value;
	}
	
	public boolean resizeProgram(int seconds){
		if(seconds < 0) return false;
		
		float[] p = new float[seconds];
		
		int min = seconds;
		if(targetSignal.length < min) min = targetSignal.length;
		
		for(int i=0;i<min;i++) p[i] = targetSignal[i];
		
		for(int i=min;i<p.length;i++) p[i] = -1.0f;
		
		targetSignal = p;
		
		return true;
	}
	
	
	/**
	 * Returns stimulation program
	 * @return stimulation program
	 */
	public float[] getProgram(){
		return targetSignal;
	}
	
	/**
	 * Sets stimulation program
	 * @param program new stimulation program
	 * @return true if setting new program was successful
	 */
	public boolean setProgram(float[] program){
		if(targetSignal == null){ targetSignal = program; return true; }
		else if(targetSignal.length == program.length){ targetSignal = program; return true; }
		else return false;
	}
	
	
	public float getProgramValue(int second){
		if(second >= 0 && second < targetSignal.length)
			return targetSignal[second];
		else
			return -1.0f;
	}
	
	public void setProgramValue(int second, float value){
		if(second >= 0 && second < targetSignal.length)
			targetSignal[second] = value;
	}

	/**
	 * Sets length of stimulation program
	 * @param seconds length in seconds
	 */
	public void setLength(int seconds){
		if(seconds <= 0) return;
		
		if(targetSignal == null){
			targetSignal = new float[seconds];
			for(int i=0;i<targetSignal.length;i++)
				targetSignal[i] = -1.0f;
			
			return;
		}
		
		float[] signal = new float[seconds];
			
		int min = seconds;
		if(targetSignal.length < min)
			min = targetSignal.length;
			
		for(int i=0;i<min;i++)
			signal[i] = targetSignal[i];
			
		for(int i=min;i<signal.length;i++)
			signal[i] = -1.0f;
			
		targetSignal = signal;
	}
	
	/**
	 * Length of stimulation program in seconds
	 * @return length in seconds
	 */
	public int getLength(){
		if(targetSignal == null) return 0;
		else return targetSignal.length;
	}
	
	
	/**
	 * Processes signals with "deepen" algorithm,
	 * signal values are moved/scaled away from 0.5 towards 0 and 1
	 */
	public void processDeepen()
	{
		final double alpha = 3.0; // proper values 3-5 higher value deepens signal more
		
		for(int i=0;i<targetSignal.length;i++){
			if(targetSignal[i] >= 0.0f){
				float t = targetSignal[i];
				
				t = (float)((1.0 + Math.tanh((t-0.5)*alpha))/2.0);
						
				targetSignal[i] = t;
			}			
		}
	}
	
	
	/**
	 * Process signals with "simplify" algorithm:
	 * goes through triples of real points [x(n-1), x(n), x(n+1)] and 
	 * calculates error of replacing x(n) with the mean value (x(n-1)+x(n+1))/2
	 * replaces the point with smallest error with "interpolation point"
	 */
	public void processSimplify()
	{
		if(targetSignal.length < 3)
			return; // too small signal: nothing to do
		
		int bestIndex = -1;
		float bestError = Float.POSITIVE_INFINITY;
		
		for(int i=0;i<(targetSignal.length-2);i++){
			if(targetSignal[i] < 0.0f)
				continue; // this is interpolation point: nothing to do
			
			// finds next two non-interpolation points
			int middleIndex = -1;
			int endIndex = -1;
			
			for(int j=(i+1);j<targetSignal.length;j++){
				if(targetSignal[j] >= 0.0f){
					middleIndex = j;
					break;
				}
			}
			
			for(int j=(middleIndex+1);j<targetSignal.length;j++){
				if(targetSignal[j] >= 0.0f){
					endIndex = j;
					break;
				}
			}
			
			if(middleIndex < 0 || endIndex < 0)
				continue; // couldn't find next points
			
			// now we have a triplet (i, middleIndex, endIndex)
			// calculates error we make if we replace middleIndex with interpolation
			
			float interpolation = targetSignal[i] + ((targetSignal[endIndex] - targetSignal[i])/((float)(endIndex - i)))*((float)(middleIndex - i));
			float error = Math.abs(interpolation - targetSignal[middleIndex]);
			
			if(error < bestError){
				bestIndex = middleIndex;
				// bestValue = interpolation;
				bestError = error;
			}
		}
		
		if(bestIndex >= 0){
			targetSignal[bestIndex] = -1;
		}
	}
}
