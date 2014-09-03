package com.mukunda.dungeons.commands;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;


public class EnableCommand extends CommandHandler {
	public EnableCommand( CommandGroup parent ) {
		super( parent, "enable", 1, false );
	}
	public void printSyntax() {
		reply( "/dgn enable <name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn enable <name>" );
		reply( "Enables a dungeon. Use this when you are done with the configuration." ); 
	}
	public void run( String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] );
		if( config == null ) return;
		
		if( config.enabled ) {
			reply( "That dungeon is already enabled." );
			return;
		}
		if( !config.canEnable() ) {
			reply( "Cannot enable dungeon, configuration is not complete." );
			return;
		}
		reply( "Enabled dungeon." );
		config.enabled = true;
		config.save();
	}
}
