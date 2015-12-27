package game.server.listeners;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

import operator.Vector2D;
import game.server.main.MainServer;
import game.server.main.PlayerList;
import game.server.main.Packet.*;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class NetworkListenerServer extends Listener {
	
	private File loginFile;
	private Dictionary<String, Vector2D> positions = new Hashtable<String, Vector2D>();
	private CopyOnWriteArrayList<String> chat = new CopyOnWriteArrayList<String>();
	private CopyOnWriteArrayList<String> players = new CopyOnWriteArrayList<String>();
	private Server server;
	private int xPos, yPos;
	private PlayerList pl;
	private MainServer main;
	static StringBuffer stringBufferOfData = new StringBuffer();
	
	public NetworkListenerServer(Server server, PlayerList pl, MainServer main) {
		this.server = server;
		this.pl = pl;
		this.main = main;
	}
	
	@Override
	public void connected(Connection con) {
		MainServer.logger.info(" Someone has connected!");
		
	}
	@Override
	public void disconnected(Connection con) {
		
	}
	
	@Override
	public void received(Connection con, Object obj) {
		if(obj instanceof Packet0LoginRequest){
			Packet1LoginAnswer loginAnswer = new Packet1LoginAnswer();
			loginAnswer.accepted = true;
			con.sendTCP(loginAnswer);
			
		} else if(obj instanceof Packet3Disconnect){
			String name = ((Packet3Disconnect)obj).name;
			for (String player : players) {
				System.out.println(name);
				if(player.equals(name)){
					players.remove(player);
				}
			}
		} else if(obj instanceof PacketRegister){
			loginFile = null;
			PacketRegister registerPacket = ((PacketRegister) obj);
			PacketRegister regPack = new PacketRegister();
			regPack.username = registerPacket.username;
			regPack.password = registerPacket.password;
			
			players.add(registerPacket.username);
			
			regPack = regPacket(regPack, con);
			positions.put(regPack.username, new Vector2D(xPos, yPos));
			
			NewPlayerPacket npp = new NewPlayerPacket();
			npp.pos = new Vector2D(xPos, yPos);
			npp.username = regPack.username;
			npp.positions = positions;
			npp.players = players;
			
			WorldTilesPacket wtp = new WorldTilesPacket();
			wtp.tiles = main.getWorld().getTiles();
			wtp.props = main.getProps().getTiles();
			
			pl.addPlayer(players);
			
			ChatPacket cp = new ChatPacket();
			cp.chat = ""+npp.username+" has logged in!";
			
			ChatPacket cpOnce = new ChatPacket();
			cpOnce.chat = "@blue@Welcome to larn catchers! ";
			con.sendTCP(cpOnce);

			con.sendTCP(wtp);
			con.sendTCP(regPack);
			server.sendToAllTCP(npp);
			server.sendToAllTCP(cp);
			
			MainServer.logger.info( " " + regPack.username +" has logged in!");
		} else if(obj instanceof MovePacket){
			MovePacket mp = (MovePacket)obj;
			positions.put(mp.username, mp.dir);
			
			MovePacketServer mps = new MovePacketServer();
			mps.username = mp.username;
			mps.positions = positions;
			
			server.sendToAllTCP(mps);
		} else if(obj instanceof LogOffPacket){
			String logOffUser = ((LogOffPacket)obj).username;
			ChatPacket cp = new ChatPacket();
			cp.chat = logOffUser+" has logged off!";
			server.sendToAllTCP(cp);
			updatePlayerData(logOffUser);
			
			for (String player : players) {
				if(player.equals(logOffUser)){
					players.remove(player);
				}
			}
			
			pl.addPlayer(players);
			
			LogOffPacket lp = new LogOffPacket();
			lp.username = logOffUser;
			
			server.sendToAllTCP(lp);
			
			MainServer.logger.info( " " + logOffUser +" has logged out!");
		} else if(obj instanceof ChatPacket){
			ChatPacket cp = (ChatPacket)obj;
			chat.add(cp.chat);
			MainServer.logger.info("Message: " + cp.chat);
			
			ChatPacket cpSend = new ChatPacket();
			cpSend.chat = cp.chat;
			server.sendToAllTCP(cpSend);
		} else if(obj instanceof ColorPacketChar){
			ColorPacketChar cpc = (ColorPacketChar)obj;
			
			ColorPacketChar cpcSend = new ColorPacketChar();
			cpcSend.character = cpc.character;
			cpcSend.selected = cpc.selected;
			cpcSend.username = cpc.username;
			server.sendToAllTCP(cpcSend);
		}
	}
	
	private PacketRegister regPacket(PacketRegister loginPacket, Connection con){
		xPos = 32*2; yPos = 32*4;
		loginFile = new File("characterdata/"+loginPacket.username + ".txt");
		
		if(loginFile.exists()){
			try {
				@SuppressWarnings("resource")
				BufferedReader br = new BufferedReader(new FileReader(loginFile));
				
				for(String s = ""; (s=br.readLine()) != null;){
					String split[] = s.split(":");
					
					if(split[0].equals("password")){
						String password = split[1];
						password = password.trim();
						if(loginPacket.password.equals(password)){
							loginPacket.accepted = true;
						} else {
							loginPacket.accepted = false;
							for (String player : players) {
								if(player.equals(loginPacket.username)){
									players.remove(player);
								}
							}
						}
					} else if(split[0].equals("xpos")){
						xPos = Integer.parseInt(split[1].trim());
					} else if(split[0].equals("ypos")){
						yPos = Integer.parseInt(split[1].trim());
					}
				}
			} catch (Exception e) {
				System.out.println("failed to load characterdata");
				e.printStackTrace();
			}
		} else {
			loginPacket.accepted = true;
			if(!createRegisterFile(loginPacket)) throw new RuntimeException("error creating options");
		}
		return loginPacket;
	}
	
	private boolean createRegisterFile(PacketRegister loginPacket){
		try {
			PrintWriter print = new PrintWriter(new PrintWriter(loginFile));
			print.println("name: " + loginPacket.username);
			print.println("password: " + loginPacket.password);
			print.println("xpos: " + xPos);
			print.println("ypos: " + yPos);
			print.close();
			return true;
		} catch (IOException e) {
			System.out.println("failed to save characterData");
			loginPacket.accepted = false;
			e.printStackTrace();
			return false;
		}
	}
	
	private void updatePlayerData(String playername){
		loginFile = new File("characterdata/" + playername + ".txt");
		
		String password = readPassword(loginFile);
		
		if(password.equals("") || password.equals(null)) return;

		try {
			PrintWriter print = new PrintWriter(new PrintWriter(loginFile));
			print.println("name: " + playername);
			print.println("password: " + password);
			print.println("xpos: " + (int)positions.get(playername).xPos);
			print.println("ypos: " + (int)positions.get(playername).yPos);
			print.close();
			
			MainServer.logger.info(playername + " data has been saved!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String readPassword(File loginFile){
		
		if(!loginFile.exists()) return null;
		
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(loginFile));
			
			for(String s = ""; (s=br.readLine()) != null;){
				String split[] = s.split(":");
				
				if(split[0].equals("password")){
					return split[1];
				}
			}
			
		} catch (Exception e) {
			System.out.println("failed to load file");
			e.printStackTrace();
			return null;
		}
		return "";
	}
}
