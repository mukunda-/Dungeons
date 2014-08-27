package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;

import com.mukunda.dungeons.Cuboid;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class ExitPortalCommand extends DungeonCommand {
	public ExitPortalCommand(  ) {
		super( "exit_portal", 0 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn exit_portal" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn exit_portal" );
		Commands.reply( sender, "Sets the exit portal for a dungeon to the current WorldEdit region, associated with the dungeon world you make the selection in." );
		Commands.reply( sender, "This is the inside area that teleports people to the exit point when they touch it." );
	}
	public void run( CommandSender sender, String[] args ) {
		Selection selection = CommandHelper.getWorldEditCuboidSelection( sender );
		if( selection == null ) return;
		
		DungeonConfig config = Dungeons.getContext().findConfig( selection.getWorld().getName() ); 
		if( config == null ) {
			Commands.reply( sender, "This world is not registered as a dungeon." );
			return;
		} 
		
		config.exitPortal = new Cuboid( selection );
		Commands.reply( sender, "Exit portal set." );
		config.save();
	}
}
