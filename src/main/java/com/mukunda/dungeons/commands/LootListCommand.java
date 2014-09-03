package com.mukunda.dungeons.commands;

import java.util.List;

import org.bukkit.entity.Player;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.mukunda.dungeons.LootChestInfo;

public class LootListCommand extends CommandHandler {
	public LootListCommand( CommandGroup parent ) {
		super( parent, "loot_list", 0, true );
	}
	
	public void printSyntax() {
		reply( "/dgn loot_list" );
	}
	
	public void printUsage() {
		reply( "Usage: /dgn loot_list" );
		reply( "Lists the loot chest links in the world you are standing in." );
	}

	public void run( String[] args ) { 
		
		Player player = getPlayer(); 
		
		DungeonConfig config = Dungeons.getContext().findConfig( player.getWorld().getName() );
		if( config == null ) {
			reply( "You aren't in a dungeon world." );
			return;
		}
		
		List<LootChestInfo> list = config.getLootChestList();
		reply( "Loot chest links in this world:" );
		for( LootChestInfo loot : list ) {
			reply( " - " +  loot.name );
		}
		
	}
	
	
}
