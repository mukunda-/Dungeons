package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;

import com.mukunda.dungeons.CooldownType;
import com.mukunda.dungeons.DungeonConfig;


public class CooldownCommand extends DungeonCommand {
	public CooldownCommand() {
		super( "cooldown", 2 );
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn cooldown <name> <cooldown>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn cooldown <name> <cooldown>" );
		Commands.reply( sender, "Sets a cooldown for a dungeon." );
		Commands.reply( sender, "<cooldown> can be NONE, DAY, or WEEK" );
	}
	public void run( CommandSender sender, String[] args ) {
		DungeonConfig config = CommandHelper.getDungeonConfig( sender, args[1] );
		if( config == null ) return;
		  
		if( args[2].equalsIgnoreCase("none") ) {
			config.cooldown = CooldownType.NONE;
		} else if( args[2].equalsIgnoreCase("day") ) {
			config.cooldown = CooldownType.DAY;
			
		} else if( args[2].equalsIgnoreCase("week") ) {
			config.cooldown = CooldownType.WEEK;
		} else {
			Commands.reply( sender, "Unknown cooldown arg." );
			return;
		}

		Commands.reply( sender, "Set dungeon cooldown for \""+config.name+"\" to "+config.cooldown.toString()+"." );

		config.save();
	}
}
