import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFileChooser;


public class TilesManager {

	private ArrayList<Tile> tiles = new ArrayList<Tile>();
	
	ValueListener progressListener;

	public TilesManager(ValueListener progressListener) {
		this.progressListener = progressListener;
	}
	
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
	
	
	/**
	 * Imports image tiles from JFileChooser
	 * 
	 */
	
	public void importTiles(){
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		
		// Show the dialog; wait until dialog is closed
		int retrieve = chooser.showOpenDialog(null);
		
		if (retrieve == JFileChooser.APPROVE_OPTION){
			// Retrieve the selected files.
			File[] files = chooser.getSelectedFiles();
			clearTiles();
		
		// TODO needs to make condition for incorrect selected files (not images)
			for (int i = 0; i < files.length; i++) {
				int progress = (100*(i+1)/files.length);
				if (progress%1 == 0){
					progressListener.update(progress);
				}
				addTile(new Tile(files[i].getPath()));
			}
		}
	}
	
	public void shuffle(){
		Collections.shuffle(tiles); 
	}
	
	public void resetTilesUsage(){
		for (Tile t : tiles){
			t.resetUsage();
		}
	}

}
