package game.server.world;

public abstract class Tile {
	public static Tile[] tiles = new Tile[60];
	
	private int id;
	
	private TileType type;
	private boolean solid;
	
	public Tile(TileType type){
		this.type = type;
		id = type.getId();
		solid = type.isSolid();
		tiles[id] = this;
	}
	
	public boolean isSolid(World world, int xPos, int yPos){
		return true;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isSolid(){
		return solid;
	}
	
	public TileType getType() {
		return type;
	}
}
