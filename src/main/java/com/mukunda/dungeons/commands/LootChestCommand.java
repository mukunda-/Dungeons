package com.mukunda.dungeons.commands;
 
import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.cmdhandler.CommandHandler;
import com.mukunda.dungeons.DungeonConfig;
import com.mukunda.dungeons.Dungeons;
import com.mukunda.dungeons.LootChestInfo;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class LootChestCommand extends CommandHandler {
	public LootChestCommand( CommandGroup parent ) {
		super( parent, "loot_chest", 1, true ); 
	}
	public void printSyntax() {
		reply( "/dgn loot_chest <chest name>" );
	}
	public void printUsage() {
		reply( "Usage: /dgn loot_chest <chest name> [major]" );
		reply( "Creates a loot chest link. The name can be used in scripts to spawn it." );
		reply( "Before using this command, set the source chest with WorldEdit's first point." );
		reply( "Set the destination point with WorldEdit's second point, this is where the chest will be spawned; do not select the ground underneath it." );
		reply( "If [major] is set to TRUE, then when the chest is spawned, the contents will only be present for people who were in the dungeon at the time." );
		reply( "When a MAJOR chest spawns, anyone who doens't get it will be locked out of the dungeon." );
	}
	
	
	
	public void run( String[] args ) { 
		
		Selection selection = CommandHelper.getWorldEditCuboidSelection( this );
		if( selection == null ) return;
		
		DungeonConfig config = Dungeons.getContext().findConfig( selection.getMaximumPoint().getWorld().getName() );
		if( config == null ) {
			reply( "A dungeon is not registered with that world. ("+
					selection.getMaximumPoint().getWorld().getName()+")" );
			return;
		}
		
		String lootname = args[1];
		
		if( config.getLootChest( lootname ) != null ) {
			reply( "That loot chest already exists Remove it with /loot_unlink." );
			return;
		}
		
		boolean giveAll = true;
		if( args.length >= 3 ) {
			if( args[2].equalsIgnoreCase("true") ) giveAll = false;
		}
		
		LootChestInfo loot = new LootChestInfo( 
				lootname, 
				selection.getMaximumPoint().toVector(),
				selection.getMinimumPoint().toVector(), giveAll );
		
		config.addLootChest( loot );
		reply( "Loot chest link created." + ((!giveAll)?"+major":"") );
		config.save();
	}
	
	
}

