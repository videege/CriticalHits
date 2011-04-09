package com.barronsoftware.videege.CriticalHits;

import java.util.Random;

public class CriticalItem {
	private Integer baseDamage;
	private Integer baseCritDamage;
	private Integer critDamageModifier;
	private Integer critChance;
	
	private static Random rand = new Random();
	
	public CriticalItem(Integer BaseDamage, Integer BaseCritDamage, Integer CritDamageModifier, Integer CritChance) {
		baseDamage = BaseDamage;
		baseCritDamage = BaseCritDamage;
		critDamageModifier = CritDamageModifier;
		critChance = CritChance;
	}
	
	public boolean isCriticalHit() {
		if (critChance == null) return false;
		
		return (rand.nextInt(100) <= critChance);
	}
	
	public int getCriticalDamage() {
		int damage = (baseCritDamage == null ? 0 : baseCritDamage);
		damage = (critDamageModifier == null ? damage : damage + rand.nextInt(critDamageModifier));
		return damage;
	}
	
	public int getBaseDamage() {
		return baseDamage;
	}
}
