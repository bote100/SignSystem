package net.bote.signsystem.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;

public class UpdateInformation {

	private boolean newest;
	private URL url;
	
	public UpdateInformation(String uri) {
		
		try {
			this.url = new URL(uri);
		} catch (MalformedURLException e) {
			Bukkit.getConsoleSender().sendMessage("[SignSystem] §cDie Updateseite, existiert nicht mehr!");
		}
		
		try {
			check();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void check() throws IOException {
		BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
        	reader = new BufferedReader(new InputStreamReader(this.url.openStream(), "UTF-8"));
			
			int count;
			final char[] data = new char[5000];
			while ((count = reader.read(data)) != -1){
			    builder.append(data,0 ,count);
			}
        } finally {
        	IOUtils.closeQuietly(reader);
        }

        String response = builder.toString();

        if(response.contains(SignSystem.getInstance().getDescription().getVersion())) {
            this.newest = true;
        } else {
            this.newest = false;
        }
		
	}
	
	public boolean isNewest() {
		return this.newest;
	}
	
}
