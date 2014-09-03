package com.mukunda.dungeons.commands;

import org.bukkit.entity.Player;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;

public class EntryPointCommand extends CommandHandler {
	public EntryPointCommand( CommandGroup parent ) {
		super( parent, "entry_point", 1, true );
	}
	public void printSyntax() {
		reply( "/dgn entry_point" );
	}
	public void printUsage() {
		reply( "Usage: /dgn entry_point" );
		reply( "Sets the entry point for a dungeon to your current location, associated with the dungeon world you are standing in." );
		reply( "This is the inside location people teleport to when touching the entry portal." );
	}
	public void run( String[] args ) {
		Player player = getPlayer();
		DungeonConfig config = Dungeons.getContext().findConfig( player.getWorld().getName() );
		if( config == null ) {
			reply( "You aren't in a dungeon world." );
			return;
		}
		 
		config.entryPoint = player.getLocation().toVector();
		config.entryAngle = player.getLocation().getYaw();
		reply( "Entry point set." ); 
		config.save();
	}
}
