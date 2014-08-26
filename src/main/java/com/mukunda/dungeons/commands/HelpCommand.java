package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;

import com.mukunda.dungeons.Dungeons;

public class HelpCommand extends DungeonCommand {
	public HelpCommand() {
		super( "help", 1 );
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn help <command>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn help <command>" );
		Commands.reply( sender, "Prints help for a command." );
	}
	public void run( CommandSender sender, String[] args ) {
	
		DungeonCommand c = Dungeons.getContext().commands.get( args[1] );
		if( c == null ) {
			Commands.reply( sender, "Unknown command: \"" + args[1].toLowerCase() + "\"" );
			return;
		}
		
		c.printUsage( sender );
	}
}