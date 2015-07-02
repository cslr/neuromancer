package fi.iki.nop.neuromancer;


import java.io.File;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;

import swing2swt.layout.FlowLayout;



public class NeuromancerWindow {
	
	protected static NeuromancerModel model;
	protected static NeuromancerWindow window;
	protected static SignalSource eeg;
	protected static ResonanzEngine engine;
	
	protected Shell shell;
	protected Display display;
	
	private final Image[] programCanvas = new Image[2];
	
	private Button btnRandom;
	private Button btnMeasure;
	private Button btnLearn;
	private Button btnStopAction;
	private Text statusLine;
	
	private Label lblDatabasemodelInformation;
	private Text audioFileText;
	
	private MenuItem mntmResetDatabase;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			eeg    = new EmotivInsightSignalSourceStub();
			engine = new ResonanzEngine();
			model  = new NeuromancerModel();
			window = new NeuromancerWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		
		// creates updater thread that updates UI periodically
		
		Thread updaterThread = new Thread(){
			public void run(){
				while(true){
					
					Display.getDefault().syncExec(new Runnable(){
						public void run(){
							if(!shell.isDisposed()){
								boolean busy = engine.isBusy();
								
								if(busy){
									btnRandom.setEnabled(false);
									btnMeasure.setEnabled(false);
									btnLearn.setEnabled(false);
									btnStopAction.setEnabled(true);
									mntmResetDatabase.setEnabled(false);
								}
								else{
									btnRandom.setEnabled(true);
									btnMeasure.setEnabled(true);
									btnLearn.setEnabled(true);
									btnStopAction.setEnabled(false);
									mntmResetDatabase.setEnabled(true);
								}
								
								String engineState = engine.getStatusLine();
								if(engineState == null) engineState = "";
								window.statusLine.setText(engineState);
							}
						}
					});
					
					try{ Thread.sleep(500); } // 500ms
					catch(InterruptedException e){ }
				}
			}
		};
		
		updaterThread.setDaemon(true);
		updaterThread.start();
		
		
		Thread databaseScanThread = new Thread(){
			public void run(){
				while(true){
					
					Display.getDefault().syncExec(new Runnable(){
						public void run(){
							if(!shell.isDisposed()){
								String modelDir = model.getModelDirectory();
								if(modelDir == null) return;
								String dbInfo = engine.getAnalyzeModel(model.getModelDirectory());
								
								if(dbInfo != null)
									window.lblDatabasemodelInformation.setText(dbInfo);
								else
									window.lblDatabasemodelInformation.setText("");								
							}
						}
					});
					
					try{ Thread.sleep(10000); } // 10 second interval
					catch(InterruptedException e){ }
				}
			}
		};
		
		databaseScanThread.setDaemon(true);
		databaseScanThread.start();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(display);
		shell.setSize(600, 500);
		shell.setText(model.getSoftwareName());
		display.setAppName(model.getSoftwareName());
		display.setAppVersion(model.getVersion());
		
		shell.setImage(new Image(display, "brain.ico"));
		shell.setLayout(new GridLayout(1, false));
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmNew = new MenuItem(menu_1, SWT.NONE);
		mntmNew.setText("New program");
		
		MenuItem mntmOpenPreset = new MenuItem(menu_1, SWT.NONE);
		mntmOpenPreset.setText("Open program..");
		
		MenuItem mntmSaveProgram = new MenuItem(menu_1, SWT.NONE);
		mntmSaveProgram.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
				fd.setText("Save stimulation program..");
				fd.setFilterExtensions(new String[] { "*.nmc", "*.*" });
				String filename = fd.open();
				
				if(filename != null){
					if(model.saveProgramPreset(filename))
						System.out.println("Saving program: " + filename);
				}
			}
		});
		mntmSaveProgram.setText("Save program..");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
				
		mntmResetDatabase = new MenuItem(menu_1, SWT.NONE);
		mntmResetDatabase.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
		        MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		        messageBox.setMessage("Do you really want to delete current measurements?");
		        messageBox.setText("Delete database");
		        
		        if (messageBox.open() == SWT.YES){
		        	String modelDir = model.getModelDirectory();
		        	if(new File(modelDir).exists())
		        		engine.deleteModelData(modelDir);
		        }
		        
			}
		});
		mntmResetDatabase.setText("Reset database");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem mntmExit = new MenuItem(menu_1, SWT.NONE);
		mntmExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.setVisible(false);
				shell.close();
				shell.dispose();
				// System.exit(0);
			}
		});
		mntmExit.setText("Exit");
		
		MenuItem mntmDevice = new MenuItem(menu, SWT.CASCADE);
		mntmDevice.setText("Device");
		
		Menu menu_2 = new Menu(mntmDevice);
		mntmDevice.setMenu(menu_2);
		
		final MenuItem mntmNoDevice;
		final MenuItem mntmRandomRng;
		final MenuItem mntmEmotivEpoc;
		final MenuItem mntmEmotivInsight;
		final MenuItem mntmMuseOsc;
		
		mntmNoDevice = new MenuItem(menu_2, SWT.RADIO);
		mntmNoDevice.setSelection(true);
		mntmNoDevice.setText("No device");
		
		mntmRandomRng = new MenuItem(menu_2, SWT.RADIO);
		mntmRandomRng.setText("Random RNG");
		
		mntmEmotivEpoc = new MenuItem(menu_2, SWT.RADIO);
		mntmEmotivEpoc.setEnabled(false);
		mntmEmotivEpoc.setText("Emotiv EPOC");
		
		mntmEmotivInsight = new MenuItem(menu_2, SWT.RADIO);
		mntmEmotivInsight.setText("Emotiv Insight");
		
		mntmMuseOsc = new MenuItem(menu_2, SWT.RADIO);
		mntmMuseOsc.setEnabled(false);
		mntmMuseOsc.setText("Muse OSC");

		//////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////
		
		mntmNoDevice.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(engine.setEEGSourceDevice(ResonanzEngine.RE_EEG_NO_DEVICE))
					model.setEEGSourceDevice(ResonanzEngine.RE_EEG_NO_DEVICE);
				else{
					int selection = engine.getEEGSourceDevice();
					mntmNoDevice.setSelection(false);
					mntmRandomRng.setSelection(false);
					mntmEmotivEpoc.setSelection(false);
					mntmEmotivInsight.setSelection(false);
					mntmMuseOsc.setSelection(false);
					
					if(selection == ResonanzEngine.RE_EEG_NO_DEVICE)
						mntmNoDevice.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_RANDOM_DEVICE)
						mntmRandomRng.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_EMOTIV_INSIGHT_DEVICE)
						mntmEmotivInsight.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_IA_MUSE_DEVICE)
						mntmMuseOsc.setSelection(true);
				}
			}
		});
		
		mntmRandomRng.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(engine.setEEGSourceDevice(ResonanzEngine.RE_EEG_RANDOM_DEVICE))
					model.setEEGSourceDevice(ResonanzEngine.RE_EEG_RANDOM_DEVICE);
				else{
					int selection = engine.getEEGSourceDevice();
					mntmNoDevice.setSelection(false);
					mntmRandomRng.setSelection(false);
					mntmEmotivEpoc.setSelection(false);
					mntmEmotivInsight.setSelection(false);
					mntmMuseOsc.setSelection(false);
					
					if(selection == ResonanzEngine.RE_EEG_NO_DEVICE)
						mntmNoDevice.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_RANDOM_DEVICE)
						mntmRandomRng.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_EMOTIV_INSIGHT_DEVICE)
						mntmEmotivInsight.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_IA_MUSE_DEVICE)
						mntmMuseOsc.setSelection(true);
				}
			}
		});		
		
		mntmEmotivEpoc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});		
		
		mntmEmotivInsight.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(engine.setEEGSourceDevice(ResonanzEngine.RE_EEG_EMOTIV_INSIGHT_DEVICE))
					model.setEEGSourceDevice(ResonanzEngine.RE_EEG_EMOTIV_INSIGHT_DEVICE);
				else{
					int selection = engine.getEEGSourceDevice();
					mntmNoDevice.setSelection(false);
					mntmRandomRng.setSelection(false);
					mntmEmotivEpoc.setSelection(false);
					mntmEmotivInsight.setSelection(false);
					mntmMuseOsc.setSelection(false);
					
					if(selection == ResonanzEngine.RE_EEG_NO_DEVICE)
						mntmNoDevice.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_RANDOM_DEVICE)
						mntmRandomRng.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_EMOTIV_INSIGHT_DEVICE)
						mntmEmotivInsight.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_IA_MUSE_DEVICE)
						mntmMuseOsc.setSelection(true);
				}
			}
		});		
		
		mntmMuseOsc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(engine.setEEGSourceDevice(ResonanzEngine.RE_EEG_IA_MUSE_DEVICE))
					model.setEEGSourceDevice(ResonanzEngine.RE_EEG_IA_MUSE_DEVICE);
				else{
					int selection = engine.getEEGSourceDevice();
					mntmNoDevice.setSelection(false);
					mntmRandomRng.setSelection(false);
					mntmEmotivEpoc.setSelection(false);
					mntmEmotivInsight.setSelection(false);
					mntmMuseOsc.setSelection(false);
					
					if(selection == ResonanzEngine.RE_EEG_NO_DEVICE)
						mntmNoDevice.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_RANDOM_DEVICE)
						mntmRandomRng.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_EMOTIV_INSIGHT_DEVICE)
						mntmEmotivInsight.setSelection(true);
					else if(selection == ResonanzEngine.RE_EEG_IA_MUSE_DEVICE)
						mntmMuseOsc.setSelection(true);
				}
			}
		});
		
		//////////////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////////////

		
		new MenuItem(menu_2, SWT.SEPARATOR);
		
		MenuItem mntmCheckStatus = new MenuItem(menu_2, SWT.NONE);
		mntmCheckStatus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String status = engine.getEEGDeviceStatus();
				if(status == null) status = "";
				
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				mbox.setText("EEG Device Status");
				
				if(status.length() == 0)
					status = "Internal error.";
				
				mbox.setMessage(status);
				mbox.open();
			}
		});
		mntmCheckStatus.setText("Check status..");
		
		MenuItem mntmSettings = new MenuItem(menu, SWT.CASCADE);
		mntmSettings.setText("Settings");
		
		Menu menu_4 = new Menu(mntmSettings);
		mntmSettings.setMenu(menu_4);
		
		final MenuItem mntmAutofill = new MenuItem(menu_4, SWT.CHECK);
		mntmAutofill.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setAutoFill(mntmAutofill.getSelection());
			}
		});
		mntmAutofill.setSelection(model.getAutoFill());
		mntmAutofill.setText("Autofill");
		
		new MenuItem(menu_4, SWT.SEPARATOR);
		
		final MenuItem mntmBlindMonteCarlo = new MenuItem(menu_4, SWT.CHECK);
		mntmBlindMonteCarlo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setBlindMonteCarloMode(mntmBlindMonteCarlo.getSelection());
			}
		});
		mntmBlindMonteCarlo.setText("Blind Monte Carlo");
		mntmBlindMonteCarlo.setSelection(model.getBlindMonteCarloMode());
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		Menu menu_3 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_3);
		
		MenuItem mntmHtmlHelp = new MenuItem(menu_3, SWT.NONE);
		mntmHtmlHelp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String path = System.getProperty("user.dir");
				org.eclipse.swt.program.Program.launch(path + "/help/index.html");
			}
		});
		mntmHtmlHelp.setText("HTML help");
		
		new MenuItem(menu_3, SWT.SEPARATOR);
		
		MenuItem mntmAbout = new MenuItem(menu_3, SWT.NONE);
		mntmAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				mbox.setText("About..");
				
				String msg = model.getSoftwareName() + " " + model.getVersion() + " (64bit)\n";
				msg += "© Copyright Tomas Ukkonen 2015 <tomas.ukkonen@iki.fi>\n";
				msg += "\n";
				msg += "This software requires Emotiv Insight (tm) device <www.emotiv.com>.\n";
				msg += "\n";
				msg += "Library licenses can be found from the application root directory.\n";
				msg += "You can obtain source code of the libraries from their respective websites.\n";
				
				mbox.setMessage(msg);
				mbox.open();
			}
		});
		mntmAbout.setText("About");
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		TabItem tbtmInputModel = new TabItem(tabFolder, SWT.NONE);
		tbtmInputModel.setText("Input && Model");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmInputModel.setControl(composite);
		composite.setLayout(new GridLayout(3, false));
		
		Label lblDatabaseDirectory = new Label(composite, SWT.NONE);
		lblDatabaseDirectory.setText("Picture folder");
		
		final Text text_3 = new Text(composite, SWT.BORDER);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_3.setEditable(false);
		if(model.getPictureDirectory() != null) text_3.setText(model.getPictureDirectory());
		else text_3.setText("");

		
		Button btnSelect = new Button(composite, SWT.NONE);
		btnSelect.setText("Select");
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setText("Keywords file");
		
		final Text text_4 = new Text(composite, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_4.setEditable(false);
		if(model.getKeywordsFile() != null) text_4.setText(model.getKeywordsFile());
		else text_4.setText("");
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.txt", "*.*" });
				String filename = fd.open();
				
				if(filename != null){
					model.setKeywordsFile(filename);
					text_4.setText(filename);
				}
			}
		});
		btnNewButton.setText("Select");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setText("Database/Model folder");
		
		final Text text_5 = new Text(composite, SWT.BORDER);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_5.setEditable(false);
		if(model.getModelDirectory() != null) text_5.setText(model.getModelDirectory());
		else text_5.setText("");
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(shell);
				dd.setFilterPath("c:\\");
				String directory = dd.open();
				
				if(directory != null){
					model.setModelDirectory(directory);
					text_5.setText(directory);
				}
			}
		});
		btnNewButton_1.setText("Select");
		
		btnSelect.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(shell);
				dd.setFilterPath("c:\\");
				String directory = dd.open();
				
				if(directory != null){
					model.setPictureDirectory(directory);
					text_3.setText(directory);
				}
				
				if(model.getAutoFill()){
					String keywordFile = directory + File.separator + "keywords.txt";
					String modelDir = directory + File.separator + "datamodel";
					
					// creates new model directory if needed/possible
					File dir = new File(modelDir);
					if(!dir.exists()){
						try{ dir.mkdir(); } 
						catch(SecurityException se){ }
					}
					
					File keyword = new File(keywordFile);
					
					if(dir.exists()){
						model.setModelDirectory(modelDir);
						text_5.setText(modelDir);
					}
					
					{					
						model.setKeywordsFile(keywordFile);
						text_4.setText(keywordFile);
					}
				}
			}
		});
		
		new Label(composite, SWT.NONE);
		
		lblDatabasemodelInformation = new Label(composite, SWT.NONE);
		lblDatabasemodelInformation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblDatabasemodelInformation.setText("Database/model information");
		lblDatabasemodelInformation.setToolTipText("Model optimization requires at least 10 samples for all instances.");
		
		new Label(composite, SWT.NONE);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, true, 3, 1));
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		btnRandom = new Button(composite_1, SWT.NONE);
		btnRandom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				engine.startRandomStimulation(model.getPictureDirectory(), model.getKeywordsFile());
			}
		});
		btnRandom.setText("Test input");
		
		btnMeasure = new Button(composite_1, SWT.NONE);
		btnMeasure.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				engine.startMeasureStimulation(model.getPictureDirectory(), model.getKeywordsFile(), model.getModelDirectory());
			}
		});
		btnMeasure.setText("Measure database");
		
		btnLearn = new Button(composite_1, SWT.NONE);
		btnLearn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				engine.startOptimizeModel(
						model.getPictureDirectory(),
						model.getKeywordsFile(),
						model.getModelDirectory());
			}
		});
		btnLearn.setText("Optimize model");
		btnLearn.setToolTipText("Optimization requires > 10 examples per picture/keyword.");
		
		btnStopAction = new Button(composite_1, SWT.NONE);
		btnStopAction.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				engine.stopCommand();
			}
		});
		btnStopAction.setText("Stop activity");

		TabItem tbtmProgram = new TabItem(tabFolder, SWT.NONE);
		tbtmProgram.setText("Targets / Program");
		
		Composite composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmProgram.setControl(composite_2);
		composite_2.setLayout(new GridLayout(2, false));
		
		final Combo combo1 = new Combo(composite_2, SWT.READ_ONLY);
		combo1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		combo1.add("<disabled>");
		for(int i=0;i<eeg.getNumberOfSignals();i++)
			combo1.add(eeg.getSignalName(i));
		combo1.select(0);
		
		final Canvas canvas1 = new Canvas(composite_2, SWT.BORDER | SWT.H_SCROLL);
		
		canvas1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		final ScrollBar bar1 = canvas1.getHorizontalBar();
		bar1.setMaximum(model.getProgramLength()*model.getProgram(0).SEC_WIDTH_GUI);
		
		combo1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.getProgram(0).setSignalName(combo1.getText());
				
				if(combo1.getText() == "<disabled>")
					canvas1.setEnabled(false);
				else
					canvas1.setEnabled(true);
			}
		});
		
		bar1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				int sel = bar1.getSelection();
				
				if(programCanvas[0] != null){
					GC gc = new GC(canvas1);
					gc.drawImage(programCanvas[0], -sel, 0);
					canvas1.update();
					gc.dispose();
				}
			}
		});
		
		canvas1.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				Image im1 = model.getProgram(0).draw(canvas1);
						
				{
					if(programCanvas[0] != null) programCanvas[0].dispose();
					programCanvas[0] = im1;
							
					int sel = bar1.getSelection();
							
					GC gc = new GC(canvas1);
					gc.drawImage(im1, -sel, 0);
					canvas1.update();
					gc.dispose();
				}			
			}
		});
		
		canvas1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
			
				int second = model.getProgram(0).coordinateToSeconds(canvas1, e.x + bar1.getSelection());
				float value = model.getProgram(0).coordinateToValue(canvas1, e.y);
				
				model.getProgram(0).setProgramValue(second, value);
				
				Image im1 = model.getProgram(0).draw(canvas1);
				
				{
					int sel = bar1.getSelection();
					
					if(programCanvas[0] != null) programCanvas[0].dispose();
					programCanvas[0] = im1;
					
					GC gc = new GC(canvas1);
					gc.drawImage(im1, -sel, 0);
					canvas1.update();
					gc.dispose();
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				
				int second = model.getProgram(0).coordinateToSeconds(canvas1, e.x + bar1.getSelection());
				float value = model.getProgram(0).coordinateToValue(canvas1, e.y);
				
				model.getProgram(0).setProgramValue(second, -1.0f);
				
				Image im1 = model.getProgram(0).draw(canvas1);
				
				{
					int sel = bar1.getSelection();
					
					if(programCanvas[0] != null) programCanvas[0].dispose();
					programCanvas[0] = im1;
					
					GC gc = new GC(canvas1);
					gc.drawImage(im1, -sel, 0);
					canvas1.update();
					gc.dispose();
				}
			}
		});
		
		
		canvas1.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				if(canvas1.getEnabled() == false){
					canvas1.setToolTipText("");
					return;
				}
				
				int second = model.getProgram(0).coordinateToSeconds(canvas1, e.x + bar1.getSelection());
				float value = model.getProgram(0).coordinateToValue(canvas1, e.y);
				
				canvas1.setToolTipText(Integer.toString(second) + ", " + String.format("%.2f", value));
			}
		});
	
		final Combo combo2 = new Combo(composite_2, SWT.READ_ONLY);
		combo2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		combo2.add("<disabled>");
		for(int i=0;i<eeg.getNumberOfSignals();i++)
			combo2.add(eeg.getSignalName(i));
		combo2.select(0);
		
		final Canvas canvas2 = new Canvas(composite_2, SWT.BORDER | SWT.H_SCROLL);
		canvas2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		final ScrollBar bar2 = canvas2.getHorizontalBar();
		bar2.setMaximum(model.getProgramLength()*model.getProgram(1).SEC_WIDTH_GUI);
		
		
		bar2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				int sel = bar2.getSelection();
				
				if(programCanvas[1] != null){
					GC gc = new GC(canvas2);
					gc.drawImage(programCanvas[1], -sel, 0);
					canvas1.update();
					gc.dispose();
				}
			}
		});
		
		
		canvas2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				int second = model.getProgram(1).coordinateToSeconds(canvas2, e.x + bar2.getSelection());
				float value = model.getProgram(1).coordinateToValue(canvas2, e.y);
				
				model.getProgram(1).setProgramValue(second, value);
				
				Image im2 = model.getProgram(1).draw(canvas1);
				
				{
					int sel = bar2.getSelection();
					
					if(programCanvas[1] != null) programCanvas[1].dispose();
					programCanvas[1] = im2;
					
					GC gc = new GC(canvas2);
					gc.drawImage(im2, -sel, 0);
					canvas2.update();
					gc.dispose();
				}
			}
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int second = model.getProgram(1).coordinateToSeconds(canvas2, e.x + bar2.getSelection());
				float value = model.getProgram(1).coordinateToValue(canvas2, e.y);
				
				model.getProgram(1).setProgramValue(second, -1.0f);
				
				Image im2 = model.getProgram(1).draw(canvas2);
				
				{
					int sel = bar2.getSelection();
					
					if(programCanvas[1] != null) programCanvas[1].dispose();
					programCanvas[1] = im2;
					
					GC gc = new GC(canvas2);
					gc.drawImage(im2, -sel, 0);
					canvas2.update();
					gc.dispose();
				}
			}
		});
		
		canvas2.addMouseTrackListener(new MouseTrackAdapter() {
			@Override
			public void mouseHover(MouseEvent e) {
				if(canvas2.getEnabled() == false){
					canvas2.setToolTipText("");
					return;
				}
				
				int second = model.getProgram(1).coordinateToSeconds(canvas2, e.x + bar2.getSelection());
				float value = model.getProgram(1).coordinateToValue(canvas2, e.y);
				
				canvas2.setToolTipText(Integer.toString(second) + ", " + String.format("%.2f", value));
			}
		});
		
		
		
		canvas2.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent arg0) {
				Image im2 = model.getProgram(1).draw(canvas2);
				
				{
					if(programCanvas[1] != null) programCanvas[1].dispose();
					programCanvas[1] = im2;
							
					int sel = bar2.getSelection();
							
					GC gc = new GC(canvas2);
					gc.drawImage(im2, -sel, 0);
					canvas2.update();
					gc.dispose();
				}	
			}
		});
		
		combo2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.getProgram(1).setSignalName(combo2.getText());
				
				if(combo2.getText() == "<disabled>")
					canvas2.setEnabled(false);
				else
					canvas2.setEnabled(true);
			}
		});
		
		Label lblPlayAudio = new Label(composite_2, SWT.NONE);
		lblPlayAudio.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1));
		lblPlayAudio.setText("Play audio");
		
		Composite composite_4 = new Composite(composite_2, SWT.NONE);
		composite_4.setLayout(new GridLayout(2, false));
		composite_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		audioFileText = new Text(composite_4, SWT.BORDER);
		audioFileText.setEnabled(false);
		audioFileText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Button btnAudioFile = new Button(composite_4, SWT.NONE);
		btnAudioFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				/** Audio File Selection */
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setFilterExtensions(new String[] { "*.mp3", "*.ogg" });
				String filename = fd.open();
				
				if(filename != null){
					model.setAudioFile(filename);
					audioFileText.setText(filename);
				}				
			}
		});
		btnAudioFile.setText("Select");
		
		
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Label lblNewLabel_2 = new Label(composite_3, SWT.NONE);
		lblNewLabel_2.setText("Length");
		
		final Spinner spinner = new Spinner(composite_3, SWT.BORDER);
		spinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int value = Integer.parseInt(spinner.getText());
				
				model.setProgramLength(value);
				bar1.setMaximum(model.getProgramLength()*model.getProgram(0).SEC_WIDTH_GUI);
				bar2.setMaximum(model.getProgramLength()*model.getProgram(1).SEC_WIDTH_GUI);
				
				Image im1 = model.getProgram(0).draw(canvas1);
				Image im2 = model.getProgram(1).draw(canvas2);
				
				{
					if(programCanvas[0] != null) programCanvas[0].dispose();
					programCanvas[0] = im1;
					
					int sel = bar1.getSelection();
					
					GC gc = new GC(canvas1);
					gc.drawImage(im1, -sel, 0);
					canvas1.update();
					gc.dispose();
				}
				
				{
					if(programCanvas[1] != null) programCanvas[1].dispose();
					programCanvas[1] = im2;
					
					int sel = bar2.getSelection();
					
					GC gc = new GC(canvas2);
					gc.drawImage(im2, -sel, 0);
					canvas2.update();
					gc.dispose();
				}
				
			}
		});
		spinner.setSelection(model.getProgramLength());
		spinner.setMaximum(1000);
		spinner.setMinimum(1);
		
		Label lblSecs = new Label(composite_3, SWT.NONE);
		lblSecs.setText("secs");
		
		Button btnExecute = new Button(composite_2, SWT.NONE);
		btnExecute.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				String[] targets = new String[2];
				targets[0] = model.getProgram(0).getSignalName();
				targets[1] = model.getProgram(1).getSignalName();
				
				float[][] programs = new float[2][];
				
				programs[0] = model.getProgram(0).getProgram();
				programs[1] = model.getProgram(1).getProgram();
				
				boolean blindMonteCarlo = model.getBlindMonteCarloMode();
				
				engine.startExecuteProgram(
						model.getPictureDirectory(),
						model.getKeywordsFile(),
						model.getModelDirectory(),
						model.getAudioFile(),
						targets, programs,
						blindMonteCarlo);
			}
		});
		btnExecute.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecute.setText("Execute program");
		
		statusLine = new Text(shell, SWT.BORDER);
		statusLine.setEditable(false);
		statusLine.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				
		mntmNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setProgramLength(60);
				model.getProgram(0).resizeProgram(0);
				model.getProgram(0).resizeProgram(60);
				model.getProgram(1).resizeProgram(0);
				model.getProgram(1).resizeProgram(60);
				
				model.getProgram(0).setProgramValue(0, 1.0f);
				model.getProgram(0).setProgramValue(59, 1.0f);
				model.getProgram(1).setProgramValue(0, 1.0f);
				model.getProgram(1).setProgramValue(59, 1.0f);
				
				// updates view
				spinner.setSelection(60);
				
				canvas1.redraw();
				canvas2.redraw();
				
				combo1.select(0);
				combo2.select(0);
			}
		});
		
		
		mntmOpenPreset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open stimulation program..");
				fd.setFilterExtensions(new String[] { "*.nmc", "*.*" });
				String filename = fd.open();
				
				if(filename != null)
					if(model.loadProgramPreset(filename))
						System.out.println("Loading program: " + filename);
				
				// updates view
				spinner.setSelection(model.getProgramLength());
				canvas1.redraw();
				canvas2.redraw();
				combo1.select(eeg.getSignalNameNumber(model.getProgram(0).getSignalName()) + 1);
				combo2.select(eeg.getSignalNameNumber(model.getProgram(1).getSignalName()) + 1);
			}
		});
		
	}
}
