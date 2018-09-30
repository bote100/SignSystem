package net.bote.signsystem.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginAwareness;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.ChatColor;

public class Var {

	public static String INGAME = ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("Ingame"));
	public static String LOADING = ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("Loading"));
	public static String FULL_LOBBY = ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("FullLobby"));
	public static String LOBBY = ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("Lobby"));
	public static String UNKNOWN = ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("UnknownState"));
	public static String MAINTENANCE = ChatColor.translateAlternateColorCodes('&', SignSystem.cfg.getString("Maintenance"));
	public static boolean HIDEINGAMESERVERS = SignSystem.cfg.getBoolean("HideIngameServers");
	public static boolean PLAY_EFFECTS = SignSystem.cfg.getBoolean("PlaySignEffects");
	
	// reload the config
	// code by md5
	
	public static void reloadConfig() {
        YamlConfiguration newConfig = YamlConfiguration.loadConfiguration(SignSystem.file);

        final InputStream defConfigStream = SignSystem.getInstance().getResource("settings.yml");
        if (defConfigStream == null) {
            return;
        }

        final YamlConfiguration defConfig;
        if (isStrictlyUTF8() || FileConfiguration.UTF8_OVERRIDE) {
            defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8));
        } else {
            final byte[] contents;
            defConfig = new YamlConfiguration();
            try {
                contents = ByteStreams.toByteArray(defConfigStream);
            } catch (final IOException e) {
                return;
            }

            final String text = new String(contents, Charset.defaultCharset());
            if (!text.equals(new String(contents, Charsets.UTF_8))) {
            	SignSystem.getInstance().getLogger().warning("Default system encoding may have misread config.yml from plugin jar");
            }

            try {
                defConfig.loadFromString(text);
            } catch (final InvalidConfigurationException e) {}
        }

        newConfig.setDefaults(defConfig);
	}
	
	private static boolean isStrictlyUTF8() {
        return SignSystem.getInstance().getDescription().getAwareness().contains(PluginAwareness.Flags.UTF8);
}
	
}
