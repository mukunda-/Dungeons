package com.mukunda.dungeons;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap; 
import java.util.HashSet;
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
	Location source;
	LootChestInfo info;
	private HashMap<UUID,Inventory> inventories = new HashMap<UUID,Inventory>();
	private HashSet<UUID> playerListWhenSpawned = new HashSet<UUID>();

	
	public static class InvalidLootException extends Exception {
		 
		private static final long serialVersionUID = 1L;

		public InvalidLootException(String message) {
			super(message);
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private static void placeChest( World world, Location location ) {
		Block block = world.getBlockAt(location);
		block.setType( Material.CHEST ); 
	}
	 
	
	public LootChest( Instance instance, LootChestInfo info ) throws InvalidLootException {
		this.instance = instance;
		this.info = info;
		
		
		World world = instance.getWorld();
		if( world == null ) throw new InvalidLootException( "World is not present." );
		Location source = new Location( 
				world, 
				info.sourcePoint.getBlockX(),
				info.sourcePoint.getBlockY(),
				info.sourcePoint.getBlockZ() );
		this.source = source;
		this.location = new Location(
				world,
				info.spawnPoint.getBlockX(),
				info.spawnPoint.getBlockY(),
				info.spawnPoint.getBlockZ() );
		
		Block block = source.getBlock();
		if( block == null ) throw new InvalidLootException( "Source chest is not present." );
		BlockState blockState = block.getState();
		if( !(blockState instanceof Chest) ) throw new InvalidLootException( "Source chest is not present." );
		//Chest sourceChest = (Chest)blockState; 
		
		List<Player> players = instance.getWorld().getPlayers();
		 
		placeChest( instance.getWorld(), location );
		// TODO effect
		
		for( Player player : players ) {
			 
			playerListWhenSpawned.add( player.getUniqueId() ); 
		}
		if( !info.giveAll ) {
			instance.setLocked();
		}
	}

	private void generateBooty( Inventory dest, Inventory source ) {
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
	
	public boolean openChest( Player player ) {
		Inventory inventory = inventories.get( player.getUniqueId() );
		if( inventory == null ) {
			if( !info.giveAll && !playerListWhenSpawned.contains(player.getUniqueId()) ) {
				// not giveall mode, only spawn loot for players who were present.
				return false;
			}
			
			Block block = source.getBlock();
			if( block == null ) {
				Dungeons.getContext().getLogger().warning( 
						"A loot chest is missing it's source. instance=" 
						+ instance.getWorld().getName() );
				return false; // the source loot was destroyed!
			}
			BlockState blockState = block.getState();
			if( blockState == null || !(blockState instanceof Chest) ) {
				Dungeons.getContext().getLogger().warning( 
						"A loot chest is missing it's source. instance=" 
						+ instance.getWorld().getName() );
				return false; // the source loot was destroyed!
			}
			
			Inventory booty = Bukkit.getServer().createInventory( null, 9, "Spoils" );
			generateBooty( booty, ((Chest)blockState).getInventory()  ); 
			inventories.put( player.getUniqueId(), booty );
			
			// player has accessed his loot, create dungeon lock
			Dungeons.getContext().userData.setCooldown( player, instance.getDungeon() );
			instance.setLockedPlayer( player );
			
			inventory = booty;
			
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
