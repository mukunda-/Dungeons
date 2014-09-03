package com.mukunda.dungeons.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;

public class DeleteCommand extends CommandHandler {
	public DeleteCommand( CommandGroup parent ) {
		super( parent, "delete", 1, false );
	}
	public void printSyntax() {
		reply( "/dgn delete <name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn delete <name>" );
		reply( "Delete a dungeon configuration." );
	}
	public void run( String[] args ) {
		
		DungeonConfig c = Dungeons.getContext().findConfig( args[1] );
		if( c == null ) {
			reply( "Config doesn't exist." );
		}
		Dungeons.getContext().configs.remove(c);
		Path p = new File( Dungeons.getContext().getDataFolder(), "configs/" + args[1] + ".yml" ).toPath();
		try {
			if( Files.deleteIfExists( p ) ) {

				reply( "Deleted dungeon file: "+args[1]+".yml" );
			}
		} catch( IOException e ) {
			Dungeons.getContext().getLogger().info( "Couldn't delete dungeon file: " + args[1] + ".yml" );
		}
	}
}
