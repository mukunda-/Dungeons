package com.mukunda.dungeons.commands;


import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.CooldownType;
import com.mukunda.dungeons.DungeonConfig;


public class CooldownCommand extends CommandHandler {
	public CooldownCommand( CommandGroup parent ) {
		super( parent, "cooldown", 2, false );
	}
	public void printSyntax() {
		reply( "/dgn cooldown <name> <cooldown>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn cooldown <name> <cooldown>" );
		reply( "Sets a cooldown for a dungeon." );
		reply( "<cooldown> can be NONE, DAY, or WEEK" );
	}
	public void run( String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( this, args[1] );
		if( config == null ) return;
		  
		if( args[2].equalsIgnoreCase("none") ) {
			config.cooldown = CooldownType.NONE;
		} else if( args[2].equalsIgnoreCase("day") ) {
			config.cooldown = CooldownType.DAY;
			
		} else if( args[2].equalsIgnoreCase("week") ) {
			config.cooldown = CooldownType.WEEK;
		} else {
			reply( "Unknown cooldown arg." );
			return;
		}

		reply( "Set dungeon cooldown for \""+config.name+"\" to "+config.cooldown.toString()+"." );

		config.save();
	}
}
