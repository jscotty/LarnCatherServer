package game.server.main;

import game.server.listeners.NetworkListenerServer;
import game.server.main.Packet.*;
import game.server.world.World;
import game.server.world.WorldLoader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import operator.Vector2D;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

@SuppressWarnings("serial")
public class MainServer extends JPanel{

	public static Logger logger = Logger.getLogger("server");
	public Properties properties;
	private static Server server;
	
	private int tcp = 54555;
	private PlayerList playerList;
	private World world;
	private World props;

	public static void main(String[] args) {

		server = new Server();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		MainServer main = null;
		try {
			main = new MainServer();
			Log.set(Log.LEVEL_DEBUG);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JFrame frame = new JFrame("networking server");
		frame.add(main);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent windowEvent) {
		    	server.close();
		    }
		});

	}
	
	public MainServer() throws IOException{
		playerList = new PlayerList();
		server.bind(tcp);
		server.start();

		registerPackets();
		
		server.addListener(new NetworkListenerServer(server,playerList,this));

		setPreferredSize(new Dimension(600,300));
		setLayout(new BorderLayout());
		add(getPlayerList(), "North");
		add(getLog(), "Center");
		
		properties = new Properties();
		
		world = WorldLoader.getWorldFromImage("map1.png");
		props = WorldLoader.getPropsFromImage("propsMap.png");
	}
	
	public void registerPackets(){
		Kryo kryo = server.getKryo();
		kryo.register(PacketRegister.class);
		kryo.register(Packet0LoginRequest.class);
		kryo.register(Packet1LoginAnswer.class);
		kryo.register(Packet2Message.class);
		kryo.register(Packet3Disconnect.class);
		kryo.register(MovePacketServer.class);
		kryo.register(LogOffPacket.class);
		kryo.register(NewPlayerPacket.class);
		kryo.register(MovePacket.class);
		kryo.register(Hashtable.class);
		kryo.register(Vector2D.class);
		kryo.register(CopyOnWriteArrayList.class);
		kryo.register(WorldTilesPacket.class);
		kryo.register(ChatPacket.class);
		kryo.register(ColorPacketChar.class);
		kryo.register(Packet.class);
		kryo.register(int[][].class);
		kryo.register(int[].class);
		kryo.register(int.class);
		kryo.register(float[].class);
		kryo.register(float.class);
		kryo.register(Color.class);
		kryo.register(Integer.class);
	}
	
	public JScrollPane getPlayerList(){
		JScrollPane scroll = new JScrollPane(playerList);
		scroll.setBorder(new TitledBorder(new EtchedBorder(), "Player list"));
		
		return scroll;
	}
	
	public JPanel getLog(){
		JPanel panel = new JPanel(new BorderLayout());
		JTextArea text = new JTextArea();
		logger.addHandler(new OutputHandler(text));
		JScrollPane pane = new JScrollPane(text);
		text.setEditable(false);
		
		JTextField input = new JTextField();
		
		panel.add(pane, "Center");
		panel.add(input, "South");
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Log output"));
		
		return panel;
	}
	
	public World getWorld() {
		return world;
	}
	
	public World getProps() {
		return props;
	}

}
