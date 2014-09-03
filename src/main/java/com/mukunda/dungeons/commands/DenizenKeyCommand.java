package com.mukunda.dungeons.commands;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;

public class DenizenKeyCommand extends CommandHandler {
	public DenizenKeyCommand( CommandGroup parent ) {
		super( parent, "denizen_key", 1, false );
	}
	public void printSyntax() {
		reply( "/dgn denizen_key <name> <key>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn denizen_key <name> [key]" );
		reply( "Only players that have the Denizens flag [key] can enter the dungeon." );
	}
	public void run( String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] );
		if( config == null ) return;
		
		if( args.length < 3 ) {
			config.setDenizenKey(null);
			reply( "Removed denizen key for \""+config.name+"\"." );			
		} else {
			config.setDenizenKey( args[2] );
			reply( "Set denizen key for \""+config.name+"\" to \""+args[2]+"\"." );
		}
		config.save();
	}
}
