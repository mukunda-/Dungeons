package com.mukunda.dungeons.commands;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;

public class LootTagCommand extends CommandHandler {
	public LootTagCommand( CommandGroup parent ) {
		super( parent, "loot_tag", 1, true );
	}
	
	public void printSyntax() {
		reply( "/dgn loot_tag <chance>" );
	}
	
	public void printUsage() {
		reply( "Usage: /loot_tag <chance>" );
		reply( "Gives you a loot tag item to place in a loot chest." );
		reply( "<chance> is a number between 0 and 100 that determines the drop chance." );
		reply( "You place the loot tag to the RIGHT of an item in a loot chest to tag it." );
	}
	@SuppressWarnings("deprecation")
	public void run( String[] args ) {
		Player player = getPlayer(); 
		
		float chance;
		try {
			chance = Float.parseFloat( args[1] );
			if( chance < 0.0 || chance > 100.0 ) {
				throw new NumberFormatException( "Number out of range." );
			}
		} catch( NumberFormatException e ) {
			reply( "Invalid number." );
			return;
		}
		
		if( player.getInventory().firstEmpty() == -1 ) {
			reply( "Your bag is full." );
			return;
		}
		
		ItemStack item = new ItemStack( Material.DEAD_BUSH );
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName( String.format( "LOOT%.2f", chance ) );
		item.setItemMeta( meta );
		player.getInventory().addItem( item );
		
		// deprecated, but the only way to make this dumb shit work.
		player.updateInventory();

		reply( "Loot tag created; drop chance: " + chance + "%." );
	}
}
