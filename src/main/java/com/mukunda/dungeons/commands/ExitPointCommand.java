package com.mukunda.dungeons.commands;

import org.bukkit.entity.Player;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;


public class ExitPointCommand extends CommandHandler {
	public ExitPointCommand( CommandGroup parent ) {
		super( parent, "exit_point", 1, true );
	}
	public void printSyntax() {
		reply( "/dgn exit_point <name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn exit_point <name>" );
		reply( "Sets the exit point for a dungeon to your current location." );
		reply( "The exit point is the outside location where players are teleported when they quit the dungeon or touch the exit portal." );
	}
	public void run( String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] );
		if( config == null ) return;
		
		Player player = getPlayer();
		
		config.exitPointWorld = player.getWorld().getName();
		config.exitPoint = player.getLocation().toVector();
		config.exitAngle = player.getLocation().getYaw();
		reply( "Exit point set." );
		config.save();
	}
}
