import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

public class MosaicRender implements Runnable {
	private BufferedImage baseImageBuffer;
	private BufferedImage mapImageBuffer;
	private BufferedImage finalImageBuffer;
	
	private TilesManager mapTileManager;
	
	private int xTiles, yTiles, widthTile, heightTile;
	
	public MosaicRender() {
		baseImageBuffer = new Tile("assets/corgi.png").getTileImage();
		mapImageBuffer = new Tile("assets/corgi.png").getTileImage();
		finalImageBuffer = new Tile("assets/corgi.png").getTileImage();
				
		xTiles = 20;
		yTiles = 20;
		widthTile = 20;
		heightTile = 20;
		
		mapTileManager = new TilesManager();
	
		
		prepareMapImage();
	}
	
	@Override
	public void run() {
		System.out.println("hello");
	}
	
	public void prepareMapImage(){
		Image mapBuffer = baseImageBuffer.getScaledInstance(xTiles, yTiles, Image.SCALE_DEFAULT);
	    BufferedImage resizedMap = new BufferedImage(xTiles, yTiles, BufferedImage.TYPE_INT_ARGB); 
	    // Draw the image on to the buffered image
	    Graphics2D bGr = resizedMap.createGraphics();
	    bGr.drawImage(mapBuffer, 0, 0, null);
	    bGr.dispose();
	    
	   	mapImageBuffer = resizedMap;
	    
	}
	
	public void setBaseImage() {
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		int retrieve = chooser.showOpenDialog(null);
        if (retrieve == JFileChooser.APPROVE_OPTION){
			try {
				baseImageBuffer = ImageIO.read(chooser.getSelectedFile());
				prepareMapImage();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public BufferedImage getBaseImage() {
		return baseImageBuffer;
	}

	public BufferedImage getMapImage() {
		return mapImageBuffer;
	}
	
	public BufferedImage getFinalImage(){
		return finalImageBuffer;
	}
	
	public void exportFinalImagetoFile(){
		JFileChooser chooser = new JFileChooser();
		int retrieve = chooser.showSaveDialog(null);
		if (retrieve == JFileChooser.APPROVE_OPTION){
			try {
				ImageIO.write(finalImageBuffer, "png", chooser.getSelectedFile());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void updateMosaicParameters(int xTiles, int yTiles, int widthTile, int heightTile){
		this.xTiles = xTiles;
		this.yTiles = yTiles;
		this.widthTile = widthTile;
		this.heightTile = heightTile;
		prepareMapImage();
	}
}
