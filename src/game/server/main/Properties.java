package game.server.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Properties {
	
	public File optionsFile;
	
	private int maxPlayers = 6;
	private String name = "TEST SERVER";
	
	private int server_port = 54555;
	
	public Properties() {
		optionsFile = new File("server.txt");
		
		if(!optionsFile.exists()){
			if(!createOptions()) throw new RuntimeException("error creating options");
		}
		
		loadOptions();
	}
	
	private void loadOptions(){
		if(!optionsFile.exists()) return;
		
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(optionsFile));
			
			for(String s = ""; (s=br.readLine()) != null;){
				String split[] = s.split(":");
				
				if(split[0].equals("name")){
					name = split[1];
				} else if(split[0].equals("maxPlayers")){
					maxPlayers = Integer.parseInt(split[1].trim());
				} else if(split[0].equals("server_port")){
					server_port = Integer.parseInt(split[1].trim());
				}
			}
			
		} catch (Exception e) {
			System.out.println("failed to load options");
			e.printStackTrace();
		}
		
	}
	
	private boolean createOptions(){
		try {
			PrintWriter print = new PrintWriter(new PrintWriter(optionsFile));
			print.println("name: " + name);
			print.println("maxPlayers: " + maxPlayers);
			print.println("server_port: " + server_port);
			print.close();
			return true;
		} catch (IOException e) {
			System.out.println("failed to save options");
			e.printStackTrace();
			return false;
		}
	}

	public String getName() {
		return name;
	}
	
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	public int getServer_port() {
		return server_port;
	}
}
