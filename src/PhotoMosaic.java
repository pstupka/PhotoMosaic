import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PhotoMosaic implements ActionListener, ChangeListener, ComponentListener{
	
	/**
	 * GUI elements
	 */
	private JFrame frame;
	private JButton runButton;
	private JTabbedPane imageTabbedPane;
	private JSpinner xTileSpinner, yTileSpinner, widthTileSpinner, heightTileSpinner;
	private JPanel baseImagePanel, mapImagePanel, finalImagePanel;
	private JProgressBar progressBar;
	String imageWidthString = "Base image width: ";
	String imageHeightString = "Base image height: ";
	String imagesNumberInLibraryString = "Images in Library:  ";
	private JLabel imageWidthLabel, imageHeightLabel, imagesNumberInLibraryLabel;

	JLabel baseImageLabel, mapImageLabel, finalImageLabel;
	
	/*
	 * Threading sync variables 
	 */
	private boolean isImportingTiles = false;
	
	/**
	 * Render components
	 */
	TilesManager tilesManager;
	MosaicRender mosaicRenderer;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PhotoMosaic window = new PhotoMosaic();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Progress Listener class updates progress bar of the application
	 */
	class ProgressListener implements ValueListener{
		public void update(int value){
			progressBar.setValue(value);
			updateUI();
		}
	}
	
	/**
	 * Create the application.
	 */
	public PhotoMosaic() {
		imageWidthLabel = new JLabel(imageWidthString);
		imageHeightLabel = new JLabel(imageHeightString);
		imagesNumberInLibraryLabel = new JLabel(imagesNumberInLibraryString);

		ProgressListener progressListener = new ProgressListener();
		
		tilesManager = new TilesManager(progressListener);
		mosaicRenderer = new MosaicRender(tilesManager, progressListener);
		
		initializeGUI();
		updateUI();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initializeGUI() {
		frame = new JFrame("Photo Mosaic");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		imageTabbedPane = new JTabbedPane();
		baseImagePanel = new JPanel();
		baseImagePanel.setPreferredSize(new Dimension(350, 200));
		imageTabbedPane.addTab("Base Image",baseImagePanel);
		mapImagePanel = new JPanel();
		mapImagePanel.setPreferredSize(new Dimension(350, 200));
		imageTabbedPane.addTab("Map Image",mapImagePanel);
		finalImagePanel = new JPanel();
		finalImagePanel.setPreferredSize(new Dimension(350, 200));
		imageTabbedPane.addTab("Final Image",finalImagePanel);
		imageTabbedPane.addChangeListener(this);
		
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		frame.getContentPane().add(progressBar, BorderLayout.PAGE_END);
		
		JPanel optionsPanel = new JPanel(new BorderLayout());
        JPanel settingPanel = new JPanel(new SpringLayout());
        settingPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
        
        xTileSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
        JLabel label1 = new JLabel("X tiles");
        label1.setLabelFor(xTileSpinner);
        xTileSpinner.addChangeListener(this);
        settingPanel.add(label1);
        settingPanel.add(xTileSpinner);
        
        yTileSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
        JLabel label2 = new JLabel("Y tiles");
        label1.setLabelFor(yTileSpinner);
        yTileSpinner.addChangeListener(this);
        settingPanel.add(label2);
        settingPanel.add(yTileSpinner);
        
        widthTileSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
        JLabel label3 = new JLabel("Tile width");
        label1.setLabelFor(widthTileSpinner);
        widthTileSpinner.addChangeListener(this);
        settingPanel.add(label3);
        settingPanel.add(widthTileSpinner);
        
        heightTileSpinner = new JSpinner(new SpinnerNumberModel(20, 1, 1000, 1));
        JLabel label4 = new JLabel("Tile height");
        label1.setLabelFor(heightTileSpinner);
        heightTileSpinner.addChangeListener(this);
        settingPanel.add(label4);
        settingPanel.add(heightTileSpinner);
        
        SpringUtilities.makeCompactGrid(settingPanel,
                4, 2, 			//rows, cols
                6, 6,        	//initX, initY
                6, 6);       	//xPad, yPad
                
        optionsPanel.add(settingPanel, BorderLayout.PAGE_START);

        runButton = new JButton("Run");
        runButton.setActionCommand("RunAction");
        runButton.addActionListener(this);
        optionsPanel.add(runButton, BorderLayout.PAGE_END);

		frame.getContentPane().add(optionsPanel, BorderLayout.LINE_START);
		
		JPanel infoPanel = new JPanel();
		infoPanel.setPreferredSize(new Dimension(200, 400));
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setBorder(BorderFactory.createTitledBorder("Image info"));
		infoPanel.add(imageWidthLabel);
		infoPanel.add(imageHeightLabel);
		infoPanel.add(imagesNumberInLibraryLabel);
		frame.getContentPane().add(infoPanel, BorderLayout.LINE_END);

		JMenuBar mainMenuBar = new JMenuBar();
		JMenu mainMenu = new JMenu("Main menu");
		JMenuItem getTilesMenuItem = new JMenuItem("Get Tiles");
		getTilesMenuItem.setActionCommand("getTilesAction");
		getTilesMenuItem.addActionListener(this);
		
		JMenuItem getBaseImageMenuItem = new JMenuItem("Get Base Image");
		getBaseImageMenuItem.setActionCommand("getBaseImageAction");
		getBaseImageMenuItem.addActionListener(this);
			
		JMenu exportMenu = new JMenu("Export");
		JMenuItem exportMenuItem = new JMenuItem("Export final image");
		exportMenuItem.setActionCommand("exportAction");
		exportMenuItem.addActionListener(this);
		
		exportMenu.add(exportMenuItem);
		mainMenu.add(getTilesMenuItem);
		mainMenu.add(getBaseImageMenuItem);
		mainMenuBar.add(mainMenu);
		mainMenuBar.add(exportMenu);
		frame.setJMenuBar(mainMenuBar);
		
		baseImageLabel = new JLabel();
		baseImagePanel.add(baseImageLabel);
		mapImageLabel = new JLabel();
		mapImagePanel.add(mapImageLabel);

		finalImageLabel = new JLabel();
		finalImagePanel.add(finalImageLabel);

		frame.getContentPane().add(imageTabbedPane, BorderLayout.CENTER);
		frame.addComponentListener(this);
		frame.pack();
	}
	
	private void updateUI(){
		switch (imageTabbedPane.getSelectedIndex()){
			case 0: 		
				baseImageLabel.setIcon(new ImageIcon(
					mosaicRenderer.getBaseImage().getScaledInstance(
							baseImagePanel.getWidth(),
							baseImagePanel.getHeight(),
							Image.SCALE_SMOOTH)));
				imageWidthLabel.setText(imageWidthString+mosaicRenderer.getBaseImage().getWidth());
				imageHeightLabel.setText(imageHeightString+mosaicRenderer.getBaseImage().getHeight());
				break;
			case 1: 		
				mapImageLabel.setIcon(new ImageIcon(
					mosaicRenderer.getMapImage().getScaledInstance(
							mapImagePanel.getWidth(),
							mapImagePanel.getHeight(), Image.SCALE_SMOOTH)));
				break;
			case 2: 		
				finalImageLabel.setIcon(new ImageIcon(
					mosaicRenderer.getFinalImage().getScaledInstance(
							finalImagePanel.getWidth(),
							finalImagePanel.getHeight(), Image.SCALE_SMOOTH)));
				break;
			default: break;
		}
		imagesNumberInLibraryLabel.setText(imagesNumberInLibraryString+tilesManager.getTilesNumber());
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("exportAction")){
			mosaicRenderer.exportFinalImagetoFile();
		}
		if (actionCommand.equals("getBaseImageAction")){
			mosaicRenderer.setBaseImage();
			updateUI();
		}
		if (actionCommand.equals("getTilesAction")){
			new Thread(new Runnable(){

				@Override
				public void run() {
					isImportingTiles = true;
					tilesManager.importTiles();
					isImportingTiles = false;
				}
				
			}).start();
		}
		if (actionCommand.equals("RunAction")){		
			if (tilesManager.getTilesNumber() > 0 & isImportingTiles == false){
				new Thread(new Runnable(){

					@Override
					public void run() {
						mosaicRenderer.createMosaic();
					}
					
				}).start();
			} else {
				System.out.println("No tiles in library or importer is running");
			}
		}
	
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		mosaicRenderer.updateMosaicParameters((int)xTileSpinner.getValue(),
				(int)yTileSpinner.getValue(),
				(int)widthTileSpinner.getValue(),
				(int)heightTileSpinner.getValue());
		updateUI();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		Dimension currentFrameSize = e.getComponent().getSize();
		if ((currentFrameSize.getHeight() % 10) == 0 | (currentFrameSize.getWidth() % 10) == 0 ){
			updateUI();
		}
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

}     