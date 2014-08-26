package com.mukunda.dungeons.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.bukkit.command.CommandSender;

import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;

public class DeleteCommand extends DungeonCommand {
	public DeleteCommand( ) {
		super( "delete", 1 );
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn delete <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn delete <name>" );
		Commands.reply( sender, "Delete a dungeon configuration." );
	}
	public void run( CommandSender sender, String[] args ) {
		
		DungeonConfig c = Dungeons.getContext().findConfig( args[1] );
		if( c == null ) {
			Commands.reply( sender, "Config doesn't exist." );
		}
		Dungeons.getContext().configs.remove(c);
		Path p = new File( Dungeons.getContext().getDataFolder(), "configs/" + args[1] + ".yml" ).toPath();
		try {
			if( Files.deleteIfExists( p ) ) {

				Commands.reply( sender, "Deleted dungeon file: "+args[1]+".yml" );
			}
		} catch( IOException e ) {
			Dungeons.getContext().getLogger().info( "Couldn't delete dungeon file: " + args[1] + ".yml" );
		}
	}
}
