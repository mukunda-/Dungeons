package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mukunda.dungeons.DungeonConfig;


public class ExitPointCommand extends DungeonCommand {
	public ExitPointCommand() {
		super( "exit_point", 1 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn exit_point <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn exit_point <name>" );
		Commands.reply( sender, "Sets the exit point for a dungeon to your current location." );
		Commands.reply( sender, "The exit point is the outside location where players are teleported when they quit the dungeon or touch the exit portal." );
	}
	public void run( CommandSender sender, String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] );
		if( config == null ) return;
		
		Player player = (Player)sender;
		
		config.exitPointWorld = player.getWorld().getName();
		config.exitPoint = player.getLocation().toVector();
		config.exitAngle = player.getLocation().getYaw();
		Commands.reply( sender, "Exit point set." );
		config.save();
	}
}
