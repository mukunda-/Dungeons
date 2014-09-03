package com.mukunda.dungeons.commands;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;

public class ListCommand extends CommandHandler {
	public ListCommand( CommandGroup parent ) {
		super( parent, "list", 0, false ); 
	}
	public void printSyntax() {
		reply( "/dgn list" );
	}
	public void printUsage() {
		reply( "Usage: /dgn list" );
		reply( "Lists registered dungeons." );
	}
	public void run( String[] args ) {
 
		if( Dungeons.getContext().configs.size() == 0 ) {
			reply( "There are no registered dungeons." );
			return;
		}
		
		for( DungeonConfig config : Dungeons.getContext().configs ) {
			reply( config.name );
		}
		reply( Dungeons.getContext().configs.size() + " total." );
	}
}
