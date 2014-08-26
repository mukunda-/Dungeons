package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mukunda.dungeons.Cuboid;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.mukunda.dungeons.LootChestInfo;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class LootUnlinkCommand extends DungeonCommand {
	public LootUnlinkCommand() {
		super( "loot_unlink", 1 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn loot_unlink <chest name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn loot_unlink <chest name>" );
		Commands.reply( sender, "Deletes a loot chest link. You need to be standing in the dungeon world that contains it." );
	}
	
	
	
	public void run( CommandSender sender, String[] args ) { 
		
		Player player = (Player)sender; 
		
		DungeonConfig config = Dungeons.getContext().findConfig( player.getWorld().getName() );
		if( config == null ) {
			Commands.reply( sender, "You aren't in a dungeon world." );
			return;
		}
		
		String lootname = args[1];
		
		LootChestInfo loot = config.getLootChest( lootname );
		if( loot == null ) {
			Commands.reply( sender, "That loot chest doesn't exist." );
			return;
		}
		
		config.removeLootChest( loot );
		Commands.reply( sender, "Removed loot chest link \""+loot.name+"\"." );
		config.save();
	}
	
	
}
