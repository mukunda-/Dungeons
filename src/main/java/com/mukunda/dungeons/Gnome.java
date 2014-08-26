package com.mukunda.dungeons;
 
import org.bukkit.scheduler.BukkitRunnable;

// the gnome cleans up stuff
public class Gnome extends BukkitRunnable {
	
	public void run () {
		
		for( int i = 0; i < Dungeons.getContext().instances.size(); i++ ) { 
			if( Dungeons.getContext().instances.get(i).tryCleanup() ) 
				i--;
			
		}
	}
}
