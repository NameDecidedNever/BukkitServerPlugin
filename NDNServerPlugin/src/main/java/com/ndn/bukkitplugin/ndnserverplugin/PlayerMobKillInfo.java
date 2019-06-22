package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Location;

public class PlayerMobKillInfo {
	private Location loc;
	private int kills;

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public PlayerMobKillInfo(Location loc, int kills) {
		super();
		this.loc = loc;
		this.kills = kills;
	}

}
