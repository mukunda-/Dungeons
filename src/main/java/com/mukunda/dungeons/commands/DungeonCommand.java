package com.mukunda.dungeons.commands;
 

import org.bukkit.command.CommandSender;
public abstract class DungeonCommand { 
	 
	public final String name; 
	public int minArgs;  
	public boolean playerOnly;
	
	DungeonCommand( String name, int minArgs ) { 
		this.name = name; 
		this.minArgs = minArgs;
	}
	
	public abstract void printSyntax( CommandSender sender ); 
	public abstract void printUsage( CommandSender sender );  
	public abstract void run( CommandSender sender, String[] args );
}
