import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;

public class TilesManager {

	private ArrayList<Tile> tiles = new ArrayList<Tile>();
	
	public Tile getTile(int i){
		return tiles.get(i);
	}
	
	public void clearTiles(){
		tiles.clear();
	}
	
	public int getTilesNumber(){
		return tiles.size();
	}
	
	public void addTile(Tile t){
		tiles.add(t);
	}
	
	public void importTiles(){
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);

		// Show the dialog; wait until dialog is closed
		int retrieve = chooser.showOpenDialog(null);
		
		if (retrieve == JFileChooser.APPROVE_OPTION){
			// Retrieve the selected files.
			File[] files = chooser.getSelectedFiles();
			clearTiles();
			for (File file : files) {
				addTile(new Tile(file.getPath()));
			}
		}
	}
}
