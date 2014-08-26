package com.mukunda.dungeons;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.bukkit.selections.*;

public class Cuboid {
	public final Vector min;
	public final Vector max;
	
	public int getWidth() {
		return (int)(max.getX() - min.getX());
	}

	public int getHeight() {
		return (int)(max.getY() - min.getY());
	}

	public int getLength() {
		return (int)(max.getZ() - min.getZ());
	}
	 
	public Cuboid( Vector min, Vector max ) {
		this.min = min;
		this.max = max; 
		
	}
	
	
	// from worldedit selection
	public Cuboid( Selection sel ) {
		min = sel.getMinimumPoint().toVector();
		max = sel.getMaximumPoint().toVector();
	}
	
	public boolean isPlayerTouching( Location loc ) {
		if( loc.getX() >= (min.getX() - 0.5) && loc.getX() < (max.getX() + 1.5) &&
				loc.getY() >= (min.getY() - 0.5) && loc.getY() < (max.getY() + 1.5) &&
				loc.getZ() >= (min.getZ() - 0.5) && loc.getZ() < (max.getZ() + 1.5) ) {
			return true;
		}
		return false;
	}
}
