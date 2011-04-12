package com.barronsoftware.videege.CriticalHits;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class CriticalHitsEntityListener extends EntityListener {
	private final CriticalHits plugin;
	
	public CriticalHitsEntityListener(CriticalHits plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent edee = (EntityDamageByEntityEvent) event;
			if (edee.getDamager() instanceof Player) {
				Player damager = (Player) edee.getDamager();
				//see if the item in the damager's hand is a crit item
				int itemType = damager.getItemInHand().getTypeId();
				CriticalItem critItem = plugin.getCriticalItem(itemType, damager);
				if (critItem != null) {
					if (critItem.isCriticalHit()) {
						edee.setDamage(critItem.getCriticalDamage(edee.getDamage()));
						if (plugin.notifyOnCrit) {
							damager.sendMessage(ChatColor.GREEN + "[CriticalHits] " + ChatColor.WHITE + " You scored a critical hit!");
						}
					}
					else {
						//base hit
						edee.setDamage(critItem.getBaseDamage(edee.getDamage()));
					}
				}
			}
			
		}
	}
	
}