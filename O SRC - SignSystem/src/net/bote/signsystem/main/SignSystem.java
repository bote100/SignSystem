package net.bote.signsystem.main;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.bote.signsystem.api.MaintenanceGroup;
import net.bote.signsystem.api.ServerSign;
import net.bote.signsystem.api.SignView;
import net.bote.signsystem.commands.SetSignCommand;
import net.bote.signsystem.commands.ToggleMaintenanceCommand;
import net.bote.signsystem.events.SetupListener;
import net.bote.signsystem.events.SignInteractListener;
import net.md_5.bungee.api.ChatColor;

public class SignSystem extends JavaPlugin {
	
	private static SignSystem instance;
	public static String prefix;
	public static String setup = "";
	
	private int count = 0;
	
	public static File file = new File("plugins//SignSystem", "settings.yml");
	
	// efficency
	public static ArrayList<ServerSign> serversigns = new ArrayList<ServerSign>();
	public static HashMap<String, Boolean> maintenance = new HashMap<String, Boolean>();
	public static YamlConfiguration cfg = new YamlConfiguration().loadConfiguration(file);

	@Override
	public void onEnable() {
		
		instance = this;
		
		Bukkit.getConsoleSender().sendMessage("§7----§8[§ebote100 - Pluginshop§8]§7----");
		Bukkit.getConsoleSender().sendMessage("§aProdukt: §3NickSystem");
		Bukkit.getConsoleSender().sendMessage("§aVersion: §3" + this.getDescription().getVersion());
		Bukkit.getConsoleSender().sendMessage("§aEntwickelt von: §3bote100");
		
		boolean bol = new UpdateInformation("http://bote100.eu/pluginshop/api/SignSystem.html").isNewest();
		
		if(bol) {
			Bukkit.getConsoleSender().sendMessage("§aAktuellste Version: §aJa");
		} else {
			Bukkit.getConsoleSender().sendMessage("§aAktuellste Version: §cNein");
		}
		Bukkit.getConsoleSender().sendMessage("§7----§8[§ebote100 - Pluginshop§8]§7----");
		
		if(!file.exists()) {
			cfg.set("Prefix", "&8〣 §6§lSignSystem &8» ");
			cfg.set("NoPerms", "&cKein Recht!");
			cfg.set("ServerLoading", "&7Der Server &clädt &7derzeit!");
			cfg.set("ServerMaintenance", "&7Die Servergruppe&3 %group% &7ist derzeit in &3Wartungen&7.");
			cfg.set("UnknownState", "&cUnknown");
			cfg.set("FullLobby", "&6LOBBY");
			cfg.set("Lobby", "&aLOBBY");
			cfg.set("Loading", "&c&lLoading");
			cfg.set("Maintenance", "&3&lWartungen");
			cfg.set("Ingame", "&3INGAME");
			cfg.set("HideIngameServers", true);
			cfg.set("PlaySignEffects", true);
			
			
			try {
				cfg.save(file);
				Bukkit.getConsoleSender().sendMessage("[SignSystem] §ePlugin startet das erste Mal! Die Konfigurationsdatei wurde erstellt.");
			} catch (IOException e) {
				Bukkit.getConsoleSender().sendMessage("[SignSystem] §cEs gab einen Fehler beim erstellen einer Konfigurationsdatei! Fehler: " + e.getMessage());
			}
		}
		
		prefix = ChatColor.translateAlternateColorCodes('&', cfg.getString("Prefix"));
		
		if(!new File("plugins//SignSystem" , "config.yml").exists()) {
			Bukkit.getConsoleSender().sendMessage("[SignSystem] §cDu hast noch kein Sign eingerichtet!");
		} else {
			
			ServerSign.getServerSigns().forEach(all -> {
				serversigns.add(all);
				maintenance.put(all.getGroup(), new MaintenanceGroup(all).isMaintenace());
				count++;
			});
			
			if(count == 1) {
				Bukkit.getConsoleSender().sendMessage("[SignSystem] §eEs wurde " + count + " Sign registriert!");
			} else {
				Bukkit.getConsoleSender().sendMessage("[SignSystem] §eEs wurden " + count + " Signs registriert!");
			}
		}
		
		Bukkit.getPluginCommand("addsign").setExecutor(new SetSignCommand());
		Bukkit.getPluginCommand("togglemaintenance").setExecutor(new ToggleMaintenanceCommand());
		Bukkit.getPluginManager().registerEvents(new SetupListener(), this);
		Bukkit.getPluginManager().registerEvents(new SignInteractListener(), this);
		
		
		SignView.startUpdater();
		
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		
	}
	
	@Override
	public void onDisable() {}
	
	public static SignSystem getInstance() {
		return instance;
	}
	
	public static void connect(Player p, String server) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			out.writeUTF("Connect");
			out.writeUTF(server);
			p.sendPluginMessage(getInstance(), "BungeeCord", b.toByteArray());
		} catch (IOException ignored) {}
	}
	
	public static boolean isGroupExists(String group) {
		boolean exists = false;
		for(ServerSign signs : serversigns) {
			if(!exists) {
				if(signs.getGroup().equals(group)) {
					exists = true;
				}
			}
		}
		return exists;
	}
	
	public static void clearSign(Sign sign) {
		sign.setLine(0, "");
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");
	}
	
	
	
}
