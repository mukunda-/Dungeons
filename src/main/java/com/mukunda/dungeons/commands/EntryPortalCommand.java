package com.mukunda.dungeons.commands;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.Cuboid;
import com.mukunda.dungeons.DungeonConfig;
import com.sk89q.worldedit.bukkit.selections.*;

public class EntryPortalCommand extends CommandHandler {
	public EntryPortalCommand( CommandGroup parent ) {
		super( parent, "entry_portal", 1, true );
	}
	public void printSyntax() {
		reply( "/dgn entry_portal <name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn entry_portal <name>" );
		reply( "Sets the entry portal for a dungeon to the current WorldEdit region." );
		reply( "This is the outside area that teleports people to the entry point when they touch it." );
	}
	
	
	
	public void run( String[] args ) { 
		
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] );
		if( config == null ) return;
		Selection selection = CommandHelper.getWorldEditCuboidSelection( this );
		if( selection == null ) return;
		
		config.entryPortal = new Cuboid( selection );
		config.entryPortalWorld = selection.getWorld().getName();
		reply( "Entry portal set." );
		config.save();
	}
	
	
}
