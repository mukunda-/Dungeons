package com.mukunda.dungeons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class DungeonOptions {
	 
	private Integer maxPlayers;  
	//private Boolean[] potions = new Boolean[64];
	private Boolean pearls;
	private String multiInvShare; // MultiInv group to share with
	private HashMap<String,Boolean> potionMap = new HashMap<String,Boolean>();
	private List<String> startupCommands;
	private List<String> unloadCommands;
	
	public boolean allowPearls() {
		if( pearls != null ) return pearls;
		return Dungeons.getContext().defaultOptions.pearls;
	}
	public boolean isPotionAllowed( PotionEffect effect ) {
		return isPotionAllowed( effect.getType().getName() );
	}
	
	public boolean isPotionAllowed( String name ) {
		Boolean allowed = potionMap.get(name); 
		if( allowed != null ) return allowed;
		allowed = Dungeons.getContext().defaultOptions.potionMap.get(name); 
		if( allowed != null ) return allowed;
		return true;
	}
	
	public int getMaxPlayers() {
		if( maxPlayers != null ) return maxPlayers;
		return Dungeons.getContext().defaultOptions.maxPlayers;
		
	}
	
	public String getMultiInvShare() {
		if( multiInvShare != null ) return multiInvShare;
		return Dungeons.getContext().defaultOptions.multiInvShare;
	}
	
	public void forceValues() {
		multiInvShare = null;
		maxPlayers = 1;  
		pearls = true;
		
	}
	
	public void loadFromConfig( FileConfiguration config, String branch ) {
		if( config.isInt( branch + ".max_players" ) ) maxPlayers = config.getInt( branch + ".max_players" );
		if( config.isBoolean( branch + ".ender_pearls" ) ) pearls = config.getBoolean( branch + ".ender_pearls" );
		ConfigurationSection sect = config.getConfigurationSection( branch + ".potions" );
		if( sect != null ) {
			Map<String,Object> map = sect.getValues(false);
 
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				String name = entry.getKey();
				
				PotionEffectType potion = PotionEffectType.getByName(name);
				if( potion == null ) {
					Dungeons.getContext().getLogger().info( "Unknown potion type in config: \""+name+"\"" );
				}
				potionMap.put( name, (Boolean)entry.getValue() );
				
			}
			
				
		}
		if( config.isString( branch + ".multiinv_share") ) multiInvShare = config.getString( branch + ".multiinv_share" );
		if( config.isList( branch + ".startup_commands" ) ) startupCommands = config.getStringList( branch + ".startup_commands" );
		if( config.isList( branch + ".unload_commands" ) ) unloadCommands = config.getStringList( branch + ".unload_commands" );
	}
	
	public void writeToConfig( YamlConfiguration config, String branch ) {
		
		if( maxPlayers != null ) config.set( branch + ".max_players", maxPlayers );
		if( pearls != null ) config.set( branch + ".ender_pearls", pearls );
		for (Map.Entry<String, Boolean> entry : potionMap.entrySet()) {
	 
			config.set( branch + ".potions." + entry.getKey(), entry.getValue() ? "yes":"no" );
			  
		}
		if( multiInvShare != null ) {
			config.set( branch + ".multiinv_share", multiInvShare );
		}
		
		if( startupCommands != null ) {
			config.set( branch + ".startup_commands", startupCommands );
		}
		if( unloadCommands != null ) {
			config.set( branch + ".unload_commands", unloadCommands );
		}
	}
	
	public List<String> getStartupCommands() {
		return startupCommands;
	}
	
	public List<String> getUnloadCommands() {
		return unloadCommands;
	}
}
