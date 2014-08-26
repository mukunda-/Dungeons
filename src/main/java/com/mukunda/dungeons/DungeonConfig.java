package com.mukunda.dungeons;

import java.io.File;
import java.io.IOException; 
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.util.Vector;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

//-------------------------------------------------------------------------------------------------
public class DungeonConfig { 
	public String name; 
	public String entryPortalWorld;
	public Vector entryPoint; 
	public float entryAngle;
	public Cuboid entryPortal;
	public Vector exitPoint;
	public String exitPointWorld;
	public float exitAngle;
	public Cuboid exitPortal; 
	public Cuboid area; 
	public CooldownType cooldown;
	public ArrayList<LootChestInfo> lootChests;
	
	public boolean enabled; 
	
	public DungeonOptions options = new DungeonOptions();
	
	//-------------------------------------------------------------------------------------------------
	public DungeonConfig() { 
		lootChests = new ArrayList<LootChestInfo>();
		enabled = false;
	}
	
	//-------------------------------------------------------------------------------------------------
	public boolean loadFromConfig( FileConfiguration config ) {
		if( !config.isString( "name" ) ) {
			return false; // bad config
		}
		name = config.getString( "name" ); 
		entryPortalWorld = config.getString( "entry_portal_world" );
		exitPointWorld = config.getString( "exit_point_world" );
		entryPoint = Dungeons.ParseConfigVector(config.getString( "entry_point" ));
		entryAngle = (float)config.getDouble( "entry_angle" );
		entryPortal = new Cuboid( 
				Dungeons.ParseConfigVector(config.getString( "entry_portal.min" ) ), 
				Dungeons.ParseConfigVector(config.getString( "entry_portal.max" ) ) );
		
		exitPoint = Dungeons.ParseConfigVector(config.getString( "exit_point" ));
		exitAngle = (float)config.getDouble( "exit_angle" );
		exitPortal = new Cuboid( 
				Dungeons.ParseConfigVector(config.getString( "exit_portal.min" )), 
				Dungeons.ParseConfigVector(config.getString( "exit_portal.max" )) );
		area = new Cuboid( 
				Dungeons.ParseConfigVector(config.getString( "area.min" )), 
				Dungeons.ParseConfigVector(config.getString( "area.max" )) ); 
		cooldown = CooldownType.fromString( config.getString( "cooldown" ) );
		//maxPlayers = config.getInt( "max_players", 1 ); 
		enabled = config.getBoolean( "enabled", false );
		
		loadLootChests( config );

		options.loadFromConfig( config, "options" );
		
		if( enabled && !canEnable() ) {
			enabled = false;
		} 
		return true;
		
	}
	
	//-------------------------------------------------------------------------------------------------
	public void save() {
		YamlConfiguration config = new YamlConfiguration();
		config.set( "name", name );
		config.set( "entry_portal_world", entryPortalWorld );
		config.set( "exit_point_world", exitPointWorld );
		config.set( "entry_point", Dungeons.FormatConfigVector(entryPoint,false) );
		config.set( "entry_angle", entryAngle );
		if( entryPortal != null ) {
			config.set( "entry_portal.min", Dungeons.FormatConfigVector(entryPortal.min,true) );
			config.set( "entry_portal.max", Dungeons.FormatConfigVector(entryPortal.max,true) );
		}
		config.set( "exit_point", Dungeons.FormatConfigVector(exitPoint,false) );
		config.set( "exit_angle", exitAngle );
		if( exitPortal != null ) {
			config.set( "exit_portal.min", Dungeons.FormatConfigVector(exitPortal.min,true) );
			config.set( "exit_portal.max", Dungeons.FormatConfigVector(exitPortal.max,true) );
		}
		if( area != null ) {
			config.set( "area.min", Dungeons.FormatConfigVector(area.min,true) );
			config.set( "area.max", Dungeons.FormatConfigVector(area.max,true) );
		} 
		//config.set( "max_players", maxPlayers ); 
		cooldown = CooldownType.fromString( config.getString( "cooldown" ) );
		config.set( "cooldown", cooldown.toString() );
		config.set( "enabled", enabled );
		
		saveLootChests(config);
		
		options.writeToConfig( config, "options" );
		
		try {
			config.save( new File( Dungeons.getContext().getDataFolder(), "configs/" + name + ".yml" ) );
		} catch( IOException e) {
			Dungeons.getContext().getLogger().severe( "Couldn't save dungeon config!" );
		}
	}
	//-------------------------------------------------------------------------------------------------
	public boolean canEnable() {
		// TODO validate that world name is registered with multiverse.
		if( entryPortalWorld == null ||
				Dungeons.getContext().getServer().getWorld( entryPortalWorld ) == null ||
				exitPointWorld == null ||
				Dungeons.getContext().getServer().getWorld( exitPointWorld ) == null ||
				entryPoint == null || entryPortal == null ||
				exitPoint == null || exitPortal == null ||
				area == null ||
				options.getMaxPlayers() < 1 ) 
			return false;
		
		return true;
		
	}
	
	//-------------------------------------------------------------------------------------------------
	private void loadLootChests( FileConfiguration config ) {
		lootChests.clear();
		
		ConfigurationSection loot = config.getConfigurationSection( "loot" );
		if( loot == null ) return; // no loot chests!
		Set<String> keys = loot.getKeys(false);
		for( String key : keys ) {
			Vector spawn, source;
			spawn = Dungeons.ParseConfigVector(config.getString( "loot." + key + ".spawn" ));
			source = Dungeons.ParseConfigVector(config.getString( "loot." + key + ".source" ));
			if( spawn == null || source == null ) continue;
			lootChests.add( new LootChestInfo(key, spawn, source) );
			
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private void saveLootChests( YamlConfiguration config ) {
		for( LootChestInfo info : lootChests ) {
			config.set( "loot." + info.name + ".spawn", Dungeons.FormatConfigVector(info.spawnPoint,true) );
			config.set( "loot." + info.name + ".source", Dungeons.FormatConfigVector(info.sourcePoint,true) );
		}
	}
	
	public LootChestInfo getLootChest( String name ) {
		for( LootChestInfo info : lootChests ) {
			if( info.name.equals( name ) ) return info;
		}
		return null;
	}
	
	public void addLootChest( LootChestInfo info ) {
		lootChests.add( info );
	}
	
	public void removeLootChest( LootChestInfo info ) {
		lootChests.remove( info );
	}
	
	public List<LootChestInfo> getLootChestList() {
		return lootChests;
	}
}
