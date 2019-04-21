package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Chest;
import org.bukkit.plugin.Plugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnutils.Utils;

public class SignShop {
	private int accountNum; // number of account
	private Chest linkedChest; // linked block to sign TODO: implement this
	private Material item; // the item sold
	private int extangeAmount; // the amount extanged for the price (ex. 5 coal for $1)
	private double buyCost; // the cost to buy, -1 means no buy option
	private double sellCost; // the cost to sell, -1 means no sell option

	public SignShop(int accountNum, Chest chest, Material item, int amount, double buy, double sell) {
		this.accountNum = accountNum;
		this.linkedChest = chest;
		this.item = item;
		this.buyCost = buy;
		this.sellCost = sell;
		this.extangeAmount = amount;
	}

	// returns a SignShop from a Sign method
	// will throw exception if given sign doesent work
	public static SignShop makeSignShopFromSign(Sign sign, Chest chest) throws IllegalArgumentException {
		if (!isSignShop(sign.getLines())) {
			return null;
		}
		// getting account num
		int accNum = -1;
		if (Utils.isNumeric(sign.getLine(0).substring(2))) {
			accNum = Integer.parseInt(sign.getLine(0));
		} else {
			accNum = DataManager.getInstance().getPlayerPrimaryAccount(sign.getLine(0).substring(2));
			if (accNum <= 0) {
				throw new IllegalArgumentException("Account not found.");
			}
		}

		// getting amount per transaction
		int amount;
		if (Utils.isNumeric(sign.getLine(2))) {
			amount = Integer.parseInt(sign.getLine(2));
		} else {
			throw new IllegalArgumentException("Stack Size is Invalid");
		}

		// getting item
		Material item = getMaterial(sign.getLine(1));

		//using separate methods to get buy and sell price
		double buy = getBuyPriceFromSignLine(sign.getLine(3));
		double sell = getSellPriceFromSignLine(sign.getLine(3));

		return new SignShop(accNum, chest, item, amount, buy, sell);
	}
	
	// used by makeSignShopFromSign() method
	public static Material getMaterial(String line) {
		line = line.replace(' ', '_').toLowerCase();
		if (Material.matchMaterial(line) != null) {
			return Material.matchMaterial(line);
		} else if(Material.matchMaterial(line+"s")  != null) {
			//in case user enters "oak_plank instead of oak_planks"
			return Material.matchMaterial(line+"s");
		} else if (Material.getMaterial(line.toUpperCase()) != null) {
			return Material.getMaterial(line.toUpperCase());
		} else {
			switch (line.toLowerCase()) {
			case "iron":
				return Material.IRON_INGOT;
			case "gold":
				return Material.GOLD_INGOT;
			case "plank": case "planks":
				return Material.OAK_PLANKS;
			case "wood": case "log": case "logs":
				return Material.OAK_LOG;
			}
			throw new IllegalArgumentException("Item not found");
		}
	}

	// used by makeSignShopFromSign() method
	public static double getBuyPriceFromSignLine(String line) {
		return getPrice(line, 'b');
	}

	// used by makeSignShopFromSign() method
	public static double getSellPriceFromSignLine(String line) {
		return getPrice(line, 's');
	}
	
	public static double getPrice(String line, char c){
		int middle = line.indexOf(":");
		if(middle < 0) {
			//if there is only one feild
			if(line.toLowerCase().contains(""+c)) {
				return getDoubleFromCombinedString(line);
			}else {
				return -1;
			}
		} else {
			//decide what side of ':' to work on
			String section = line.substring(0,middle).toLowerCase().contains(""+c) ? line.substring(0,middle) : line.substring(middle+1, line.length());
			if(section.contains(""+c)) {
				return -1;
			}
			return getDoubleFromCombinedString(section);
		}
	}
	
	//used by buy price methods
	private static double getDoubleFromCombinedString(String s) {
		StringBuilder sb = new StringBuilder();
		for(char c : s.toString().toCharArray()) {
			if("-1234567890.".contains(""+c)) {
				sb.append(c);
			}
		}
		return Double.parseDouble(sb.toString());
	}
	// method to get chest next to sign
	public static Chest getChestFromSign(Sign sign) throws IllegalArgumentException {
		org.bukkit.material.Sign matSign = (org.bukkit.material.Sign) sign.getBlock().getState().getData();
		if (!matSign.isWallSign()) {
			if (sign.getBlock().getRelative(BlockFace.DOWN).getState() instanceof Chest) {
				return (Chest) sign.getBlock().getRelative(BlockFace.DOWN).getState();
			} else {
				throw new IllegalArgumentException(ChatColor.RED + "Chest Not Found (Upright)");
			}
		} else {
			BlockFace faceing = matSign.getFacing().getOppositeFace();

			if (sign.getBlock().getRelative(faceing).getState() instanceof Chest) {
				return (Chest) sign.getBlock().getRelative(faceing).getState();
			} else {
				throw new IllegalArgumentException(ChatColor.RED + "Chest Not Found (SideChest)");
			}

		}

	}

	// method to interact with the sign
	public boolean sell(Player player) {
		if(sellCost < 0) {
			player.sendMessage(ChatColor.RED + "You cant sell to this shop.");
			return false;
		}
		if(DataManager.getInstance().getPlayerPrimaryAccount(player.getName()) == accountNum) {
			player.sendMessage(ChatColor.RED + "This is your shop.");
			return false;
		}
		DataManager dm = new DataManager();
		if (dm.getBalance(accountNum) > sellCost
				&& player.getInventory().containsAtLeast(new ItemStack(item), extangeAmount)) {
			dm.makePayExchange(accountNum, dm.getPlayerPrimaryAccount(player.getName()), sellCost, "Sign Shop Sell");
			player.getInventory().removeItem(new ItemStack(item, extangeAmount));
			player.sendMessage(ChatColor.BLUE + "You sold " + ChatColor.YELLOW + extangeAmount + " " + item.toString()
					+ ChatColor.BLUE + " for $" + ChatColor.GREEN + sellCost + ChatColor.BLUE + ".");
			linkedChest.getInventory().addItem(new ItemStack(item, extangeAmount));
			return true;
		}
		player.sendMessage(ChatColor.RED + "Insufficient Items or Shop has ran out of money.");
		return false;

	}

	// method to interact with the sign
	public boolean buy(Player player) {
		if(buyCost < 0) {
			player.sendMessage(ChatColor.RED + "You cant buy at this shop.");
			return false;
		}
		if(DataManager.getInstance().getPlayerPrimaryAccount(player.getName()) == accountNum) {
			player.sendMessage(ChatColor.RED + "This is your shop.");
			return false;
		}
		DataManager dm = new DataManager();
		if (dm.getBalance(dm.getPlayerPrimaryAccount(player.getName())) >= buyCost && linkedChest.getInventory().containsAtLeast(new ItemStack(item), extangeAmount)) {
			dm.makePayExchange(dm.getPlayerPrimaryAccount(player.getName()), accountNum, buyCost, "Sign Shop Purchase");
			player.getInventory().addItem(new ItemStack(item, extangeAmount));
			player.sendMessage(ChatColor.BLUE + "You bought " + ChatColor.YELLOW + extangeAmount + " " + item.toString()
					+ ChatColor.BLUE + " for $" + ChatColor.GREEN + buyCost + ChatColor.BLUE + ".");
			linkedChest.getInventory().removeItem(new ItemStack(item, extangeAmount));
			return true;
		}
		player.sendMessage(ChatColor.RED + "Insufficient Funds or chest shop is out of stock.");
		return false;

	}

	public static boolean isSignShop(String[] lines) {

		if (lines.length != 4) {
			return false;
		}
		for (String l : lines) {
			if (l == null || l.equals("")) {
				return false;
			}
		}
		if(!Utils.isNumeric(lines[2])) {
			return false;
		}

		return true;
	}
}