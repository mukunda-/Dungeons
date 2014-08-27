package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;
import com.mukunda.dungeons.DungeonConfig;

public class DenizenKeyCommand extends DungeonCommand {
	public DenizenKeyCommand() {
		super( "denizen_key", 1 );
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn denizen_key <name> <key>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn denizen_key <name> [key]" );
		Commands.reply( sender, "Only players that have the Denizens flag [key] can enter the dungeon." );
	}
	public void run( CommandSender sender, String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] );
		if( config == null ) return;
		
		if( args.length < 3 ) {
			config.setDenizenKey(null);
			Commands.reply( sender, "Removed denizen key for \""+config.name+"\"." );			
		} else {
			config.setDenizenKey( args[2] );
			Commands.reply( sender, "Set denizen key for \""+config.name+"\" to \""+args[2]+"\"." );
		}
		config.save();
	}
}
