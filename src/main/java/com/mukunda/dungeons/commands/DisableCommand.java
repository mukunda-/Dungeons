package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;

import com.mukunda.dungeons.DungeonConfig;

public class DisableCommand extends DungeonCommand {
	public DisableCommand() {
		super( "disable", 1 );
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn disable <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn disable <name>" );
		Commands.reply( sender, "Disables a dungeon for maintenance. People will not be able to enter it." );
	}
	public void run( CommandSender sender, String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] );
		if( config == null ) return;
		
		if( !config.enabled )
			Commands.reply( sender, "That dungeon is already disabled." );
		else
			Commands.reply( sender, "Disabled dungeon." );
		config.enabled = false;
		config.save();
	}
}
