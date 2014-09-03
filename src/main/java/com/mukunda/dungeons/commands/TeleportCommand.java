package com.mukunda.dungeons.commands;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
 
public class TeleportCommand extends CommandHandler {
	public TeleportCommand( CommandGroup parent ) {
		super( parent, "tp", 1, true );
	}
	public void printSyntax() {
		reply( "/dgn tp <name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn tp <name>" );
		reply( "Teleports to a dungeon's exit point." );
	}
	public void run( String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] ); 
		if( config == null ) return;
		 
		if( config.exitPoint == null ) {
			reply( "Exit point is not set." );
			return;
		}
		World world = Bukkit.getWorld(config.exitPointWorld);
		if( world == null ) {
			reply( "Invalid world; can't teleport." );
			return;
		}
		
		Player player = getPlayer();
		
		player.teleport( new Location( world, 
				config.exitPoint.getX(), 
				config.exitPoint.getY(), 
				config.exitPoint.getZ(),
				config.exitAngle, 
				player.getLocation().getPitch() ) );
	}
}