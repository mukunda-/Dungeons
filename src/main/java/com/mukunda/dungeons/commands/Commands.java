package com.mukunda.dungeons.commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Commands {
 
	ArrayList<DungeonCommand> commands;
	 
	public Commands() {
		commands = new ArrayList<DungeonCommand>(); 
	}
	
	public void register( DungeonCommand cmd ) {
		commands.add( cmd );
	}
	
	//---------------------------------------------------------------------------------------------
	public DungeonCommand get( String name ) {
		for( DungeonCommand c : commands ) {
			if( c.name.equalsIgnoreCase( name ) ) return c;
		}
		return null;
	}
	
    //---------------------------------------------------------------------------------------------
    public static void reply( CommandSender sender, String text ) {
    	sender.sendMessage( "\u00A7c[Dungeons]\u00A7f " + text );
    }
    
    //--------------------------------------------------------------------------------------------
	public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args ) {
		if( args.length < 1 ) {
			reply( sender, "Command list:" );
			for( DungeonCommand c : commands ) {
				c.printSyntax( sender );
			}
			return true;
		}
		
		for( DungeonCommand c : commands ) {
			if( c.name.equalsIgnoreCase( args[0] ) ) {
				if( c.playerOnly && !(sender instanceof org.bukkit.entity.Player) ) {
					reply( sender, "This command can only be executed by a player." );
					return true;
				}
				if( args.length < c.minArgs + 1 ) {
					c.printUsage( sender );
				} else {
					c.run( sender, args );
				}
				return true;
			}
		}
		
		reply( sender, "Unknown command: \"" + args[0] + "\"" ); 
		
		return true;
	}
}
