package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Chest;
import org.bukkit.plugin.Plugin;

public class SignShop {
	private int AccountNum;
	private Chest linkedChest;
	private Sign linkedSign;
	private ItemStack item;
	
	public static SignShop makeSignShopFromSign(Sign sign, Plugin plugin) {
		return null;
	}
	public boolean sell(Player player, int amount) {
		return false;
	}
	
	public boolean buy(Player player, int amount) {
		return false;
		
	}
	public static boolean isSignShop(String[] lines) {
		if(lines.length != 4)
			return false;
		
		
		return false;
	}
}