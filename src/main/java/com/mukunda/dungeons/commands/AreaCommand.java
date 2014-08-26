package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender; 

import com.mukunda.dungeons.Cuboid;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class AreaCommand extends DungeonCommand {
	public AreaCommand() {
		super( "area", 1 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn area <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn area <name>" );
		Commands.reply( sender, "Sets the dungeon region to the current WorldEdit region." );
		Commands.reply( sender, "The dungeon region is a cuboid that surrounds the entire dungeon." );
		Commands.reply( sender, "It's used to determine which area of the world contains the dungeon structure." );
	}
	public void run( CommandSender sender, String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] );
		if( config == null ) return;

		Selection selection = CommandHelper.getWorldEditCuboidSelection( sender );
		if( selection == null ) return;
		
		if( Dungeons.getContext().getServer().getWorld(config.name) != selection.getWorld() ){

			Commands.reply( sender, "Selection is not in dungeon world." );
			return;
		}
		
		config.area = new Cuboid( selection );
		Commands.reply( sender, "Area set." );
		config.save();
	}
}

