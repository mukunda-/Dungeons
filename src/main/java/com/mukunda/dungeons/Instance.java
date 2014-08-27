package com.mukunda.dungeons;
 
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World; 
import org.bukkit.entity.Player;
 
 


import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import com.mukunda.parties.Party;  
import com.onarandombox.MultiverseCore.MultiverseCore; 
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import uk.co.tggl.pluckerpluck.multiinv.MultiInv;
import uk.co.tggl.pluckerpluck.multiinv.MIYamlFiles;

//--------------------------------------------------------------------------------------
public class Instance {
	private Party party;
	private DungeonConfig dungeon;
	private long creationTime;
	private long accessTime;
	private boolean dead;
	private boolean setup; 
	private boolean unloadCommandsRun;
	
	private ArrayList<LootChest> lootChests = new ArrayList<LootChest>();
	
	private HashSet<UUID> lockedPlayers = new HashSet<UUID>();
	private boolean locked;

	public String instanceWorld;
	
	public World getWorld() {
		return Bukkit.getWorld(instanceWorld);
	}
	
	public DungeonConfig getDungeon() {
		return dungeon;
	}
	
	public Party getParty() {
		return party;
	}
	
	public boolean isSettingUp() {
		return setup;
	}
	/*
	//--------------------------------------------------------------------------------------
	class CopyResult extends WorldCopier.WorldCopyCallback {
		public void run() {
			instanceWorld = newWorld.toString();
			if( failed ) {
				Player player = Bukkit.getPlayer( starter );
				if( player != null ) {
					player.sendMessage( "This dungeon is currently unavailable." );
				}
				cleanup();
				return;
			}
			Bukkit.broadcastMessage( "test copy complete" );
			
			setupDungeon();
		}
	} */
	
	//--------------------------------------------------------------------------------------
	public Instance( Player starter, Party party, DungeonConfig dungeon ) {
	
		dead = false;
		creationTime = System.currentTimeMillis();
		accessTime = creationTime;
		this.dungeon = dungeon;
		this.party = party;
		this.setup = true; 
		
		
		Path instancepath;
		do {
			instancepath = Paths.get(
				Dungeons.getContext().getServer().getWorldContainer().toString(),
				"instances",
				dungeon.name + "-" + ((int)(Math.random() * 99999)) );
			
		} while( Files.exists(instancepath) ); 
		instanceWorld = Dungeons.getContext().getServer().getWorldContainer().toPath().relativize( instancepath ).toString(); 
		instanceWorld = instanceWorld.replace( '\\', '/' ); /// keep slashes in dungeon names consistent.
		
		MultiverseCore mv = (MultiverseCore)Bukkit.getPluginManager().getPlugin( "Multiverse-Core" );
		
		// make sure world is unloaded
		mv.getMVWorldManager().unloadWorld( dungeon.name, true );
		mv.getMVWorldManager().removePlayersFromWorld( dungeon.name );
		
		// copy worldguard config
		WorldGuardPlugin wg = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin( "WorldGuard" );
		if( wg != null ) {
			
			File wgConfigFolder = new File( wg.getDataFolder(), "worlds/" + dungeon.name );
			
			if( wgConfigFolder.isDirectory() ) {
				// copy world guard directory
				Dungeons.getContext().getLogger().fine( "Copying WorldGuard config: " + instanceWorld );
				try {
					org.apache.commons.io.FileUtils.copyDirectory( 
							wgConfigFolder, 
							new File( wg.getDataFolder(), "worlds/" + instanceWorld ) );
				} catch( IOException e ) {
					Dungeons.getContext().getLogger().warning( "Could not clone dungeon instance WorldGuard settings. " + instanceWorld );
					starter.sendMessage( "This dungeon is currently unavailable." );
					
					cleanup();
					return;
				}
			}
		}
	    
		if( !mv.cloneWorld( dungeon.name, instanceWorld, null ) ) {
			Dungeons.getContext().getLogger().warning( "Could not clone dungeon instance. " + instanceWorld );
			starter.sendMessage( "This dungeon is currently unavailable." );
			
			cleanup();
			return;
		}
		
		MultiInv mi = (MultiInv)Bukkit.getPluginManager().getPlugin( "MultiInv" );
		if( mi != null ) {
			String share = dungeon.options.getMultiInvShare();
			if( share != null ) {
				MIYamlFiles.getGroups().put( instanceWorld, share );
			}
		}
		
		
		
		setup = false;
		Dungeons.getContext().instances.add( this ); 
		Dungeons.getContext().instanceMap.put( instanceWorld, this ); 
		
		// teleport party members that are touching the portal
		teleportParty( party );
		//WorldCopier.copy( dungeon.name, new CopyResult() );
		
		//cleanup();
		
		// run startup commands
		 
		runCommandList( Dungeons.getContext().startupCommands );
		runCommandList( dungeon.options.getStartupCommands() );
		
	}

	//--------------------------------------------------------------------------------------
	private void dispatchCommand( String cmd ) {
		cmd = cmd.replace( "{{WORLD}}", instanceWorld ); 
		Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), cmd );
	}
	
	private void runCommandList( List<String> cmds ) {
		if( cmds == null ) return;
		
		for( String cmd: cmds ) {
			dispatchCommand( cmd );
		}
	}
	 
	
	
	//--------------------------------------------------------------------------------------
	public void checkPotions( Player player ) {
		Collection<PotionEffect> effects = player.getActivePotionEffects();
		for( PotionEffect effect: effects ) {
			if( dungeon.options.isPotionAllowed( effect.getType().getName() ) == false ) {
				player.removePotionEffect( effect.getType() );
			}
		}
	}
	
	//--------------------------------------------------------------------------------------
	public void checkPotionsDelayed( Player player, long delay ) {
		new CheckPotionsTask( player ).runTaskLater( Dungeons.getContext(), delay );
	 
	}
	
	private class CheckPotionsTask extends BukkitRunnable {
		
		private UUID id; 
		
		public CheckPotionsTask( Player player ) {
			id = player.getUniqueId(); 
		}
		
		public void run() {
			Player player= Bukkit.getPlayer( id );
			if( player == null ) return;
			if( !player.getWorld().getName().equals(instanceWorld) ) return;
			
			checkPotions(player);
		}
	}
	
	//--------------------------------------------------------------------------------------
	public void teleportPlayerEntry( Player player ) {
		World world = Bukkit.getWorld( instanceWorld );
		if( world == null ) return;
		
		// if the dungeon is full, teleport him outside 
		// -- we're not sure what called this teleport command
		//    so we should still teleport them somewhere
		if( world.getPlayers().size() >= dungeon.options.getMaxPlayers() ) {
			player.sendMessage( ChatColor.BLUE + "That dungeon is full." );
			
			teleportPlayerExit( player );
			return;
		}
		player.teleport( 
				new Location( getWorld(), 
						dungeon.entryPoint.getX(), 
						dungeon.entryPoint.getY(), 
						dungeon.entryPoint.getZ(),
						dungeon.entryAngle,
						player.getLocation().getPitch() ));
		
		checkPotions( player );
		
	}
	
	//--------------------------------------------------------------------------------------
	public void teleportPlayerExit( Player player ) {
		player.teleport( 
				new Location( Bukkit.getWorld( dungeon.exitPointWorld ), 
						dungeon.exitPoint.getX(), 
						dungeon.exitPoint.getY(), 
						dungeon.exitPoint.getZ(),
						dungeon.exitAngle,
						player.getLocation().getPitch() ));
		
	}
	
	//--------------------------------------------------------------------------------------
	private void teleportParty( Party party ) {
		for( UUID pid : party.players ) {
			Player p = Bukkit.getPlayer( pid );
			if( p != null ) {
				if( p.getWorld().getName().equals( dungeon.entryPortalWorld )  &&  
						dungeon.entryPortal.isPlayerTouching( p.getLocation() ) ) {
					
					teleportPlayerEntry( p );
				}
			}
		}
	}
	/*
	private void setupDungeon() {
		// copy mv config
		// copy ???
		// copy ???
		 
	}*/
	
	public boolean closing() {
		return dead;
	}
	
	//--------------------------------------------------------------------------------------
	public boolean cleanup() {
		if( !dead ) {
			setup = false;
			dead = true;
			
			if( !unloadCommandsRun ) {
				unloadCommandsRun = true;
				runCommandList( Dungeons.getContext().unloadCommands );
				runCommandList( dungeon.options.getUnloadCommands() );
				
			}

			MultiverseCore mv = (MultiverseCore)Bukkit.getPluginManager().getPlugin( "Multiverse-Core" );
			mv.getMVWorldManager().removePlayersFromWorld( instanceWorld );
			
			// DELAY this to let other things handle chunk unload shit.
			/*
						Bukkit.getScheduler().runTaskLater( Dungeons.getContext(), new Runnable() {
							public void run () {
								
								// hopefully this succeeds, if not, they will be cleaned up at the next server restart.
								MultiverseCore mv = (MultiverseCore)Bukkit.getPluginManager().getPlugin( "Multiverse-Core" );
								mv.deleteWorld( instanceWorld );
								
							}
						}, 200 );
*/
			
			if( !mv.deleteWorld( instanceWorld ) ) {
				dead = false;
				return false;
			}
			Dungeons.getContext().instances.remove( this );
			Dungeons.getContext().instanceMap.remove( instanceWorld );
			
			Bukkit.broadcastMessage( "DEBUG FOLDER DELETE PATH=" + instanceWorld );


			// delete world guard config
			WorldGuardPlugin wg = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin( "WorldGuard" );
			if( wg != null ) {
				
				File wgConfigFolder = new File( wg.getDataFolder(), "worlds/" + instanceWorld );
				if( wgConfigFolder.isDirectory() ) {
					// copy world guard directory

					Dungeons.getContext().getLogger().fine( "Deleting WorldGuard config: " + instanceWorld );
					try {
						org.apache.commons.io.FileUtils.deleteDirectory( wgConfigFolder );
						
					} catch( IOException e ) {
						Dungeons.getContext().getLogger().warning( "Could not delete WorldGuard config folder: " + instanceWorld );
						
					}
				}
			}
			
			
			return true;
		}
		return false;
	}
	
	//--------------------------------------------------------------------------------------
	public boolean tryCleanup() {
		if( setup ) return false;
		if( dead ) return false;
		World world = Bukkit.getWorld( instanceWorld );
		if( world != null ) {
			if( !world.getPlayers().isEmpty() ) {
				return false;
			}
		}
		if( party.stale ) {
			return cleanup();
		}
		// TODO DEBUG short time
		if( System.currentTimeMillis() > accessTime + 1000*30) {//(15*60*1000) ) {
			// 15 minutes: cleanup
			return cleanup();
		}
		return false;
	}
	
	//--------------------------------------------------------------------------------------
	public void refreshAccessTime() {
		accessTime = System.currentTimeMillis();
	}
	
	//--------------------------------------------------------------------------------------
	public void addLootChest( LootChest loot ) {
		lootChests.add(loot);
	}
	
	//--------------------------------------------------------------------------------------
	public LootChest findLootChest( Location location ) {
		for( LootChest loot : lootChests ) {
			if( loot.isAtLocation(location) ) return loot;
		}
		return null;
	}
	
	public void setLockedPlayer( Player player ) {
		lockedPlayers.add(player.getUniqueId());
	}
	
	public void setLocked() {
		locked = true;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public boolean isPlayerLocked( Player player ) {
		return lockedPlayers.contains( player.getUniqueId() );
	}
}
