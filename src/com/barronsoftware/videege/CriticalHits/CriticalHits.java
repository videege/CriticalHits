package com.barronsoftware.videege.CriticalHits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class CriticalHits extends JavaPlugin {
	private Logger logger;
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	
	private final HashMap<Integer, ArrayList<CriticalItem> > critItems = new HashMap<Integer, ArrayList<CriticalItem> >();
	private boolean usingPermissions;
	public boolean notifyOnCrit;
		
    public final CriticalHitsEntityListener entityListener = new CriticalHitsEntityListener(this);
    
    public static PermissionHandler Permissions;
    
    private boolean setupPermissions() {
    	Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

        if (CriticalHits.Permissions == null) {
            if (test != null) {
            	CriticalHits.Permissions = ((Permissions)test).getHandler();
            } else {
            	this.logInfo("Permission system not detected. Plugin will ignore any permissions entries in the config.");
                return false;
            }
        }	
        return true;
	}
    
    public boolean hasPermission(Player p, String permission) {
    	if (!usingPermissions) {
    		return true;
    	}
    	else {
    		return Permissions.has(p, permission);
    	}
    }
    
    public void onEnable() {
        this.logger = this.getServer().getLogger();
        
        // Get the configuration
        if (!getDataFolder().exists()) {
        	buildConfiguration();
        }
        
        usingPermissions = setupPermissions();
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
				String []itemList = s.split(" ");
				for (String item:itemList) {
					Integer itemID = new Integer(item.equals("*") ? "-1" : item); //'*' is a global entry, indexed to -1.
										
					String sbd = config.getString(path + ".baseDamage", null);
					Integer baseDamage = (sbd == null ? null : Integer.parseInt(sbd));
					
					boolean critDamageRelative = false;
					String sbcd = config.getString(path + ".baseCritDamage", null);
					sbcd = sbcd.toLowerCase();
					if (sbcd != null && sbcd.endsWith("x")) {
						critDamageRelative = true;
						sbcd = sbcd.substring(0, sbcd.indexOf("x"));
					}
					
					Double baseCritDamage = (sbcd == null ? null : Double.parseDouble(sbcd));
					
					String cdm = config.getString(path + ".critDamageModifier", null);
					Integer critDamageModifier = (cdm == null ? null : Integer.parseInt(cdm));
					
					String cc = config.getString(path + ".critChance", null);
					Integer critChance = (cc == null ? null : Integer.parseInt(cc));
					
					String permission = config.getString(path + ".permission", null);
					
					CriticalItem citem = new CriticalItem(baseDamage, baseCritDamage, critDamageModifier, critChance, critDamageRelative, permission);
					if (!critItems.containsKey(itemID)) {
						critItems.put(itemID, new ArrayList<CriticalItem> ());
					}
					critItems.get(itemID).add(citem);
				}
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
	
	public CriticalItem getCriticalItem(int itemType, Player p) {
		//this function returns the first matching CriticalItem entry 
		//available for this player.  It will look for a matching global
		//entry if no items are found.
		Integer key = Integer.valueOf(itemType);
		if (critItems.containsKey(key)) {
			ArrayList<CriticalItem> items = critItems.get(key);
			for (CriticalItem item:items) {
				if (item.getPermission() != null && !hasPermission(p, item.getPermission())) {
					continue; //player didn't have permission for this entry
				}
				//System.out.println("Using critItem entry for permission: " + item.getPermission());
				return item;
			}
		}
		//Check the global entry (if any)
		if (critItems.containsKey(-1)) {
			ArrayList<CriticalItem> globalItems = critItems.get(-1);
			for (CriticalItem item:globalItems) {
				if (item.getPermission() != null && !hasPermission(p, item.getPermission())) {
					continue; //player didn't have permission for this entry
				}
				//System.out.println("Using global critItem entry for permission: " + item.getPermission());
				return item;
			}
		}
		
		return null;
	}
	
	public void logInfo(String str) {
    	logger.log(Level.INFO, "[CriticalHits] " + str);
    }

}
