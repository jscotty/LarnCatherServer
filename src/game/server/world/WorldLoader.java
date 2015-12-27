package game.server.world;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class WorldLoader {
	
	private static Dictionary<Integer, Integer> worldDictionary = new Hashtable<Integer, Integer>();
	private static Dictionary<Integer, Integer> worldPropsDictionary = new Hashtable<Integer, Integer>();

/// get world data from an image
	public static World getWorldFromImage(String path){
		URL url = WorldLoader.class.getResource(path); // get path url

		worldDictionary.put(0x00BC16, 0); // grass
		worldDictionary.put(0x000016, 1); // wall
		
		BufferedImage image = null; // premade image
		try {
			
			image = ImageIO.read(url); // creating image
			
			int width = image.getWidth();
			int height = image.getHeight();
			
			int[] tiles = new int[width * height];
			
			World world = new World(width, height); // creating the world
			
			
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int col = image.getRGB(x, y) & 0xFFFFFF; // get image RGB data
					int t = 0;
					if(worldDictionary.get(col) != null){
						t = worldDictionary.get(col); // get tile id from dictionary
					}
					
					tiles[x + y * width] = t;
				}
			}
			
			world.setTiles(tiles);
			
			return world;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	/// get world data from an image
		public static World getPropsFromImage(String path){
			URL url = WorldLoader.class.getResource(path); // get path url

			worldPropsDictionary.put(0x404040, 0); // grass
			worldPropsDictionary.put(0x808080, 1); // grass_02
			worldPropsDictionary.put(0x00ffff, 2); // water
			
			BufferedImage image = null; // premade image
			try {
				
				image = ImageIO.read(url); // creating image
				
				int width = image.getWidth();
				int height = image.getHeight();
				
				int[] props = new int[width * height];
				
				World world = new World(width, height); // creating the world
				
				
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						int col = image.getRGB(x, y) & 0xFFFFFF; // get image RGB data
						
						int t = -1; 
						if(worldPropsDictionary.get(col) != null){
							t = worldPropsDictionary.get(col);// get tile id from dictionary
						}
						props[x + y * width] = t;
					}
				}
				
				world.setTiles(props);
				
				return world;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
	
	public WorldLoader() {
		
	}

}
