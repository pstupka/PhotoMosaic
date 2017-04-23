import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tile { 
	
	private BufferedImage tileImage;
	private double someCoolParameter = 0;
	
	public Tile(){
	}
	
	public Tile(String name){
		setTileImage(name);
	}
	
	public Tile(BufferedImage image){
		this.tileImage = image;
	}

	public BufferedImage getTileImage() {
		return tileImage;
	}

	public void setTileImage(BufferedImage tileImage) {
		this.tileImage = tileImage;
	}
	
	public void setTileImage(String name){
        try {
			tileImage = ImageIO.read(new File(name));
		} catch (IOException e) {
			tileImage = new BufferedImage(0, 0, 0);
			e.printStackTrace();
		}
	}
	

}
