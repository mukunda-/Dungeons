package com.mukunda.dungeons.commands;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.Cuboid;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ExitPortalCommand extends CommandHandler {
	public ExitPortalCommand( CommandGroup parent ) {
		super( parent, "exit_portal", 0, true );
	}
	public void printSyntax() {
		reply( "/dgn exit_portal" );
	}
	public void printUsage() {
		reply( "Usage: /dgn exit_portal" );
		reply( "Sets the exit portal for a dungeon to the current WorldEdit region, "+
				"associated with the dungeon world you make the selection in." );
		reply( "This is the inside area that teleports people to the exit point when they touch it." );
	}
	public void run( String[] args ) {
		Selection selection = CommandHelper.getWorldEditCuboidSelection( this );
		if( selection == null ) return;
		
		DungeonConfig config = Dungeons.getContext().findConfig( selection.getWorld().getName() ); 
		if( config == null ) {
			reply( "This world is not registered as a dungeon." );
			return;
		} 
		
		config.exitPortal = new Cuboid( selection );
		reply( "Exit portal set." );
		config.save();
	}
}
