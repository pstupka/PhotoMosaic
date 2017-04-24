import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile { 
	
	private BufferedImage tileImage;
	private Color pixelColor = new Color(0);
	
	public Tile(){
	}
	
	public Tile(String name){
		setTileImage(name);
		setDefaultPixelColor();
	}
	
	public Tile(BufferedImage image){
		this.tileImage = image;
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
			setDefaultPixelColor();
		} catch (IOException e) {	   
			this.tileImage = new BufferedImage(0, 0, 0);
			e.printStackTrace();
		}
	}
	
	private void setDefaultPixelColor(){
		Image scaledImage = tileImage.getScaledInstance(1, 1, Image.SCALE_SMOOTH);
		BufferedImage bufferedScaledImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
	    Graphics2D bGr = bufferedScaledImage.createGraphics();
	    bGr.drawImage(scaledImage, 0, 0, null);
	    bGr.dispose();
	    
	    this.pixelColor = new Color(bufferedScaledImage.getRGB(0,0));
	}
	
	public void setPixelColor(Color color){
		this.pixelColor = color;
	}
	
	public Color getPixelcolor(){
		return pixelColor;
	}
	
	

}
