import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile { 
	
	private BufferedImage tileImage;
	private LabColor pixelColor;
	private int isUsed = 0;
	
	
	public Tile(){
	}
	
	public Tile(String name){
		setTileImage(name);
	}
	
	public Tile(BufferedImage image){
		this.tileImage = image;
		setTileImage(image);
	}

	public BufferedImage getTileImage() {
		return tileImage;
	}

	public void setTileImage(BufferedImage tileImage) {
		this.tileImage = tileImage;
		setDefaultPixelColor();
	}
	
	public void setTileImage(String name){
        try {
			this.tileImage = ImageIO.read(new File(name));
		} catch (IOException e) {	   
			this.tileImage = new BufferedImage(0, 0, 0);		
			e.printStackTrace();
		}
        setDefaultPixelColor();
	}
	
	private void setDefaultPixelColor(){
		Image scaledImage = tileImage.getScaledInstance(1, 1, Image.SCALE_AREA_AVERAGING);
		BufferedImage bufferedScaledImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = bufferedScaledImage.createGraphics();
	    bGr.drawImage(scaledImage, 0, 0, null);
	    bGr.dispose();
	    
	    Color tmpColor = new Color(bufferedScaledImage.getRGB(0, 0));
	    this.pixelColor = new LabColor(tmpColor.getRed(), tmpColor.getGreen(), tmpColor.getBlue());
	}
	
	public LabColor getPixelcolor(){
		return pixelColor;
	}

	public void setUsed(){
		this.isUsed++;
	}
	
	public int isUsed(){
		return this.isUsed;
	}
	
	public void resetUsage(){
		this.isUsed = 0;
	}
	
}
