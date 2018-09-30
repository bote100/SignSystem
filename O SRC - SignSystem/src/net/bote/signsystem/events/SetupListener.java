package net.bote.signsystem.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.bote.signsystem.api.ServerSign;
import net.bote.signsystem.api.SignState;
import net.bote.signsystem.main.SignSystem;

public class SetupListener implements Listener {
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		
		if(e.getPlayer().hasPermission("signsystem.setup")) {
			
			if(SignSystem.setup.equals("")) {
				return;
			}
			
			String[] setup = SignSystem.setup.split(";");
			
			if(setup[0].equals(e.getPlayer().getName())) {
				
				Player p = e.getPlayer();
				
				e.setCancelled(true);
				
				Block b = e.getBlock();
				
				if(b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN) {
					
					p.sendMessage(SignSystem.prefix + "§aDu hast erfolgreich ein ServerSign gesetzt!");
					
					ServerSign ss = new ServerSign(b.getLocation(), Integer.parseInt(setup[3]), setup[2], setup[1], setup[4], false);
					
					// new ServerSign(location, port, ip, name, group)
					
					int id = ss.save();
					ss.setID(id);
					ss.setMaintenance();
					ss.setState(SignState.OFFLINE);
					ss.update();
					
					SignSystem.serversigns.add(ss);
					
					p.sendMessage(SignSystem.prefix + "§eDu hast ein neues Sign registriert. #" + SignSystem.serversigns.size());
					Bukkit.getConsoleSender().sendMessage("[SignSystem] §e[Sign-#" + SignSystem.serversigns.size() +"-" + setup[2] +":" + Integer.parseInt(setup[3]) + "] wurde ins SignSystem registriert!");
					
					SignSystem.setup = "";
					
				} else {
					p.sendMessage(SignSystem.prefix + "§cDu musst ein Schild schlagen!");
				}
				
			}
		}
		
	}

}
