package com.mukunda.dungeons.commands;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;

public class DisableCommand extends CommandHandler {
	public DisableCommand( CommandGroup parent ) {
		super( parent, "disable", 1, false );
	}
	public void printSyntax() {
		reply( "/dgn disable <name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn disable <name>" );
		reply( "Disables a dungeon for maintenance. People will not be able to enter it." );
	}
	public void run( String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] );
		if( config == null ) return;
		
		if( !config.enabled )
			reply( "That dungeon is already disabled." );
		else
			reply( "Disabled dungeon." );
		config.enabled = false;
		config.save();
	}
}
