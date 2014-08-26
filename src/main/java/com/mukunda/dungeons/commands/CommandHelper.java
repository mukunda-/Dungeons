package com.mukunda.dungeons.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public final class CommandHelper {

	public static Selection getWorldEditCuboidSelection( CommandSender sender ){
		WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		Selection selection = worldEdit.getSelection( (Player)sender );
		if( selection == null ) {
			Commands.reply( sender, "You need to make a WorldEdit selection." );
			return null;
		}
		if( !(selection instanceof CuboidSelection) ){
			Commands.reply( sender, "You need to make a CUBOID selection." );
			return null;
		}
		return selection;
	}
	
	public static DungeonConfig getDungeonConfig( CommandSender sender, String name ) {
		DungeonConfig config = Dungeons.getContext().findConfig( name );
		if( config == null ) {
			Commands.reply( sender, "Config doesn't exist." );
		}
		return config;
		
	}
	
}
