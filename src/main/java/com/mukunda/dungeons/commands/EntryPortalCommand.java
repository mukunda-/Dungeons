package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender; 
  

import com.mukunda.dungeons.Cuboid;
import com.mukunda.dungeons.DungeonConfig;
import com.sk89q.worldedit.bukkit.selections.*;

public class EntryPortalCommand extends DungeonCommand {
	public EntryPortalCommand() {
		super( "entry_portal", 1 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn entry_portal <name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn entry_portal <name>" );
		Commands.reply( sender, "Sets the entry portal for a dungeon to the current WorldEdit region." );
		Commands.reply( sender, "This is the outside area that teleports people to the entry point when they touch it." );
	}
	
	
	
	public void run( CommandSender sender, String[] args ) { 
		
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] );
		if( config == null ) return;
		Selection selection = CommandHelper.getWorldEditCuboidSelection( sender );
		if( selection == null ) return;
		
		config.entryPortal = new Cuboid( selection );
		config.entryPortalWorld = selection.getWorld().getName();
		Commands.reply( sender, "Entry portal set." );
		config.save();
	}
	
	
}
