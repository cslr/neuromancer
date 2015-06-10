package fi.iki.nop.neuromancer;


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



public class NeuromancerWindow {
	
	protected static NeuromancerModel model;
	protected static NeuromancerWindow window;
	protected static SignalSource eeg;
	
	protected Shell shell;
	protected Display display;
	
	private Text text;
	private Text text_1;
	private Text text_2;
	
	private final Image[] programCanvas = new Image[2];

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			eeg    = new EmotivInsightSignalSourceStub();
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
		
		// creates updater thread that updates canvas periodically
		/*
		Thread updaterThread = new Thread(){
			public void run(){
				while(true){
					
					Display.getDefault().syncExec(new Runnable(){
						public void run(){
							
						}
					});
					
					try{ Thread.sleep(1000); }
					catch(InterruptedException e){ }
				}
			}
		};
		*/
		
		
		
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
		
		shell.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		
		MenuItem mntmFile = new MenuItem(menu, SWT.CASCADE);
		mntmFile.setText("File");
		
		Menu menu_1 = new Menu(mntmFile);
		mntmFile.setMenu(menu_1);
		
		MenuItem mntmNew = new MenuItem(menu_1, SWT.NONE);
		mntmNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Initializing new program");
			}
		});
		mntmNew.setText("New");
		
		MenuItem mntmOpenPreset = new MenuItem(menu_1, SWT.NONE);
		mntmOpenPreset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.OPEN);
				fd.setText("Open stimulation program..");
				fd.setFilterExtensions(new String[] { "*.nmc", "*.*" });
				String filename = fd.open();
				
				System.out.println("Loading program: " + filename);
			}
		});
		mntmOpenPreset.setText("Open program..");
		
		MenuItem mntmSaveProgram = new MenuItem(menu_1, SWT.NONE);
		mntmSaveProgram.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog fd = new FileDialog(shell, SWT.SAVE);
				fd.setText("Save stimulation program..");
				fd.setFilterExtensions(new String[] { "*.nmc", "*.*" });
				String filename = fd.open();
				
				System.out.println("Saving program: " + filename);
			}
		});
		mntmSaveProgram.setText("Save program..");
		
		new MenuItem(menu_1, SWT.SEPARATOR);
		
		MenuItem mntmResetDatabase = new MenuItem(menu_1, SWT.NONE);
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
		
		MenuItem mntmHelp = new MenuItem(menu, SWT.CASCADE);
		mntmHelp.setText("Help");
		
		Menu menu_3 = new Menu(mntmHelp);
		mntmHelp.setMenu(menu_3);
		
		MenuItem mntmAbout = new MenuItem(menu_3, SWT.NONE);
		mntmAbout.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox mbox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
				mbox.setText("About Neuromancer..");
				mbox.setMessage("Neuromancer " + model.getVersion() + "\n(C) Copyright Tomas Ukkonen 2015\ntomas.ukkonen@iki.fi\n\nThis software requires Emotiv Insight device.");
				mbox.open();
			}
		});
		mntmAbout.setText("About");
		
		TabFolder tabFolder = new TabFolder(shell, SWT.NONE);
		
		TabItem tbtmDatabase = new TabItem(tabFolder, SWT.NONE);
		tbtmDatabase.setText("Learn/Database");
		
		Composite composite = new Composite(tabFolder, SWT.NONE);
		tbtmDatabase.setControl(composite);
		composite.setLayout(new GridLayout(3, false));
		
		Label lblDatabaseDirectory = new Label(composite, SWT.NONE);
		lblDatabaseDirectory.setBounds(0, 0, 55, 15);
		lblDatabaseDirectory.setText("Database directory: ");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text_2.setBounds(0, 0, 76, 21);
		
		Button btnSelect = new Button(composite, SWT.NONE);
		btnSelect.setBounds(0, 0, 75, 25);
		btnSelect.setText("Select");
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setBounds(0, 0, 55, 15);
		lblNewLabel.setText("Picture directory: ");
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		text.setBounds(0, 0, 76, 21);
		
		Button btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setBounds(0, 0, 75, 25);
		btnNewButton.setText("Select");
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setBounds(0, 0, 55, 15);
		lblNewLabel_1.setText("Keywords file:");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		text_1.setBounds(0, 0, 76, 21);
		
		Button btnNewButton_1 = new Button(composite, SWT.NONE);
		btnNewButton_1.setText("Select");
		new Label(composite, SWT.NONE);
		new Label(composite, SWT.NONE);
		
		Composite composite_1 = new Composite(composite, SWT.NONE);
		composite_1.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 1));
		composite_1.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button btnRandom = new Button(composite_1, SWT.NONE);
		btnRandom.setText("Random");
		
		Button btnLearn = new Button(composite_1, SWT.NONE);
		btnLearn.setText("Learn");

		TabItem tbtmProgram = new TabItem(tabFolder, SWT.NONE);
		tbtmProgram.setText("Program");
		
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
		
		final ScrollBar bar2 = canvas2.getHorizontalBar();
		bar2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				int sel = bar2.getSelection();
				
				if(programCanvas[1] != null){
					GC gc = new GC(canvas2);
					gc.drawImage(programCanvas[1], -sel, 0);
					canvas2.update();
					gc.dispose();
				}
				
				
			}
		});
		
		
		Composite composite_3 = new Composite(composite_2, SWT.NONE);
		composite_3.setBounds(0, 0, 64, 64);
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
		btnExecute.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnExecute.setText("Execute");
		
	}
}
