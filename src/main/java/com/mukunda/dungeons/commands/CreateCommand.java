package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.World;

import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;

public class CreateCommand extends DungeonCommand {
	public CreateCommand( ) {
		super( "create", 1 );
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn create <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn create <name>" );
		Commands.reply( sender, "Create a dungeon configuration, which allows a dungeon to be instanced." );
		Commands.reply( sender, "NAME must match a loaded world name which contains the dungeon." ); 
	}
	public void run( CommandSender sender, String[] args ) {
		if( Dungeons.getContext().findConfig( args[1] ) != null ) {
			Commands.reply( sender, "Config already exists." );
			return;
		}
		World world = Dungeons.getContext().getServer().getWorld( args[1] ); 
		if( world == null ) { 
			Commands.reply( sender, "NAME must match a world name." );
			Commands.reply( sender, "The world must be loaded to create a configuration." );
			return;
		}
		
		DungeonConfig c = new DungeonConfig(); 
		c.name = args[1];
		Dungeons.getContext().configs.add( c );
		
		Commands.reply( sender, "Config created for: \"" + args[1] + "\"" );
	}
}
