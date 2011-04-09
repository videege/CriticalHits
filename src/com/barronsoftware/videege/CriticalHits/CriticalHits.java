package com.barronsoftware.videege.CriticalHits;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

public class CriticalHits extends JavaPlugin {
	private Logger logger;
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	
	private final HashMap<Integer, CriticalItem> critItems = new HashMap<Integer, CriticalItem>();
	public boolean notifyOnCrit;
		
    public final CriticalHitsEntityListener entityListener = new CriticalHitsEntityListener(this);
    
    
    public void onEnable() {
        this.logger = this.getServer().getLogger();
        
        // Get the configuration
        if (!getDataFolder().exists()) {
        	buildConfiguration();
        }
        
        loadConfiguration();
    
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        
        pm.registerEvent(Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
        
        
        // Output loading message
        PluginDescriptionFile pdfFile = this.getDescription();
        this.logInfo("v" + pdfFile.getVersion() + " enabled.");
    }

	@Override
	public void onDisable() {
		this.logInfo("unloaded successfully.");
	}
	
	private void loadConfiguration() {
		Configuration config = this.getConfiguration();
		config.load();
		List<String> items = config.getKeys("items");
		
		if (items != null) {
			for (String s:items) {
				String path = "items." + s;
				Integer itemID = new Integer(s);
				//get all the properties, if possible
				Integer baseDamage = config.getInt(path + ".baseDamage", 0);
				Integer baseCritDamage = config.getInt(path + ".baseCritDamage", 0);
				Integer critChance = config.getInt(path + ".critChance", 0);
				Integer critDamageModifier = config.getInt(path + ".critDamageModifier", 0);
				CriticalItem citem = new CriticalItem(baseDamage, baseCritDamage, critDamageModifier, critChance);
				critItems.put(itemID, citem);
			}
		}
		
		notifyOnCrit = config.getBoolean("notifyOnCrit", false);
	}
	
	private void buildConfiguration() {
		Configuration config = this.getConfiguration();
		//set up some basic values for a stone sword and axe
		config.setProperty("notifyOnCrit", true);

		HashMap<String, Integer> itemProps = new HashMap<String, Integer>();
		itemProps.put("baseDamage", 3);
		itemProps.put("critChance", 20);
		itemProps.put("critChanceModifier", 10);
		itemProps.put("baseCritDamage", 6);
		itemProps.put("critDamageModifier", 2);
		
		config.setProperty("items.272", itemProps);
		
		HashMap<String, Integer> itemProps2 = new HashMap<String, Integer>();
		itemProps2.put("baseDamage", 3);
		itemProps2.put("critChance", 20);
		itemProps2.put("critChanceModifier", 10);
		itemProps2.put("baseCritDamage", 6);
		itemProps2.put("critDamageModifier", 2);
		
		config.setProperty("items.275", itemProps);
		
		if (!config.save()) {
			logInfo("Unable to persist configuration file.");
		}
		
	}
	
	public CriticalItem getCriticalItem(int itemType) {
		Integer key = Integer.valueOf(itemType);
		if (critItems.containsKey(itemType)) {
			return critItems.get(key);
		}
		
		return null;
	}
	
	public void logInfo(String str) {
    	logger.log(Level.INFO, "[CriticalHits] " + str);
    }

}
