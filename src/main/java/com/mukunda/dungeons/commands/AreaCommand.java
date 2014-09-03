package com.mukunda.dungeons.commands;
 

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;

import com.mukunda.dungeons.Cuboid;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class AreaCommand extends CommandHandler {
	public AreaCommand( CommandGroup parent ) {
		super( parent, "area", 1, true );
	}
	
	public void printSyntax() {
		reply( "/dgn area <name>" );
	}
	
	public void printUsage() {
		reply( "Usage: /dgn area <name>" );
		reply( "Sets the dungeon region to the current WorldEdit region." );
		reply( "The dungeon region is a cuboid that surrounds the entire dungeon." );
		reply( "It's used to determine which area of the world contains the dungeon structure." );
	}
	
	public void run( String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] );
		if( config == null ) return;

		Selection selection = CommandHelper.getWorldEditCuboidSelection( this );
		if( selection == null ) return;
		
		if( Dungeons.getContext().getServer().getWorld(config.name) != selection.getWorld() ){

			reply( "Selection is not in dungeon world." );
			return;
		}
		
		config.area = new Cuboid( selection );
		reply( "Area set." );
		config.save();
	}
}

