package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.mukunda.dungeons.DungeonConfig;
 
public class TeleportCommand extends DungeonCommand {
	public TeleportCommand( ) {
		super( "tp", 1 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn tp <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn tp <name>" );
		Commands.reply( sender, "Teleports to a dungeon's exit point." );
	}
	public void run( CommandSender sender, String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] ); 
		if( config == null ) return;
		 
		if( config.exitPoint == null ) {
			Commands.reply( sender, "Exit point is not set." );
			return;
		}
		World world = Bukkit.getWorld(config.exitPointWorld);
		if( world == null ) {
			Commands.reply( sender, "Invalid world; can't teleport." );
			return;
		}
		
		Player player = (Player)sender;
		
		player.teleport( new Location( world, 
				config.exitPoint.getX(), 
				config.exitPoint.getY(), 
				config.exitPoint.getZ(),
				config.exitAngle, 
				player.getLocation().getPitch() ) );
	}
}