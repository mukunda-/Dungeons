package com.mukunda.dungeons;

import net.aufdemrand.denizen.Denizen;
import net.aufdemrand.denizen.flags.FlagManager;
import net.aufdemrand.denizen.objects.dPlayer;
import net.aufdemrand.denizen.utilities.DenizenAPI;
 
import org.bukkit.entity.Player;

public final class DenizenFlagChecker {
	public static boolean playerHasDenizenFlag( Player player, String flag ) {
		Denizen api = DenizenAPI.getCurrentInstance();
		if( api == null ) return false;
		return FlagManager.playerHasFlag( new dPlayer(player), flag );
	}
}
