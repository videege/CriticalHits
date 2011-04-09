package com.barronsoftware.videege.CriticalHits;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CriticalHits extends JavaPlugin {
	private Logger logger;
	
    public final CriticalHitsPlayerListener playerListener = new CriticalHitsPlayerListener(this);
    public final CriticalHitsEntityListener entityListener = new CriticalHitsEntityListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    
    public void onEnable() {
        this.logger = this.getServer().getLogger();
        
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        
        // Output loading message
        PluginDescriptionFile pdfFile = this.getDescription();
        this.logInfo("v" + pdfFile.getVersion() + " enabled.");
    }

	@Override
	public void onDisable() {
		this.logInfo("unloaded successfully.");
	}
	
	public void logInfo(String str) {
    	logger.log(Level.INFO, "[CriticalHits] " + str);
    }

}
