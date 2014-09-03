package com.mukunda.dungeons;
 
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin; 
import org.bukkit.util.Vector;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path; 
import java.nio.file.FileVisitResult;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
  









import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.dungeons.commands.AreaCommand;
import com.mukunda.dungeons.commands.Commands;
import com.mukunda.dungeons.commands.CooldownCommand;
import com.mukunda.dungeons.commands.CreateCommand;
import com.mukunda.dungeons.commands.DeleteCommand;
import com.mukunda.dungeons.commands.DenizenKeyCommand;
import com.mukunda.dungeons.commands.DisableCommand;
import com.mukunda.dungeons.commands.EnableCommand;
import com.mukunda.dungeons.commands.EntryPointCommand;
import com.mukunda.dungeons.commands.EntryPortalCommand;
import com.mukunda.dungeons.commands.ExitPointCommand;
import com.mukunda.dungeons.commands.ExitPortalCommand;
import com.mukunda.dungeons.commands.HelpCommand;
import com.mukunda.dungeons.commands.InfoCommand;
import com.mukunda.dungeons.commands.ListCommand;
import com.mukunda.dungeons.commands.LootChestCommand;
import com.mukunda.dungeons.commands.LootListCommand;
import com.mukunda.dungeons.commands.LootTagCommand;
import com.mukunda.dungeons.commands.LootUnlinkCommand;
import com.mukunda.dungeons.commands.TeleportCommand;
import com.mukunda.parties.Parties;
import com.mukunda.parties.Party;
import com.mukunda.parties.PartyEvent;
import com.mukunda.parties.PartyTryLeaveEvent;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

// TODO:
// respawn at dungeon start DUN NEED TEST
// cooldowns DUN NEED TEST
// block chunk unloading DONE 
// max players DONE,NEEDSTEST
// npc cleanup, denizen script? DUN
// allow teleport commands, and instead just check if they can enter an instance, and redirect them to entrance. DUN NEED TEST
// KEYS DUN NEED TEST
// slow down gnome
// remove debug shit

public final class Dungeons extends JavaPlugin implements Listener {
	
	public HashSet<String> blockedCommands;
	
	public ArrayList<DungeonConfig> configs;
	public DungeonOptions defaultOptions;
	public CommandGroup commands;
	public List<String> startupCommands;
	public List<String> unloadCommands;
	
	public int cooldownResetOffset; // this amount of hours is subtracted from utc time
	 
	public ArrayList<Instance> instances;
	public HashMap<String,Instance> instanceMap;
	
	public UserData userData = new UserData();
	
	
	private static Dungeons context;
	
	public static Dungeons getContext() {
		return context;
	}
    
	class ConfigLoader extends SimpleFileVisitor<Path> {
		 
		ArrayList<DungeonConfig> list;
		Path configFolder;
		
		@Override
		public FileVisitResult preVisitDirectory( Path file, BasicFileAttributes attrs ) {
			if( file.equals( configFolder ) ) return FileVisitResult.CONTINUE;
			return FileVisitResult.SKIP_SUBTREE;
		}
		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            
			loadFile( file );
            return FileVisitResult.CONTINUE;
        }
		
		ConfigLoader(  ArrayList<DungeonConfig> list ){ 
			this.list = list;
		}
		 
		void loadFile( Path file ) {
			FileConfiguration config = YamlConfiguration.loadConfiguration( file.toFile() );
			DungeonConfig dc = new DungeonConfig();
			if( dc.loadFromConfig(config) ) {
				list.add( dc );
			} else {
				getLogger().info( "Bad config file: " + file.getFileName() );
			}
			
			
		}  
		
		void load() throws IOException {
			configFolder = new File( getDataFolder(), "configs" ).toPath(); 
			Files.walkFileTree( configFolder, this );
		}
	}
	
	//---------------------------------------------------------------------------------------------
	public DungeonConfig findConfig( String name ) {
		// find a config by name
		for( DungeonConfig config : configs ) {
		
			if( config.name.equals( name ) ) {
				return config;
			}
		}
		return null;
	}	
	
	//---------------------------------------------------------------------------------------------
	public boolean loadConfigs() {
		
		ConfigLoader loader = new ConfigLoader( configs );
		try {
			loader.load();
		} catch ( IOException e ) {
			getLogger().severe( "Couldn't load dungeon configs! ("+e.getMessage()+")" );
			return false;
		}

		return true;
	}
	 
	
	//---------------------------------------------------------------------------------------------
	public static String FormatConfigVector( Vector v, boolean blocks ) {
		if( v == null ) return null;
		if( blocks )
			return String.format( "%d %d %d", v.getBlockX(), (int)v.getBlockY(), (int)v.getBlockZ() );
		else
			return String.format( "%.1f %.1f %.1f", v.getX(), v.getY(), v.getZ() );
	}
	
	//---------------------------------------------------------------------------------------------
	public static Vector ParseConfigVector( String input ) {
		if( input == null ) return null;
		String parts[] = input.split( "\\s+" );
		if( parts.length != 3 ) return null;
		float[] data = new float[3];
		
		for( int i = 0; i < 3; i++ ) {
			try {
				data[i] = Float.parseFloat( parts[i] );
			} catch ( NumberFormatException e ) {
				
			}
		}
		return new Vector( data[0], data[1], data[2] );
	}
	
	//---------------------------------------------------------------------------------------------
	private void cleanupWorldGuardConfigs() {
		WorldGuardPlugin wg = (WorldGuardPlugin)Bukkit.getPluginManager().getPlugin( "WorldGuard" );
		if( wg != null ) {
			
			File wgConfigFolder = new File( wg.getDataFolder(), "worlds/instances" );
			if( wgConfigFolder.isDirectory() ) {

				getLogger().fine( "Cleaning up worldguard configs." );
				try {
					org.apache.commons.io.FileUtils.deleteDirectory( wgConfigFolder );
					
				} catch( IOException e ) {
					getLogger().warning( "Could not delete instances folder." );
					
				}
			}
		}

	}
	
	//---------------------------------------------------------------------------------------------
	private void cleanupDungeons() {
		// try to delete all dungeon folders.
		MultiverseCore mv = (MultiverseCore)Bukkit.getPluginManager().getPlugin( "Multiverse-Core" );
		
		Collection<MultiverseWorld> worlds = mv.getMVWorldManager().getMVWorlds();
		boolean success = true;
		for( MultiverseWorld  world : worlds ) {
			if( world.getName().startsWith( "instances/") ) {
				mv.getMVWorldManager().removePlayersFromWorld( world.getName() );
				if( !mv.getMVWorldManager().deleteWorld( world.getName(), true, true ) ) {
					success = false;
				}
			}
		}
		
		if( !success ) {
			getLogger().info( "Error trying to delete old instances." );
		} else {
			// TODO delete any remaining instance folders (that aren't registered with MV)
			
			// cleanup worldguard configs
			cleanupWorldGuardConfigs();
						
		}
		
	}
	
	//---------------------------------------------------------------------------------------------
	private void disableDungeonCommand( String command ) {
		blockedCommands.add( command );
	}
	
	//---------------------------------------------------------------------------------------------
	@Override
    public void onEnable() {
		saveDefaultConfig();
		
		blockedCommands = new HashSet<String>();
		//disableDungeonCommand( "/home" );
		disableDungeonCommand( "/sethome" );
		//disableDungeonCommand( "/tp" );
		disableDungeonCommand( "/top" );
		//disableDungeonCommand( "/tphere" );
		//disableDungeonCommand( "/tpahere" );
		disableDungeonCommand( "/tppos" );
		//disableDungeonCommand( "/teleport" );
		//disableDungeonCommand( "/tpa" );
		//disableDungeonCommand( "/back" );
		//disableDungeonCommand( "/warp" );
		//disableDungeonCommand( "/spawn" );
		disableDungeonCommand( "/world" );
		
		defaultOptions = new DungeonOptions();
		defaultOptions.forceValues();
		defaultOptions.loadFromConfig( getConfig(), "Dungeons.default_options" );
		startupCommands = getConfig().getStringList( "Dungeons.startup_commands" );
		unloadCommands = getConfig().getStringList( "Dungeons.unload_commands" );
		
		
		// cleanup dungeons.
		cleanupDungeons();
		
		context = this;
		commands = new CommandGroup( "dgn", ChatColor.RED + "[Dungeons] " + ChatColor.RESET );
		configs = new ArrayList<DungeonConfig>();
		instances = new ArrayList<Instance>();
		instanceMap = new HashMap<String,Instance>();
		new File( getDataFolder(), "configs" ).mkdirs();
		if( !loadConfigs() ) return; // disk failure, crash plugin
		
	    getServer().getPluginManager().registerEvents(this, this);
		
		new CreateCommand( commands );
		new DeleteCommand( commands );
		new EntryPointCommand( commands );
		new EntryPortalCommand( commands );
		new ExitPointCommand( commands );
		new ExitPortalCommand( commands );
		new AreaCommand( commands );
		new DisableCommand( commands );
		new EnableCommand( commands );
		new TeleportCommand( commands );
		new InfoCommand( commands );
		new ListCommand( commands );
		
		new LootChestCommand( commands );
		new LootListCommand( commands );
		new LootTagCommand( commands );
		new LootUnlinkCommand( commands );
		new CooldownCommand( commands );
		new DenizenKeyCommand( commands );
		
		// TODO slow gnome down.
		new Gnome().runTaskTimer( this, 20, 5 );
		
		List<String> loadCommands = getConfig().getStringList( "Dungeons.pluginload_commands" );
		if( loadCommands != null ) {
			for( String cmd: loadCommands ) {
				Bukkit.getServer().dispatchCommand( Bukkit.getConsoleSender(), cmd );
			}
		}
		
		Path usersDir = getDataFolder().toPath().resolve( "users" );
		try {
			Files.createDirectories( usersDir );
		} catch( IOException e ) {
			getLogger().severe( "Cannot create userdata directory! -- " + e.getMessage() );
		}
		
		saveConfig();
	}
 
	//---------------------------------------------------------------------------------------------
    @Override
    public void onDisable() {
    	userData.saveAll();
    	
    	context = null;
    }
    
    //---------------------------------------------------------------------------------------------
    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		// TODO (debug code)
		if( args.length == 1 && args[0].equals("test") ) {
			
			Player player = (Player)sender;
			Instance inst = getInstance( player.getWorld() );
			if( inst == null ) return true;
			
			spawnLootChest( player.getWorld().getName(), "testloot" );
			
			return true;
		}
    		
    	return commands.onCommand( sender, cmd, label, args );
    }
    
    private void showCooldownMessage( Player player, DungeonConfig config ) {
    	if( config.cooldown == CooldownType.DAY ) {
			player.sendMessage( ChatColor.BLUE + "You are not allowed to enter that dungeon again today." );
		} else if( config.cooldown == CooldownType.WEEK ) {
			player.sendMessage( ChatColor.BLUE + "You are not allowed to enter that dungeon again this week." );
		} else {
			player.sendMessage( ChatColor.BLUE + "You are not allowed to enter that dungeon again." );
		}
    }
    
    public void enterDungeon( Player player, DungeonConfig config ) {
    	
    	String dkey = config.getDenizenKey();
    	if( dkey != null ) {
    		if( !DenizenFlagChecker.playerHasDenizenFlag( player, dkey ) ) {
    			player.sendMessage( ChatColor.BLUE + "You have not completed the required quest to enter this dungeon." );
    			return;
    		}
    	}
    	
    	Parties parties = (Parties)Bukkit.getServer().getPluginManager().getPlugin("Parties");
    	
    	if( parties.getParty(player) == null ) {
    		if( userData.hasCooldown( player, config ) ) {
    			showCooldownMessage( player, config );
	    		return;
    		}
    		
    	}
    	
		parties.ensureInParty( player );
		Party party = parties.getParty( player );
		
		// check if party has a dungeon instance, and enter that one
		for( Instance instance : instances ) {
			if( instance.getDungeon() == config && instance.getParty() == party ) {
				if( instance.isSettingUp() ) return;
				if( !instance.isPlayerLocked( player ) ) {
					if( userData.hasCooldown( player, config ) ) {
						showCooldownMessage( player, config );
						
						return;
					} else if( instance.isLocked() ) {					
						player.sendMessage( ChatColor.BLUE + "You may not enter this dungeon in progress." );
						return;
					} 
				}  
				
				instance.teleportPlayerEntry( player );
				
				return;
			}
		}
		
		if( userData.hasCooldown( player, config ) ) {
			showCooldownMessage( player, config );
			return;
		}
	 
		// create new instance
		// players will be teleported when it's done.
		new Instance( player, party, config ); 
		  
    }
    
    //---------------------------------------------------------------------------------------------
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
    	// TODO cancel multiverse-portals event.
    	if( event.isCancelled() ) return;
    	
    	if( isInDungeon( event.getPlayer() ) ) {
    		// teleport out
    		Instance instance = instanceMap.get( event.getFrom().getWorld().getName() );
			Bukkit.broadcastMessage("TOUCHING EXIT PORTAL");
			event.setCancelled(true);
    		if( instance != null ) {
    			instance.teleportPlayerExit(  event.getPlayer()  );
				 return;
    		}
    	}
    	
    	for( DungeonConfig config : configs ) {
    		
			if( event.getFrom().getWorld().getName().equals(config.entryPortalWorld) && 
					config.entryPortal.isPlayerTouching( event.getFrom() ) ) {

				Bukkit.broadcastMessage("TOUCHING ENTRY PORTAL");
				event.setCancelled(true);
				
				Player player = event.getPlayer();
				if( !config.enabled ) {
					player.sendMessage( "This dungeon is under maintenance." );
					break;
				}
				
				enterDungeon( player, config );
				return;
			}
     
    	}
    	
    	Bukkit.broadcastMessage("NO ENTRY PORTAL");
    	
    }
	//---------------------------------------------------------------------------------------------
    
    private Instance findPlayerInstance( Player player ) {
    	for( Instance instance : instances ) {
			if( player.getWorld() == instance.getWorld() ) {

				return instance; 
			}
			
    	}
    	return null;
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onPartyEvent( PartyEvent event ) {
    	Player player = Bukkit.getPlayer( event.getPlayer() );
    	if( player == null ) return; 
    	
    	if( event.getType() == PartyEvent.Type.LEAVE ) {
	    	
	    	Instance i = findPlayerInstance( player );
	    	if( i != null ) {
				i.refreshAccessTime();
	    		i.teleportPlayerExit( player );
	    	}
    	}
    }
    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onPartyEvent( PartyTryLeaveEvent event ) {
    	 
		Instance i = findPlayerInstance( event.getPlayer() );
    	if( i != null ) {
			i.refreshAccessTime();
    		i.teleportPlayerExit( event.getPlayer() );
    		event.setCancelled( true );
    		return;
    	}
		
    }
    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onQuitEvent( PlayerQuitEvent event ) {
    	
    	userData.saveUserData( event.getPlayer() );
    	
		Instance i = findPlayerInstance( event.getPlayer() );
    	if( i != null ) {
			i.refreshAccessTime();
    		i.teleportPlayerExit( event.getPlayer() ); 
    		return;
    	}
		
    }
    
    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onChangedWorld( PlayerChangedWorldEvent event ) {
   	 
    	for( Instance instance : instances ) {
			if( instance.getWorld() == event.getFrom() ) {
				instance.refreshAccessTime();
			}
			
    	}
		 
    }
    
	//---------------------------------------------------------------------------------------------
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled=true)
    public void onTeleport( PlayerTeleportEvent event ) {
   	 
    	// stop player from teleporting to someone in an instance
    	// if he belongs in the instance party, redirect him to the instance entrance.
    	
    	if( event.getTo().getWorld().getName().startsWith( "instances/" ) ) {
    		Parties parties = (Parties)Bukkit.getServer().getPluginManager().getPlugin("Parties"); 
    		Player player = event.getPlayer();
    		Party party = parties.getParty( player );
    		
    		Instance instance = getInstance( event.getTo().getWorld() );
    		if( instance != null ) {
    			if( instance.getParty() == party ) {
    				DungeonConfig d = instance.getDungeon();
    				
    				// handle blocking ender pearls (maybe just let WorldGuard handle this?)
    				if( event.getCause() == TeleportCause.ENDER_PEARL ) {
    					if( !d.options.allowPearls() ){

    			    		event.setCancelled(true);
    			    		
    					} 
    					return;
    				}
    				
    				if( instance.isSettingUp() ) {
    		    		event.setCancelled(true); // silent failure, this is a rare case.
    					return;
    				}
    				
    				String dkey = d.getDenizenKey();
    		    	if( dkey != null ) {
    		    		if( DenizenFlagChecker.playerHasDenizenFlag( player, dkey ) ) {
    		    			player.sendMessage( ChatColor.BLUE + "You have not completed the required quest to enter this dungeon." );
    		    			event.setCancelled(true);
    						
    		    			return;
    		    		}
    		    	}
    				
    				if( !instance.isPlayerLocked( player ) ) {
    					if( userData.hasCooldown( player, d ) ) {
    						showCooldownMessage( player, d );
    			    		event.setCancelled(true);
    						
    						return;
    					} else if( instance.isLocked() ) {					
    						player.sendMessage( ChatColor.BLUE + "Can't teleport; the dungeon is locked." );
    			    		event.setCancelled(true);
    						return;
    					} 
    				}  
    				
    				//instance.teleportPlayerEntry( player );
    				
    				//if( instance.)
    				
    				// redirect to dungeon entrance
    				event.setTo( 
    						new Location( 
    								event.getTo().getWorld(),
    								d.entryPoint.getX(),
    								d.entryPoint.getY(),
    								d.entryPoint.getZ(),
    								d.entryAngle, 
    								player.getLocation().getPitch() ) );
    				
    						
    				instance.refreshAccessTime();
    				return;
    			}
    		}
    		event.setCancelled(true);
    		player.sendMessage( ChatColor.RED + "You aren't allowed to teleport there." );
    	}
    	
		 
    }
    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onDeathEvent( PlayerDeathEvent  event ) {
   	 
    	World world = event.getEntity().getWorld();
    	if( world == null ) return;
    	Instance instance = instanceMap.get(world.getName());
    	if( instance != null ) {
    		instance.refreshAccessTime(); 
    	}
    	
    	final UUID id = event.getEntity().getUniqueId();
    	 
    	Bukkit.getScheduler().runTaskLater( this, new Runnable() {
    		public void run() {
    			Bukkit.broadcastMessage( "world=" + Bukkit.getPlayer(id).getWorld().getName() );
    		}
    	}, 60);
		 
    }
    
    @EventHandler(priority=EventPriority.HIGH)
    public void onRespawnEvent( PlayerRespawnEvent event ) {
    	Player player = event.getPlayer();
    	if( player.getWorld().getName().startsWith( "instances/" ) ) {
    		Instance instance = getInstance( player.getWorld().getName() );
    		if( instance == null ) return; //error
    		event.setRespawnLocation( new Location( 
    				instance.getWorld(), 
    				instance.getDungeon().entryPoint.getX(),
    				instance.getDungeon().entryPoint.getY(),
    				instance.getDungeon().entryPoint.getZ(),
    				instance.getDungeon().entryAngle,
    				0 ) );
    	}
    	 
    }
    //---------------------------------------------------------------------------------------------
    @EventHandler
    public void onPlayerCommand( PlayerCommandPreprocessEvent event ) {
    	
    	// TODO permission to bypass this mechanism
    	
    	// block cer
    	Player player = event.getPlayer();
    	if( player.getWorld().getName().startsWith( "instances/" ) ) {
    		String command = event.getMessage().split(" ")[0].toLowerCase();
    		Boolean blocked = blockedCommands.contains(command); 
    		if( blocked != null && blocked == true ) {
    			player.sendMessage( ChatColor.RED + "You can't use that command in here. To exit the dungeon use /leave." );
    			event.setCancelled(true);
    			return;
    		}
    	}
    	
    }
    
    public boolean isInDungeon( Entity ent ) {
    	
    	return ent.getWorld().getName().startsWith( "instances/" );
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onPlayerConsume( PlayerItemConsumeEvent event ) {

    	Player player = event.getPlayer();
    	if( isInDungeon(player) ) {
    		
    		if( event.getItem().getType() == Material.POTION ) {
				
	    		for( Instance instance : instances ) {
	    			if( instance.getWorld() == player.getWorld() ) {
	    				instance.refreshAccessTime();
	    				/* cancelling this event causes the item to go on the players head (yes, his head)
	    				 * and delete any armor there
	    				 * 
	    				 * we apparently cant stop health potions from being used
	    				 * 
	    				Collection<PotionEffect> fx = Potion.fromDamage(event.getItem().getDurability()&~0x40).getEffects();
	    				for( PotionEffect effect : fx ) {
	    					if( !instance.getDungeon().options.isPotionAllowed(effect) ) {
	    						event.setCancelled(true);
	    						return;
	    					}
	    				}*/
	    				
	    				// as for other potions, we check them a few ticks later and cancel them.
	    				instance.checkPotionsDelayed( player, 3 );
	    			}
	    			
        		}	
    		}
    	}
    }
    //---------------------------------------------------------------------------------------------
    @EventHandler(ignoreCancelled = true)
    public void onPotionSplash( PotionSplashEvent event ) {
    	for( LivingEntity ent : event.getAffectedEntities() ) {
    		if( ent.getWorld().getName().startsWith( "instances/" ) ) {
    			event.setCancelled(true);
    			return;
    		}
    	}
    }

    //---------------------------------------------------------------------------------------------
    @EventHandler( priority = EventPriority.LOW, ignoreCancelled = true )
    public void onChunkUnload( ChunkUnloadEvent event ) {
    	if( event.getWorld().getName().startsWith( "instances/" ) ) {
    		Instance instance = getInstance( event.getWorld().getName() );
    		if( instance == null ) return;
    		if( instance.closing() ) return;
    		Chunk c = event.getChunk();
    		
    		Cuboid area = instance.getDungeon().area;
    		
    		if(		Math.abs( area.min.getX() - (c.getX()*16) ) * 2 < ( area.getWidth() + 16 ) &&
    				Math.abs( area.min.getZ() - (c.getZ()*16) ) * 2 < ( area.getLength() + 16 ) ) {

        		getLogger().info( "TEST: CHUNK UNLOAD cancel!!" );
    			event.setCancelled(true);
    			return;
    		}
    	}
    }
    
	//---------------------------------------------------------------------------------------------
    public Instance getInstance( String worldname ) {
    	return instanceMap.get( worldname );
    }
    
	//---------------------------------------------------------------------------------------------
    public Instance getInstance( World world ) {
    	return instanceMap.get( world.getName() );
    }
    
	//---------------------------------------------------------------------------------------------
    public void spawnLootChest( String world, String lootChestId ) {
    	// first, get the instance from the world name
    	// if the instance doesnt exist, cancel
    	// get the loot chest id, if it doesnt exist, cancel and log error
    	// get a list of players to generate loot for, and spawn the loot chest
    	// save dungeon cooldown data for each player who received loot/
    	//
    	// when players open a loot chest, the loot is unique to them.
    	
    	Instance instance = getInstance(world);
    	if( instance == null ) return;
    	DungeonConfig config = instance.getDungeon();
    	LootChestInfo info = config.getLootChest( lootChestId );
    	if( info == null ) {
    		getLogger().warning( "Loot chest ID is not valid: " + lootChestId );
    		return;
    	}
    	
    	LootChest loot;
    	try {
    		loot = new LootChest( instance, info );
    	} catch ( LootChest.InvalidLootException e ) {
    		
    		getLogger().warning( "Couldn't spawn loot chest: " + e.getMessage() );
    		return;
    	}
    	
    	instance.addLootChest( loot );
    }
    
    @EventHandler(ignoreCancelled=true)
	public void onOpenInventory( InventoryOpenEvent event ) {
		if( !(event.getPlayer() instanceof Player) ) return;
		Player player = (Player)event.getPlayer();
		if( !player.getWorld().getName().startsWith( "instances/" ) ) return;
		Instance instance = getInstance( player.getWorld() );
		if( instance == null ) return;
		
		Inventory inventory = event.getInventory();
		InventoryHolder holder = inventory.getHolder();
		 
		if( holder instanceof Chest ) {
			Chest chest = (Chest)holder;
			Location loc = ((Chest)chest).getLocation();
			LootChest loot = instance.findLootChest( loc );
			if( loot == null ) return;
			
			if( loot.openChest( player ) ) {
				event.setCancelled(true);
				return;
			}
		}
    }
}

