package com.mukunda.dungeons.commands;
 
import org.bukkit.command.CommandSender;

import com.mukunda.dungeons.DungeonConfig;
 

public class InfoCommand extends DungeonCommand {
	public InfoCommand() {
		super( "info", 1 ); 
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn info <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn info <name>" );
		Commands.reply( sender, "Displays info about a dungeon." );
	}
	public void run( CommandSender sender, String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] ); 
		if( config == null ) return;
		Commands.reply( sender, "Info about \"" + config.name +"\"" );
		
		Commands.reply( sender, "Enabled: " + (config.enabled ? "True" : "False") );
		
		if( config.entryPortalWorld == null )
			Commands.reply( sender, "Entry portal not set." );
		else
			Commands.reply( sender, "Entry portal world: " + config.entryPortalWorld );
		
		if( config.exitPointWorld == null )
			Commands.reply( sender, "Exit point not set." );
		
		if( config.area == null ) 
			Commands.reply( sender, "Dungeon AREA not set." );
				
		if( config.options.getMaxPlayers() < 1 )
			Commands.reply( sender, "Dungeon max players is not set." );
		
	}
}
