package com.mukunda.dungeons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class UserData {
	 
	
	private class DataEntry {
		
		@SuppressWarnings("unused")
		public UUID id;
		
		// dungeon name -> last completed day
		public HashMap<String,Integer> cooldowns = new HashMap<String,Integer>();
		
		public DataEntry( UUID id ) {
			this.id = id;
		}
		
		
		
	}	
	
	// map players to data
	private HashMap<UUID,DataEntry> playerMap = new HashMap<UUID,DataEntry>();
	
	public UserData() {
		 
	}
	
	private DataEntry getUserData( Player player ) {
		if( playerMap.get(player.getUniqueId()) != null ) return playerMap.get( player.getUniqueId() );
		
		DataEntry data = new DataEntry( player.getUniqueId() );
		playerMap.put( player.getUniqueId(), data );
		
		Path file = Dungeons.getContext().getDataFolder().toPath()
				.resolve( "users" )
				.resolve( player.getUniqueId().toString() + ".yml" );
		
		if( !Files.exists(file) ) {
			// user file doesnt exist yet
			return data;
		}
		
		// otherwise load values from yaml file
		FileConfiguration config = YamlConfiguration.loadConfiguration( file.toFile() );
		
		// todo load into map
		
		ConfigurationSection cooldowns = config.getConfigurationSection("cooldowns");
		if( cooldowns != null ) {
			for( Map.Entry<String, Object> entry : cooldowns.getValues(false).entrySet() ) {
				if( cooldowns.isInt(entry.getKey()) ) {
					data.cooldowns.put( entry.getKey(), (Integer)entry.getValue() );
				}
			}
		}
		return data;
		
	}
	
	public void saveUserData( UUID id, boolean unload ) {
		DataEntry data = playerMap.get(id);
		if( data == null ) return;
		 
		YamlConfiguration config = new YamlConfiguration();
		config.set( "cooldowns", data.cooldowns );
		
		try {
			config.save( 
					Dungeons.getContext().getDataFolder().toPath()
					.resolve( "users" )
					.resolve( id.toString() + ".yml" ).toFile() );
		} catch( IOException e ) {
			Dungeons.getContext().getLogger().severe( 
					"Could not save user data for " + "(" + id + ") -- " + e.getMessage() );
		}
		
		if( unload ) {
			playerMap.remove(id);
		}
	}
	
	public void saveUserData( Player player  ) {
		saveUserData( player.getUniqueId(), true );
	}
	
	public int getCurrentDay() {
		Calendar calendar = Calendar.getInstance();
		long hours = calendar.getTimeInMillis() / (1000*60*60);
		hours -= Dungeons.getContext().cooldownResetOffset;
		return (int)(hours / 24);
	}
	
	// check if the user is currently on a dungeon cooldown
	public boolean hasCooldown( Player player, DungeonConfig dungeon ) {
		DataEntry data = getUserData( player );
		Integer day = data.cooldowns.get( dungeon.name ); 
		if( day == null ) return false;
		if( dungeon.cooldown == CooldownType.NONE ) return false;
		if( dungeon.cooldown == CooldownType.DAY ) {
			// get current day and check if day matches
			return getCurrentDay() == day;
			
		}
		if( dungeon.cooldown == CooldownType.WEEK ) {
			// get current day and check if week matches
			return getCurrentDay()/7 == day/7;
		}
		
		return false;
	}
	
	public void setCooldown( Player player, DungeonConfig dungeon ) {
		DataEntry data = getUserData( player );
		data.cooldowns.put( dungeon.name, getCurrentDay() );
	} 
	
	public void saveAll() {
		for( Entry<UUID,DataEntry> entry : playerMap.entrySet() ) {
			saveUserData( entry.getKey(), false );
		}
		playerMap.clear();
	}
}
