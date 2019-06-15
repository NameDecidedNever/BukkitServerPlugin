package com.ndn.bukkitplugin.ndnutils;

import org.bukkit.Location;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;

public class TeleportLogic {
	public static double TELE_COSTPERBLOCK = .05;
	public static double MINIMUM_PROXIMITY = 200;
	
	public static double getTeleportCost(Location l1, Location l2) {
		MINIMUM_PROXIMITY = ConstantManager.constants.get("MIN_SPAWN_FREE_TP");
		TELE_COSTPERBLOCK = ConstantManager.constants.get("COST_PER_BLOCK_TRAVEL_SPAWN_TP");
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
