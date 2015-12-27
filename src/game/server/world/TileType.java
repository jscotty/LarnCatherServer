package game.server.world;

public enum TileType {
	GRASS(0,false),
	WALL(1,true);
	
	private int id;
	private boolean solid;
	
	private TileType(int id,boolean isSolid){
		this.id = id;
		this.solid = isSolid;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isSolid(){
		return solid;
	}
}
