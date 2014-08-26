package com.mukunda.dungeons;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap; 
import java.util.List;
import java.util.UUID; 

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

//-------------------------------------------------------------------------------------------------
public class LootChest {
	Instance instance;
	Location location;
	private HashMap<UUID,Inventory> inventories = new HashMap<UUID,Inventory>();
	
	//-------------------------------------------------------------------------------------------------
	private static void placeChest( World world, Location location ) {
		Block block = world.getBlockAt(location);
		block.setType( Material.CHEST ); 
	}
	
	//-------------------------------------------------------------------------------------------------
	public static LootChest create( Instance instance, Location source, Location dest ) {
		
		World world = instance.getWorld();
		if( world == null ) return null;
		Block block = source.getBlock();
		if( block == null ) return null;
		BlockState blockState = block.getState();
		if( !(blockState instanceof Chest) ) return null;
		Chest sourceChest = (Chest)blockState; 
		
		return new LootChest( instance, dest, sourceChest.getInventory() );
		
	}
	
	private void loadInventory( Inventory dest, Inventory source ) {
		// getSize()-1 because we are checking two entries together
		for( int i = 0; i < source.getSize()-1; i++ ) {
			
			ItemStack item = source.getItem(i);
			if( item == null || item.getType() == Material.AIR ) continue;
			
			String name = item.getItemMeta().getDisplayName();
			if( name != null && item.getType() == Material.DEAD_BUSH && name.substring( 0, 4 ).equals("LOOT") ){
				// loot item, skip
				continue;
			} 
			 
			 
			ItemStack tag = source.getItem(i+1);
			if( tag == null || tag.getType() == Material.AIR ) continue;
			name = tag.getItemMeta().getDisplayName();
			if( name == null || tag.getType() != Material.DEAD_BUSH || (!name.substring( 0, 4 ).equals("LOOT")) ){
				// no loot tag, skip item.
				continue;
			}
			i++;
			
			float chance;
			try {
				chance = Float.parseFloat( name.substring( 4 ) );
			} catch (NumberFormatException e ) {
				chance = 0;
			}
			
			if( Math.random() * 100.0 <= chance ) {
				dest.addItem( item.clone() ); 
			}
			
		}
	}
	
	private LootChest( Instance instance, Location location, Inventory source ) {
		this.instance = instance;
		this.location = location;
		
		List<Player> players = instance.getWorld().getPlayers();
		 
		placeChest( instance.getWorld(), location );
		// TODO effect
		
		for( Player player : players ) {
			Inventory booty = Bukkit.getServer().createInventory( null, 9, "Spoils" );
			loadInventory( booty, source  ); 
			inventories.put( player.getUniqueId(), booty );
			
			// loot was generated for player, create dungeon lock
			Dungeons.getContext().userData.setCooldown( player, instance.getDungeon() );
		}
	}
	
	public boolean openChest( Player player ) {
		Inventory inventory = inventories.get( player.getUniqueId() );
		if( inventory == null ) {
			return false;
		}
		player.openInventory( inventory );
		  
		location.getWorld().playSound( location, Sound.CHEST_OPEN, 0.7f, 1.0f );
		
		//chest.getInventory().
		
		final UUID uid = player.getUniqueId();
		
		Bukkit.getScheduler().runTaskLater( Dungeons.getContext(), new Runnable() {
			 
			public void run() {
				Player player = Bukkit.getPlayer( uid );
				if( player == null ) return;
				
				ProtocolManager manager = ProtocolLibrary.getProtocolManager();
				
				PacketContainer packet = new PacketContainer( PacketType.Play.Server.BLOCK_ACTION );
				packet.getIntegers().write( 0, location.getBlockX() );
				packet.getIntegers().write( 1, location.getBlockY() );
				packet.getIntegers().write( 2, location.getBlockZ() );
				packet.getIntegers().write( 3, 1 );
				packet.getIntegers().write( 4, 1 ); 
				packet.getBlocks().write( 0, Material.CHEST );
				try {
					
					manager.sendServerPacket( player, packet );
					for( Player observer : manager.getEntityTrackers( player ) ) {
						manager.sendServerPacket( observer, packet );
					}
				} catch( InvocationTargetException e ) {
					// ohwell
				}
				
				
			}
		}, 3 );
			
		return true;
		
	}

	public boolean isAtLocation( Location location ) {
		if( this.location.equals(location) ) return true;
		return false;
	}
}
