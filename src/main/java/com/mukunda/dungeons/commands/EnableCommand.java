package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;

import com.mukunda.dungeons.DungeonConfig;


public class EnableCommand extends DungeonCommand {
	public EnableCommand() {
		super( "enable", 1 );
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn enable <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn enable <name>" );
		Commands.reply( sender, "Enables a dungeon. Use this when you are done with the configuration." ); 
	}
	public void run( CommandSender sender, String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] );
		if( config == null ) return;
		
		if( config.enabled ) {
			Commands.reply( sender, "That dungeon is already enabled." );
			return;
		}
		if( !config.canEnable() ) {
			Commands.reply( sender, "Cannot enable dungeon, configuration is not complete." );
			return;
		}
		Commands.reply( sender, "Enabled dungeon." );
		config.enabled = true;
		config.save();
	}
}
