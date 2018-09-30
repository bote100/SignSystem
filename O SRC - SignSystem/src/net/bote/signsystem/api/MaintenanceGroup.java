package net.bote.signsystem.api;

import org.bukkit.configuration.ConfigurationSection;

import net.bote.signsystem.main.SignSystem;

public class MaintenanceGroup {

	private ServerSign sign = null;
	
	public MaintenanceGroup(ServerSign s) {
		this.sign = s;
	}
	
	public boolean isMaintenace() {
		
		if(SignSystem.maintenance.containsKey(this.sign.getGroup())) return SignSystem.maintenance.get(this.sign.getGroup());
		
		ConfigurationSection s1 = SignSystem.getInstance().getConfig().getConfigurationSection(String.valueOf(sign.getID()));
		ConfigurationSection section = s1.getConfigurationSection(String.valueOf("info"));
		
		return section.getBoolean("maintenance");
		
	}
	
	public void setMaintenance(boolean maintenance) {
		SignSystem.getInstance().getConfig().set(sign.getID() + ".info.maintenance", maintenance);
		SignSystem.getInstance().saveConfig();
		SignSystem.maintenance.put(this.sign.getGroup(), maintenance);
	}

}
