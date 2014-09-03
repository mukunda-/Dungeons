package com.mukunda.dungeons.commands;

import org.bukkit.entity.Player;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.mukunda.dungeons.LootChestInfo;

public class LootUnlinkCommand extends CommandHandler {
	public LootUnlinkCommand( CommandGroup parent ) {
		super( parent, "loot_unlink", 1, true );
	}
	
	public void printSyntax() {
		reply( "/dgn loot_unlink <chest name>" );
	}
	
	public void printUsage() {
		reply( "Usage: /dgn loot_unlink <chest name>" );
		reply( "Deletes a loot chest link. You need to be standing in the dungeon world that contains it." );
	}
	
	
	
	public void run( String[] args ) { 
		
		Player player = getPlayer(); 
		
		DungeonConfig config = Dungeons.getContext().findConfig( player.getWorld().getName() );
		if( config == null ) {
			reply( "You aren't in a dungeon world." );
			return;
		}
		
		String lootname = args[1];
		
		LootChestInfo loot = config.getLootChest( lootname );
		if( loot == null ) {
			reply( "That loot chest doesn't exist." );
			return;
		}
		
		config.removeLootChest( loot );
		reply( "Removed loot chest link \""+loot.name+"\"." );
		config.save();
	}
	
	
}
