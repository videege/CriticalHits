package com.barronsoftware.videege.CriticalHits;

import java.util.Random;

public class CriticalItem {
	private Integer baseDamage;
	private Double baseCritDamage;
	private Integer critDamageModifier;
	private boolean critDamageRelative;
	private Integer critChance;
	private String permission;	
	
	private static Random rand = new Random();
	
	public CriticalItem(Integer BaseDamage, Double BaseCritDamage, Integer CritDamageModifier, Integer CritChance, boolean CritDamageRelative, String Permission) {
		baseDamage = BaseDamage;
		baseCritDamage = BaseCritDamage;
		critDamageModifier = CritDamageModifier;
		critChance = CritChance;
		critDamageRelative = CritDamageRelative;
		permission = Permission;
	}
	
	public boolean isCriticalHit() {
		if (critChance == null) return false;
		
		return (rand.nextInt(100) <= critChance);
	}
	
	public String getPermission() {
		return permission;
	}
	
	public int getCriticalDamage(int eventDamage) {
		int damage = (baseDamage == null ? eventDamage : baseDamage); //baseline
		if (baseCritDamage != null) {
			damage = (int) (critDamageRelative ? Math.floor(damage * baseCritDamage) : Math.floor(baseCritDamage));
		}
		damage = (critDamageModifier == null ? damage : damage + rand.nextInt(critDamageModifier));
		return damage;
	}
	
	public int getBaseDamage(int eventDamage) {
		return baseDamage == null ? eventDamage : baseDamage;
	}
}
