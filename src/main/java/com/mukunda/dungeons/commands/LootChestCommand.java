package com.mukunda.dungeons.commands;

import org.bukkit.command.CommandSender;
 
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.mukunda.dungeons.LootChestInfo;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class LootChestCommand extends DungeonCommand {
	public LootChestCommand() {
		super( "loot_chest", 1 );
		playerOnly = true;
	}
	public void printSyntax( CommandSender sender ) {
		Commands.reply( sender, "/dgn loot_chest <chest name>" );
	}
	public void printUsage( CommandSender sender ) {
		Commands.reply( sender, "Usage: /dgn loot_chest <chest name>" );
		Commands.reply( sender, "Creates a loot chest link. The name can be used in scripts to spawn it." );
		Commands.reply( sender, "Before using this command, set the source chest with WorldEdit's first point." );
		Commands.reply( sender, "Set the destination point with WorldEdit's second point, this is where the chest will be spawned; do not select the ground underneath it." );
	}
	
	
	
	public void run( CommandSender sender, String[] args ) { 
		
		Selection selection = CommandHelper.getWorldEditCuboidSelection( sender );
		if( selection == null ) return;
		
		DungeonConfig config = Dungeons.getContext().findConfig( selection.getMaximumPoint().getWorld().getName() );
		if( config == null ) {
			Commands.reply( sender, 
					"A dungeon is not registered with that world. ("+
					selection.getMaximumPoint().getWorld().getName()+")" );
			return;
		}
		
		String lootname = args[1];
		
		if( config.getLootChest( lootname ) != null ) {
			Commands.reply( sender, "That loot chest already exists Remove it with /loot_unlink." );
			return;
		}
		
		LootChestInfo loot = new LootChestInfo( 
				lootname, 
				selection.getMaximumPoint().toVector(),
				selection.getMinimumPoint().toVector() );
		
		config.addLootChest( loot );
		Commands.reply( sender, "Loot chest link created." );
		config.save();
	}
	
	
}

