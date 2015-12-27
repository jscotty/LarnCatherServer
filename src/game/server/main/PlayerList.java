package game.server.main;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JList;


@SuppressWarnings("serial")
public class PlayerList extends JList<Object> {
	private String[] players = new String[]{"there are no players online"};
	public PlayerList() {
		this.setListData(players);
	}
	
	public void addPlayer(CopyOnWriteArrayList<String> playerArrayList){
		String playerList = playerArrayList.toString();

		players = playerList.split(",");
		
		this.setListData(players);
	}

}
