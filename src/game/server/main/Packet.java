package game.server.main;

import java.awt.Color;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

import operator.Vector2D;

public class Packet {
	public static class PacketRegister{ public String username; public String password; public boolean accepted = false; }
	public static class Packet0LoginRequest{ }
	public static class Packet1LoginAnswer{ public boolean accepted = false;}
	public static class Packet2Message{ public String message; }
	public static class Packet3Disconnect{ public String name; }
	public static class MovePacket{ public Vector2D dir; public String username;}
	public static class MovePacketServer{ public Dictionary<String, Vector2D> positions = new Hashtable<String, Vector2D>(); public String username;}
	public static class NewPlayerPacket{ public Dictionary<String, Vector2D> positions = new Hashtable<String, Vector2D>(); 
			public CopyOnWriteArrayList<String> players = new CopyOnWriteArrayList<String>(); public String username; public Vector2D pos; }
	public static class LogOffPacket{ public String username; }
	public static class WorldTilesPacket{ public int[] tiles; public int[] props; }
	public static class ChatPacket{ public String chat;}
	public static class ColorPacketChar{ public Dictionary<String, Integer> character = new Hashtable<String, Integer>(); public String username; public Integer selected;}
}
