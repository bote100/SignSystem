package net.bote.signsystem.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

import net.bote.signsystem.main.SignSystem;
import net.bote.signsystem.main.Var;

public class ServerSign {
	
	private static SignSystem main = SignSystem.getInstance();
	private static HashMap<Integer, SignState> status = new HashMap<>();
	
	
	private Location loc;
	private Sign sign;
	
	// ---
	
	private int port;
	private String ip;
	private String name;
	private String group;
	private int id;
	private String statustext;
	private boolean maintenance;
	
	public ServerSign(Location location) {
		this.loc = location;
		this.sign = (Sign) location.getBlock().getState();
	}
	
	public ServerSign(Location location, int port, String ip, String name, String group, int uuid, boolean maintenancebol) {
		
		this.port = port;
		this.ip = ip;
		this.name = name;
		this.group = group;
		this.loc = location;
		this.id = uuid;
		this.sign = (Sign) location.getBlock().getState();
		this.maintenance = maintenancebol;
		
		if(SignSystem.maintenance.containsKey(group)) {
			if(SignSystem.maintenance.get(group)) {
				status.put(uuid, SignState.MAINTENANCE);
			} else {
				status.put(uuid, SignState.OFFLINE);
			}
		} else {
			SignSystem.maintenance.put(group, maintenancebol);
			
			// System.out.println("Sign der gruppe " + group + " HINZUFEGUEGT");
			
			if(maintenancebol) {
				status.put(uuid, SignState.MAINTENANCE);
			} else {
				status.put(uuid, SignState.OFFLINE);
			}
			
			
		}
		
	}
	
	public ServerSign(Location location, int port, String ip, String name, String group, boolean mainten) {
		this.port = port;
		this.ip = ip;
		this.name = name;
		this.group = group;
		this.loc = location;
		this.sign = (Sign) location.getBlock().getState();
		this.maintenance = mainten;
	}
	
	public void setID(int uuid) {
		this.id = uuid;
	}
	
	public Integer getID() {
		return this.id;
	}
	
	public void setState(SignState state) {
		status.put(id, state);
	}
	
	@Deprecated
	public void setMaintenanceStatusAfter() {
		
		if(SignSystem.maintenance.containsKey(group)) {
			if(SignSystem.maintenance.get(group)) {
				status.put(this.id, SignState.MAINTENANCE);
			} else {
				status.put(this.id, SignState.OFFLINE);
			}
		} else {
			SignSystem.maintenance.put(group, this.maintenance);
			
			main.getConfig().set(getID() + ".info.maintenance", this.maintenance);

			main.saveConfig();
			
			if(this.maintenance) {
				status.put(this.id, SignState.MAINTENANCE);
			} else {
				status.put(this.id, SignState.OFFLINE);
			}
			
		}
	}
	
	public void setMaintenance() {
		if(SignSystem.maintenance.containsKey(group)) {
			if(SignSystem.maintenance.get(group)) {
				status.put(this.id, SignState.MAINTENANCE);
			} else {
				status.put(this.id, SignState.OFFLINE);
			}
			return;
		}
		
		SignSystem.maintenance.put(group, this.maintenance);
		
		main.getConfig().set(getID() + ".info.maintenance", this.maintenance);

		main.saveConfig();
		
		if(this.maintenance) {
			status.put(this.id, SignState.MAINTENANCE);
		} else {
			status.put(this.id, SignState.OFFLINE);
		}
		
	}
	
	public String getName() {
		return name;
	}
	
	public boolean getMaintenance() {
		return this.maintenance;
	}
	
	public String getIP() {
		return ip;	
	}
	
	public String getGroup() {
		return group;
	}
	
	public Integer save() {
		int i = main.getConfig().getKeys(false).size() + 1;
		
		main.getConfig().set(i + ".info.name", name);
		main.getConfig().set(i + ".info.ip", ip);
		main.getConfig().set(i + ".info.port", port);
		main.getConfig().set(i + ".info.group", group);
		
		main.getConfig().set(i + ".location.world", loc.getWorld().getName());
		main.getConfig().set(i + ".location.x", loc.getBlockX());
		main.getConfig().set(i + ".location.y", loc.getBlockY());
		main.getConfig().set(i + ".location.z", loc.getBlockZ());
		
		main.saveConfig();
		return i;
	}
	
	public SignState getState() {
		return status.get(id);
	}
	
	public ServerSign getServerSign() {
		return new ServerSign(loc, port, ip, name, group, id, maintenance);
	}
	
	public String getStatusText() {
		return this.statustext;
	}
	
	public void setStatusText(String text) {
		this.statustext = text;
	}
	
	public void update() {
		
		if(!status.get(id).equals(SignState.MAINTENANCE)) {
			Socket socket = new Socket();
			
			try {
				socket.connect(new InetSocketAddress(this.ip, this.port));
				
				InputStream in = socket.getInputStream();
				OutputStream out = socket.getOutputStream();
				
				out.write(254);
				
				StringBuilder sb = new StringBuilder();
				
				int i;
				
				while((i = in.read()) != -1) {
					
					if((i != 0) && (i > 16) && (i != 255) && (i != 23) && (i != 24)) {
						sb.append((char)i);
					}
					
				}
				
				String[] data = sb.toString().split("§");
				
				String motd = data[0];
				String mesh = data[1] + data[2];
				
				byte blockbyte = 0;
				
				int onlinePlayers = Integer.valueOf(data[1]);
			    int maxPlayers = Integer.valueOf(data[2]);
				
				String fetch_motd = motd.toLowerCase();
				
				String status = "";
				String motd_state = "";
				
				if(fetch_motd.contains("lobby")) {
					
					if(onlinePlayers >= maxPlayers) {
						motd_state = Var.FULL_LOBBY;
					} else {
						motd_state = Var.LOBBY;
					}
					
					motd.replace("LOBBY", "");
					motd.replace("Lobby", "");
					motd.replace("lobby", "");
					
				} else if(fetch_motd.contains("ingame") || fetch_motd.contains("game")) {
					
					setState(SignState.INGAME);
					
					if(!Var.HIDEINGAMESERVERS) {
						status = motd_state + ";" + onlinePlayers + " / " + maxPlayers + ";" + motd;
						
						setStatusText(status);
					}
					
					return;
					
				} else {
					
					// Config
					
					motd_state = Var.UNKNOWN;
					
				}
				
				status = motd_state + ";" + onlinePlayers + " / " + maxPlayers + ";" + motd;
				
				setStatusText(status);
				
				setState(SignState.ONLINE);
			
				socket.close();
				
			} catch (IOException error) {
				setState(SignState.OFFLINE);
			}
			
		} else {
			setState(SignState.MAINTENANCE);
		}
		
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public Sign getSign() {
		return sign;
	}
	
	@Deprecated
	public static ArrayList<ServerSign> getSigns() {
		
		ArrayList<ServerSign> list = new ArrayList<ServerSign>();
		for(String x : main.getConfig().getKeys(false)) {
			ConfigurationSection section = main.getConfig().getConfigurationSection(x);
			ConfigurationSection cs = section.getConfigurationSection("location");
			
			World world = Bukkit.getServer().getWorld(cs.getString("world"));
			
			Location location = new Location(world, cs.getDouble("x"), cs.getDouble("y"), cs.getDouble("z"));
			
			Block b = location.getBlock();
			
			if(b != null) {
				
				ConfigurationSection info = section.getConfigurationSection("info");
				
				list.add(new ServerSign(location, info.getInt("port"), info.getString("ip"), info.getString("name"), info.getString("group"), Integer.parseInt(x), info.getBoolean("maintenance")));
				
			} else {
				main.getConfig().set(x, null);
			}
			
		}
		return list;
		
	}
	
	public static ArrayList<ServerSign> getServerSigns() {
		
		ArrayList<ServerSign> list = new ArrayList<ServerSign>();
		main.getConfig().getKeys(false).forEach(x -> {
			ConfigurationSection section = main.getConfig().getConfigurationSection(x);
			ConfigurationSection cs = section.getConfigurationSection("location");
			
			World world = Bukkit.getServer().getWorld(cs.getString("world"));
			
			Location location = new Location(world, cs.getDouble("x"), cs.getDouble("y"), cs.getDouble("z"));
			
			Block b = location.getBlock();
			
			if(b != null) {
				ConfigurationSection info = section.getConfigurationSection("info");
				
				list.add(new ServerSign(location, info.getInt("port"), info.getString("ip"), info.getString("name"), info.getString("group"), Integer.parseInt(x), info.getBoolean("maintenance")));
			} else {
				main.getConfig().set(x, null);
			}
		});
		return list;
		
	}
  
	
	// Location -> "location"
	// Infos -> "info"
	
}
