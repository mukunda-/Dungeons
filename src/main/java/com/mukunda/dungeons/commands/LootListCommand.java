package com.mukunda.dungeons.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.mukunda.dungeons.LootChestInfo;

public class LootListCommand extends DungeonCommand {
	public LootListCommand() {
		super( "loot_list", 0 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn loot_list" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn loot_list" );
		Commands.reply( sender, "Lists the loot chest links in the world you are standing in." );
	}

	public void run( CommandSender sender, String[] args ) { 
		
		Player player = (Player)sender; 
		
		DungeonConfig config = Dungeons.getContext().findConfig( player.getWorld().getName() );
		if( config == null ) {
			Commands.reply( sender, "You aren't in a dungeon world." );
			return;
		}
		
		List<LootChestInfo> list = config.getLootChestList();
		Commands.reply( sender, "Loot chest links in this world:" );
		for( LootChestInfo loot : list ) {
			Commands.reply( sender, " - " +  loot.name );
		}
		
	}
	
	
}
