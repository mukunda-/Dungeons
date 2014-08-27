package com.mukunda.dungeons;

import org.bukkit.util.Vector;

public class LootChestInfo {
	public String name;
	public final Vector spawnPoint;
	public final Vector sourcePoint;
	public final boolean giveAll;
	
	
	public LootChestInfo( String name, Vector spawn, Vector source , boolean giveAll ) {
		this.name = name;
		this.spawnPoint = spawn;
		this.sourcePoint = source;
		this.giveAll = giveAll;
	}
}
