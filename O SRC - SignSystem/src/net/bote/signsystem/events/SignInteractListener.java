package net.bote.signsystem.events;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import net.bote.signsystem.api.ServerSign;
import net.bote.signsystem.main.SignSystem;
import net.bote.signsystem.main.Var;

public class SignInteractListener implements Listener {
	
	private ArrayList<Player> used = new ArrayList<Player>();

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			
			Block b = e.getClickedBlock();
			
			if((b.getType() != Material.SIGN) && (b.getType() != Material.SIGN_POST) && (b.getType() != Material.WALL_SIGN)) {
		        return;
		    }
			
			Player p = e.getPlayer();
			
			if(used.contains(p)) return;
			
			SignSystem.serversigns.forEach(all -> {
				if(!all.getLocation().equals(b.getLocation()))  return;
					
					if(used.contains(p)) return;
					
					used.add(p);
					
					Bukkit.getScheduler().runTaskLater(SignSystem.getInstance(), new Runnable() {
						
						@Override
						public void run() {
							used.remove(p);
						}
					}, 10);
					
					if(all.getSign().getLine(1).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Var.LOADING))) {
						if(Var.PLAY_EFFECTS) {
							p.playEffect(all.getLocation(), Effect.CLOUD, 3);
							p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 2, 3);
							p.sendMessage(SignSystem.prefix + ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("ServerLoading")));
						}
						return;
					}
					
					if(all.getSign().getLine(1).equalsIgnoreCase(ChatColor.translateAlternateColorCodes('&', Var.MAINTENANCE))) {
						if(Var.PLAY_EFFECTS) {
							p.playEffect(all.getLocation(), Effect.CLOUD, 3);
							p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 2, 3);
							
							String message = ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("ServerMaintenance"));
							message = message.replace("%group%", all.getGroup());
							
							p.sendMessage(SignSystem.prefix + message);
						}
						return;
					}
					
					/*
					 * doesn't contains
					 * 
					 * 
					if(all.getSign().getLine(3).equalsIgnoreCase("§5Private")) {
						
						if(p.hasPermission("signsystem.joinpremium")) {
							
							SignSystem.connect(p, all.getName());
							
						} else {
							// TODO CONFIG
							
							p.sendMessage(SignSystem.prefix + ""); 
						}
						
					}
					
					*/
					SignSystem.connect(p, all.getSign().getLine(0).split(" ")[1]);
			});
		}
	}
	
}
