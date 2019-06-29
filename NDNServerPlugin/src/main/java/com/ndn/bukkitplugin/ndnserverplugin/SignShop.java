package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.block.Chest;
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
		if (Utils.isNumeric(ChatColor.stripColor(sign.getLine(0)))) {
			//throw new IllegalArgumentException("Account ID shop disabled.");
			accNum = Integer.parseInt(ChatColor.stripColor(sign.getLine(0)));
		} else {
			accNum = DataManager.getInstance().getPlayerPrimaryAccount(ChatColor.stripColor(sign.getLine(0)));
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
		
		sign = null;

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
		if (!isWallSign(sign)) {
			if (sign.getBlock().getRelative(BlockFace.DOWN).getState() instanceof Chest) {
				return (Chest) sign.getBlock().getRelative(BlockFace.DOWN).getState();
			} else {
				throw new IllegalArgumentException(ChatColor.RED + "Chest Not Found (Upright)");
			}
		} else {
			BlockFace faceing = ((Directional)sign.getBlock().getBlockData()).getFacing();
			if (sign.getBlock().getRelative(faceing.getOppositeFace()).getType().equals(Material.CHEST)) {
				return (Chest) sign.getBlock().getRelative(faceing.getOppositeFace()).getState();
			} else {
				throw new IllegalArgumentException(ChatColor.RED + "Chest Not Found (SideChest)");
			}

		}

	}
	
	//check if a sign is a wall sign
	public static boolean isWallSign(Sign sign) {
		switch (sign.getBlock().getType()) {
		case ACACIA_WALL_SIGN:
			return true;
		case DARK_OAK_WALL_SIGN:
			return true;
		case BIRCH_WALL_SIGN:
			return true;
		case JUNGLE_WALL_SIGN:
			return true;
		case SPRUCE_WALL_SIGN:
			return true;
		case OAK_WALL_SIGN:
			return true;
		default:
			return false;
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
		DataManager dm = DataManager.getInstance();
		int townId = DataManager.getInstance().getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
		if(townId == -1) {
			player.sendMessage(ChatColor.RED + "You must be within a town to use a sign shop!");
			return false;
		}
		double taxToTown = sellCost * dm.getTownShopTax(townId);
		double totalSellCost = sellCost + taxToTown;
		if (dm.getBalance(accountNum) > totalSellCost 
				&& player.getInventory().containsAtLeast(new ItemStack(item), extangeAmount)) {
			dm.makePayExchange(accountNum, dm.getPlayerPrimaryAccount(player.getName()), sellCost, "Sign Shop Sell");
			dm.makePayExchange(dm.getPlayerPrimaryAccount(player.getName()), dm.getPlayerPrimaryAccount(dm.getTownOwnerName(townId)), taxToTown, "Sign Shop Tax");
			player.getInventory().removeItem(new ItemStack(item, extangeAmount));
			player.sendMessage(ChatColor.BLUE + "You sold " + ChatColor.YELLOW + extangeAmount + " " + item.toString()
					+ ChatColor.BLUE + " for " + ChatColor.YELLOW + "$" + sellCost + ChatColor.BLUE + " plus town tax of " + ChatColor.YELLOW + "$" + taxToTown);
			linkedChest.getInventory().addItem(new ItemStack(item, extangeAmount));
			try {
				Bukkit.getServer().getPlayer(DataManager.getInstance().getTownOwnerName(townId)).sendMessage(ChatColor.GREEN + "Recieved sign shop tax of " + ChatColor.YELLOW + "$" + new java.text.DecimalFormat("0.00").format(taxToTown));
				} catch(Exception e) {
					
				}
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
		DataManager dm = DataManager.getInstance();
		int townId = DataManager.getInstance().getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
		if(townId == -1) {
			player.sendMessage(ChatColor.RED + "You must be within a town to use a sign shop!");
			return false;
		}
		double taxToTown = buyCost * dm.getTownShopTax(townId);
		double totalBuyCost = buyCost + taxToTown;
		if (dm.getBalance(dm.getPlayerPrimaryAccount(player.getName())) >= totalBuyCost && linkedChest.getInventory().containsAtLeast(new ItemStack(item), extangeAmount)) {
			dm.makePayExchange(dm.getPlayerPrimaryAccount(player.getName()), accountNum, buyCost, "Sign Shop Purchase");
			dm.makePayExchange(dm.getPlayerPrimaryAccount(player.getName()), dm.getPlayerPrimaryAccount(dm.getTownOwnerName(townId)), taxToTown, "Sign Shop Tax");
			player.getInventory().addItem(new ItemStack(item, extangeAmount));
			player.sendMessage(ChatColor.BLUE + "You bought " + ChatColor.YELLOW + extangeAmount + " " + item.toString()
					+ ChatColor.BLUE + " for " + ChatColor.YELLOW + "$" + buyCost + ChatColor.BLUE + " plus town tax of " + ChatColor.YELLOW + "$" + taxToTown);
			linkedChest.getInventory().removeItem(new ItemStack(item, extangeAmount));
			try {
				Bukkit.getServer().getPlayer(DataManager.getInstance().getTownOwnerName(townId)).sendMessage(ChatColor.GREEN + "Recieved sign shop tax of " + ChatColor.YELLOW + "$" + new java.text.DecimalFormat("0.00").format(taxToTown));
				} catch(Exception e) {
					
				}
			return true;
		}
		player.sendMessage(ChatColor.RED + "Insufficient Funds or chest shop is out of stock.");
		return false;

	}

	public double getBuyCost() {
		return buyCost;
	}

	public double getSellCost() {
		return sellCost;
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