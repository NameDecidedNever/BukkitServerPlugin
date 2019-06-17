package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;

public class ChunkProtection {
	
	public static int WORLD_DIAMETER = ConstantManager.constants.get("WORLD_BORDER_BLOCK_DIAMETER").intValue();
	public static int WORLD_RADIUS = (WORLD_DIAMETER / 2);
	public static int MAX_PLAYERS = 24;
	public static boolean[][][] isCoreProtected = new boolean[WORLD_DIAMETER][WORLD_DIAMETER][MAX_PLAYERS];
	
	public static void loadCoreProtectionDataFromDB() {
		//TODO : Write later
	}
	
	public static void setCoreProtectionData(int x, int z, int width, int length, int playerid, boolean value) {
		for(int i = x; i < x + width; i++) {
			for(int j = z; j < z + length; j++) {
				isCoreProtected[WORLD_RADIUS + i][WORLD_RADIUS + j][playerid] = value;
			}
		}
	}
	
	public static boolean getIfCoreIsProtected(Location location, int playerid) {
		return isCoreProtected[location.getBlockX() + WORLD_RADIUS][location.getBlockZ() + WORLD_RADIUS][playerid];
	}
}
