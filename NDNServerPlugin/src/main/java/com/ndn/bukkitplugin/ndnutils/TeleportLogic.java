package com.ndn.bukkitplugin.ndnutils;

import org.bukkit.Location;

public class TeleportLogic {
	public static final double TELE_COSTPERBLOCK = .05;
	//code these in for a world
	public static final int SPAWN_X = 140;
	public static final int SPAWN_Y = 64;
	public static final int SPAWN_Z = -75;
	public static final double MINIMUM_PROXIMITY = 200;
	
	public static double getTeleportCost(Location l1, Location l2) {
		if (l1 == null || l2 == null) {
			return -1;
		}
		try {
			double xDisplacment = Math.abs(l1.getBlockX() - l2.getBlockX());
			double zDisplacment = Math.abs(l1.getBlockZ() - l2.getBlockZ());
			if (xDisplacment < MINIMUM_PROXIMITY && zDisplacment < MINIMUM_PROXIMITY) {
				return 0;
			} else {
				return Math.round(100*(Math.sqrt(Math.pow(xDisplacment-MINIMUM_PROXIMITY, 2) + Math.pow(zDisplacment-MINIMUM_PROXIMITY, 2))*TELE_COSTPERBLOCK))/100;
			}
		} catch (Exception e) {
			return -1;
		}
	}
}
