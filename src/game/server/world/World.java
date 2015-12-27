package game.server.world;

public class World {
	private int width, height;
	private int[] tiles;

	public World(int width, int height) {
		this.width = width;
		this.height = height;
		
		this.tiles = new int[width * height];
	}
	
	public Tile getTile(int xPos, int yPos){
		if(xPos<0 || xPos > width || yPos < 0 || yPos > height){
			return null;
		} else {
			return Tile.tiles[xPos+yPos*width];
		}
	}
	
	public Tile getTileByID(int id){
		return Tile.tiles[id];
	}
	
	public void setTile(int xPos, int yPos, TileType type){
		if(xPos>=0 || xPos < width || yPos >= 0 || yPos < height){
			tiles[xPos + yPos * width] = type.getId();
		}
	}
	
	public void setTiles(int[] tiles){
		this.tiles = tiles;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int[] getTiles() {
		return tiles;
	}
}
