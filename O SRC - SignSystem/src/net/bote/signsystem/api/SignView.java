package net.bote.signsystem.api;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;

import net.bote.signsystem.main.SignSystem;
import net.bote.signsystem.main.Var;

public class SignView {
	
	private static int count = 0;
	
	// Loading animation	
	private static String[] loading = new String[] {
			
			"O o o o o", "o O o o o", "o o O o o", "o o o O o", "o o o o O", "o o o O o", "o o O o o", "o O o o o"
			
	};
	
	// start updating signs
	
	public static void startUpdater() {
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SignSystem.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				
					SignSystem.serversigns.forEach(signs -> {
						signs.update();
						
						if(signs.getState().equals(SignState.OFFLINE)) {
							SignSystem.clearSign(signs.getSign());
							signs.getSign().setLine(1, Var.LOADING);
							signs.getSign().setLine(2, loading[count]);
						} else if(signs.getState().equals(SignState.ONLINE)) {
							SignSystem.clearSign(signs.getSign());
							signs.getSign().setLine(0, "- " + signs.getName() + " -");
							signs.getSign().setLine(1, signs.getStatusText().split(";")[0]);
							signs.getSign().setLine(2, signs.getStatusText().split(";")[1]);
							try {
								signs.getSign().setLine(3, signs.getStatusText().split(";")[2].split(" ")[0]);
							} catch (IndexOutOfBoundsException e) {
								signs.getSign().setLine(3, "");
							}
						} else if(signs.getState().equals(SignState.MAINTENANCE)) {
							SignSystem.clearSign(signs.getSign());
							signs.getSign().setLine(1, Var.MAINTENANCE);
							signs.getSign().setLine(2, loading[count]);
						} else if(signs.getState().equals(SignState.INGAME)) {
							if(Var.HIDEINGAMESERVERS) {
								SignSystem.clearSign(signs.getSign());
								signs.getSign().setLine(1, Var.LOADING);
								signs.getSign().setLine(2, loading[count]);
							} else {
								SignSystem.clearSign(signs.getSign());
								signs.getSign().setLine(0, "- " + signs.getName() + " -");
								signs.getSign().setLine(1, Var.INGAME);
								signs.getSign().setLine(2, signs.getStatusText().split(";")[1]);
								signs.getSign().setLine(3, signs.getStatusText().split(";")[2].split(" ")[0]);
							}
						}
						
						signs.getSign().update();
					});
					count++;
					
					if(count >= loading.length) {
						count = 0;
					}
				
			}
		}, 0, 11);
		
	}

}
