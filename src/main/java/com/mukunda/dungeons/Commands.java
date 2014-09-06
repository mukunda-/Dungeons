package com.mukunda.dungeons;

import org.bukkit.ChatColor;

import com.mukunda.cmdhandler.CommandGroup;
import com.mukunda.dungeons.commands.AreaCommand;
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
import com.mukunda.dungeons.commands.InfoCommand;
import com.mukunda.dungeons.commands.ListCommand;
import com.mukunda.dungeons.commands.LootChestCommand;
import com.mukunda.dungeons.commands.LootListCommand;
import com.mukunda.dungeons.commands.LootTagCommand;
import com.mukunda.dungeons.commands.LootUnlinkCommand;
import com.mukunda.dungeons.commands.TeleportCommand;

public class Commands extends CommandGroup {

	public Commands() {
		super( "dgn", "[" + ChatColor.RED + "Dungeons" + ChatColor.RESET + "] "  );

		new CreateCommand( this );
		new DeleteCommand( this );
		new EntryPointCommand( this );
		new EntryPortalCommand( this );
		new ExitPointCommand( this );
		new ExitPortalCommand( this );
		new AreaCommand( this );
		new DisableCommand( this );
		new EnableCommand( this );
		new TeleportCommand( this );
		new InfoCommand( this );
		new ListCommand( this );
		
		new LootChestCommand( this );
		new LootListCommand( this );
		new LootTagCommand( this );
		new LootUnlinkCommand( this );
		new CooldownCommand( this );
		new DenizenKeyCommand( this );
	}

}
