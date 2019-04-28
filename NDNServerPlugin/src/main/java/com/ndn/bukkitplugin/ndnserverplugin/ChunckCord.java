package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Location;

public class ChunckCord {
	private int x;
	private int y;
	
	public ChunckCord(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public ChunckCord getCordFromLoc(Location loc) {
		return getCordFromLoc(loc.getBlockX(), loc.getBlockZ());
		
	}
	
	public ChunckCord getCordFromLoc(int x, int y) {
		return new ChunckCord(x%16, y%16);
		
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ChunckCord && this.getX() == ((ChunckCord)obj).getX() && this.getY() == ((ChunckCord)obj).getY();
	}
	
	
}
