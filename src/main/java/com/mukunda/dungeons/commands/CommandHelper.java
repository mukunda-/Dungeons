package com.mukunda.dungeons.commands;

import org.bukkit.Bukkit;  

import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public final class CommandHelper {

	public static Selection getWorldEditCuboidSelection( CommandHandler source ){
		WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		Selection selection = worldEdit.getSelection( source.getPlayer() );
		if( selection == null ) {
			source.reply( "You need to make a WorldEdit selection." );
			return null;
		}
		if( !(selection instanceof CuboidSelection) ){
			source.reply( "You need to make a CUBOID selection." );
			return null;
		}
		return selection;
	}
	
	public static DungeonConfig getDungeonConfig( CommandHandler source, String name ) {
		DungeonConfig config = Dungeons.getContext().findConfig( name );
		if( config == null ) {
			source.reply( "Config doesn't exist." );
		}
		return config;
		
	}
	
}
