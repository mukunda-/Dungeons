package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;

public class EntryPointCommand extends DungeonCommand {
	public EntryPointCommand() {
		super( "entry_point", 1 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn entry_point" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn entry_point" );
		Commands.reply( sender, "Sets the entry point for a dungeon to your current location, associated with the dungeon world you are standing in." );
		Commands.reply( sender, "This is the inside location people teleport to when touching the entry portal." );
	}
	public void run( CommandSender sender, String[] args ) {
		Player player = (Player)sender;
		DungeonConfig config = Dungeons.getContext().findConfig( player.getWorld().getName() );
		if( config == null ) {
			Commands.reply( sender, "You aren't in a dungeon world." );
			return;
		}
		 
		config.entryPoint = player.getLocation().toVector();
		config.entryAngle = player.getLocation().getYaw();
		Commands.reply( sender, "Entry point set." ); 
		config.save();
	}
}
