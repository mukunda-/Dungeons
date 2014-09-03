package com.mukunda.dungeons.commands;

import org.bukkit.World;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;

public class CreateCommand extends CommandHandler {
	public CreateCommand( CommandGroup parent ) {
		super( parent, "create", 1, false );
	}
	public void printSyntax() {
		reply( "/dgn create <name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn create <name>" );
		reply( "Create a dungeon configuration, which allows a dungeon to be instanced." );
		reply( "NAME must match a loaded world name which contains the dungeon." ); 
	}
	public void run( String[] args ) {
		if( Dungeons.getContext().findConfig( args[1] ) != null ) {
			reply( "Config already exists." );
			return;
		}
		World world = Dungeons.getContext().getServer().getWorld( args[1] ); 
		if( world == null ) { 
			reply( "NAME must match a world name." );
			reply( "The world must be loaded to create a configuration." );
			return;
		}
		
		DungeonConfig c = new DungeonConfig(); 
		c.name = args[1];
		Dungeons.getContext().configs.add( c );
		
		reply( "Config created for: \"" + args[1] + "\"" );
	}
}
