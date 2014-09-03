package com.mukunda.dungeons.commands;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;

public class InfoCommand extends CommandHandler {
	public InfoCommand( CommandGroup parent ) {
		super( parent, "info", 1, false ); 
	}
	public void printSyntax() {
		reply( "/dgn info <name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn info <name>" );
		reply( "Displays info about a dungeon." );
	}
	public void run( String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] ); 
		if( config == null ) return;
		reply( "Info about \"" + config.name +"\"" );
		
		reply( "Enabled: " + (config.enabled ? "True" : "False") );
		
		if( config.entryPortalWorld == null )
			reply( "Entry portal not set." );
		else
			reply( "Entry portal world: " + config.entryPortalWorld );
		
		if( config.exitPointWorld == null )
			reply( "Exit point not set." );
		
		if( config.area == null ) 
			reply( "Dungeon AREA not set." );
				
		if( config.options.getMaxPlayers() < 1 )
			reply( "Dungeon max players is not set." );
		
	}
}
