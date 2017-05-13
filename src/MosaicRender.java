import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

public class MosaicRender {	
	
	ValueListener progressListener;
	
	private BufferedImage baseImageBuffer;
	private BufferedImage mapImageBuffer;
	private BufferedImage finalImageBuffer;
	
	private int xTiles, yTiles, widthTile, heightTile;
	
	private TilesManager tilesManager;
	
	public MosaicRender(TilesManager manager, ValueListener progressListener) {
		baseImageBuffer = new Tile("assets/corgi.png").getTileImage();
		mapImageBuffer = new Tile("assets/corgi.png").getTileImage();
		finalImageBuffer = new Tile("assets/corgi.png").getTileImage();
		
		this.progressListener = progressListener;
				
		xTiles = 20;
		yTiles = 20;
		widthTile = 20;
		heightTile = 20;
		
		this.tilesManager = manager;
				
		prepareMapImage();
	}
	
	public void createMosaic() {
		int maxTiles = tilesManager.getTilesNumber();
		int matchTileID = 0;
		double minDifference = 500;

		this.finalImageBuffer = new BufferedImage(xTiles*widthTile,
				yTiles*heightTile,
				BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = finalImageBuffer.createGraphics();
	    
	    tilesManager.resetTilesUsage();
	    
	    ArrayList<Integer> randomIndexList = new ArrayList<>();
	    for (int i = 0; i < xTiles*yTiles; i++) {
	    	randomIndexList.add(i);
	    }
	    	    
	    Collections.shuffle(randomIndexList);
	    
		for (int i = 0; i < xTiles*yTiles; i++){
			int progress = 100*(i+1)/(xTiles*yTiles);
			if (progress%1 == 0) progressListener.update(progress);
			
			matchTileID = 0;
			minDifference = 500;
			tilesManager.shuffle();
			int x = randomIndexList.get(i)%xTiles;
			int y = randomIndexList.get(i)/xTiles;
			Color c = new Color(mapImageBuffer.getRGB(x,y));
			
			for(int j = 0; j < maxTiles; j++){
				Tile currentTile = tilesManager.getTile(j);
				if (currentTile.isUsed() > 9) System.out.println(currentTile.isUsed());
				if (currentTile.isUsed() < 10){
					int redDiff = Math.abs(c.getRed() - currentTile.getPixelcolor().getRed());
					int greenDiff = Math.abs(c.getGreen() - currentTile.getPixelcolor().getGreen());
					int blueDiff = Math.abs(c.getBlue() - currentTile.getPixelcolor().getBlue());
					double diff = Math.sqrt(redDiff*redDiff + greenDiff*greenDiff + blueDiff*blueDiff);

					if (diff < minDifference){
						matchTileID = j;
						minDifference = diff;
						if (minDifference < 20) break;
					}
				}
				
			}
			tilesManager.getTile(matchTileID).setUsed();
			bGr.drawImage(
					tilesManager.getTile(matchTileID).getTileImage().getScaledInstance(widthTile,
							heightTile,
							Image.SCALE_SMOOTH),
					x*widthTile,
					y*heightTile,
					widthTile,
					heightTile,
					null);
		}
	    bGr.dispose();
	}
	
	public void prepareMapImage(){
		Image mapBuffer = baseImageBuffer.getScaledInstance(xTiles, yTiles, Image.SCALE_SMOOTH);
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
