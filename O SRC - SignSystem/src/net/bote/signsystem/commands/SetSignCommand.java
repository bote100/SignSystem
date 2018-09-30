package net.bote.signsystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.bote.signsystem.main.SignSystem;

public class SetSignCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		// TODO Auto-generated method stub
		
		if(sender instanceof Player) {
			Player p = (Player)sender;
			
			if(p.hasPermission("signsystem.setup")) { 
				
				if(args.length == 4) {
					
					int port = 0;
					
					try {
						port = Integer.parseInt(args[2]);
					} catch (NumberFormatException error) {
						p.sendMessage(SignSystem.prefix + "§7Nutze: §e/addsign <Servername> <IP> <Port> <Gruppe>"); 
						return true;
					}
					
					String name = args[0];
					String ip = args[1];
					String group = args[3];
					
					if(ip.equalsIgnoreCase("localhost")) {
						ip = "127.0.0.1";
					}
					
					SignSystem.setup = p.getName() + ";" + name + ";" + ip + ";" + port + ";" + group;
					p.sendMessage(SignSystem.prefix + "§aSchlage nun das Schild!");
					
				} else {
					p.sendMessage(SignSystem.prefix + "§7Nutze: §e/addsign <Servername> <IP> <Port> <Gruppe>");
				}
				
			} else {
				p.sendMessage(SignSystem.prefix + ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("NoPerms")));
			}
			
		}
		
		return true;
	}

}
