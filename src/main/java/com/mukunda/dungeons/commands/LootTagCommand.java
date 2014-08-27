package com.mukunda.dungeons.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class LootTagCommand extends DungeonCommand {
	public LootTagCommand(  ) {
		super( "loot_tag", 1 );
		playerOnly=true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn loot_tag <chance>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /loot_tag <chance>" );
		Commands.reply( sender, "Gives you a loot tag item to place in a loot chest." );
		Commands.reply( sender, "<chance> is a number between 0 and 100 that determines the drop chance." );
		Commands.reply( sender, "You place the loot tag to the RIGHT of an item in a loot chest to tag it." );
	}
	@SuppressWarnings("deprecation")
	public void run( CommandSender sender, String[] args ) {
		Player player = (Player)sender; 
		
		float chance;
		try {
			chance = Float.parseFloat( args[1] );
			if( chance < 0.0 || chance > 100.0 ) {
				throw new NumberFormatException( "Number out of range." );
			}
		} catch( NumberFormatException e ) {
			Commands.reply( sender, "Invalid number." );
			return;
		}
		
		if( player.getInventory().firstEmpty() == -1 ) {
			Commands.reply( sender, "Your bag is full." );
			return;
		}
		
		ItemStack item = new ItemStack( Material.DEAD_BUSH );
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( String.format( "LOOT%.2f", chance ) );
		item.setItemMeta( meta );
		player.getInventory().addItem( item );
		
		// deprecated, but the only way to make this dumb shit work.
		player.updateInventory();

		Commands.reply( sender, "Loot tag created; drop chance: " + chance + "%." );
	}
}
