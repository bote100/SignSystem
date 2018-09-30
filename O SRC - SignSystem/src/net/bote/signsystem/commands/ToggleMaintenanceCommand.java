package net.bote.signsystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bote.signsystem.api.MaintenanceGroup;
import net.bote.signsystem.api.ServerSign;
import net.bote.signsystem.main.SignSystem;
import net.md_5.bungee.api.ChatColor;

public class ToggleMaintenanceCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		// TODO Auto-generated method stub
		
		Player p = (Player)sender;
		
		if(p.hasPermission("signsystem.togglemaintenance")) {
			
			if(args.length == 1) {
				
				String group = args[0];
				
				if(!SignSystem.isGroupExists(group)) {
					p.sendMessage(SignSystem.prefix + "§7Es wurde kein Sign mit der Gruppe §e" + group + " §7gefunden!");
					return false;
				}
				
				boolean bol = SignSystem.maintenance.get(group);
				
				if(bol) {
					p.sendMessage(SignSystem.prefix + "§7Die Sign-Gruppe §e" + group + " §7wurde aus dem §eWartungsmodus geholt");
					
					for(ServerSign signs : SignSystem.serversigns) {
						if(signs.getGroup().equals(group)) {
							new MaintenanceGroup(signs).setMaintenance(false);
						}
					}
					
				} else {
					p.sendMessage(SignSystem.prefix + "§7Die Sign-Gruppe §e" + group + " §7wurde in §eWartungsmodus versetzt");
					
					for(ServerSign signs : SignSystem.serversigns) {
						if(signs.getGroup().equals(group)) {
							new MaintenanceGroup(signs).setMaintenance(true);
						}
					}
					
				}
				
				Bukkit.getServer().reload();
				
				
			} else {
				p.sendMessage(SignSystem.prefix + "§7Nutze: §e/togglemaintenance <Gruppe>");
			}
			
		} else {
			p.sendMessage(SignSystem.prefix + ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("NoPerms")));
		}
		
		return false;
	}

}
