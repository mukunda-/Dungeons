package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;

import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;

public class ListCommand extends DungeonCommand {
	public ListCommand(  ) {
		super( "list", 0 ); 
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn list" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn list" );
		Commands.reply( sender, "Lists registered dungeons." );
	}
	public void run( CommandSender sender, String[] args ) {
 
		if( Dungeons.getContext().configs.size() == 0 ) {
			Commands.reply( sender, "There are no registered dungeons." );
			return;
		}
		
		for( DungeonConfig config : Dungeons.getContext().configs ) {
			Commands.reply( sender, config.name );
		}
		Commands.reply( sender, Dungeons.getContext().configs.size() + " total." );
	}
}
