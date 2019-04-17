package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Chest;
import org.bukkit.plugin.Plugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnutils.Utils;

public class SignShop {
	private int accountNum; // number of account
	private Block linkedChest; // linked block to sign TODO: implement this
	private Material item; // the item sold
	private int extangeAmount; // the amount extanged for the price (ex. 5 coal for $1)
	private double buyCost; // the cost to buy, -1 means no buy option
	private double sellCost; // the cost to sell, -1 means no sell option

	public SignShop(int accountNum, Block chest, Material item, int amount, double buy, double sell) {
		this.accountNum = accountNum;
		this.linkedChest = chest;
		this.item = item;
		this.buyCost = buy;
		this.sellCost = sell;
		this.extangeAmount = amount;
	}

	// returns a SignShop from a Sign method
	// will throw exception if given sign doesent work
	public static SignShop makeSignShopFromSign(Sign sign, Block chest) throws IllegalArgumentException {
		if (!isSignShop(sign.getLines())) {
			return null;
		}
		// getting account num
		int accNum = -1;
		if (Utils.isNumeric(sign.getLine(0))) {
			accNum = Integer.parseInt(sign.getLine(0));
		} else {
			accNum = DataManager.getInstance().getPlayerPrimaryAccount(sign.getLine(0));
			if (accNum == 0) {
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
		Material item;
		if (Material.matchMaterial(sign.getLine(1)) != null) {
			item = Material.matchMaterial(sign.getLine(1));
		} else if (Material.getMaterial(sign.getLine(1)) != null) {
			item = Material.getMaterial(sign.getLine(1));
		} else {
			throw new IllegalArgumentException("Item not found");
		}

		// TODO: find buy and sell cost

		double buy = 1;
		double sell = 1;

		return new SignShop(accNum, chest, item, amount, buy, sell);
	}

	// used by makeSignShopFromSign() method
	public double getBuyPriceFromSignLine(String line) {
		return 0;

	}

	// used by makeSignShopFromSign() method
	public double getSellPriceFromSignLine(String line) {
		return 0;

	}

	// method to interact with the sign
	public boolean sell(Player player) {
		DataManager dm = new DataManager();
		if (dm.getBalance(accountNum) > sellCost )  {
			dm.makePayExchange(accountNum, dm.getPlayerPrimaryAccount(player.getName()), sellCost, "Sign Shop Sell");
			player.getInventory().removeItem(new ItemStack(item, extangeAmount));
			player.sendMessage(ChatColor.BLUE + "You sold " + ChatColor.YELLOW + extangeAmount + " " + item.toString() + ChatColor.BLUE + " for $" + ChatColor.GREEN + sellCost + ChatColor.BLUE + ".");
			return true;
		}
		player.sendMessage(ChatColor.RED + "Insufficient Funds.");
		return false;

	}

	// method to interact with the sign
	public boolean buy(Player player) {
		DataManager dm = new DataManager();
		if (dm.getBalance(dm.getPlayerPrimaryAccount(player.getName())) >= buyCost) {
			dm.makePayExchange(dm.getPlayerPrimaryAccount(player.getName()), accountNum, buyCost, "Sign Shop Purchase");
			player.getInventory().addItem(new ItemStack(item, extangeAmount));
			player.sendMessage(ChatColor.BLUE + "You bought " + ChatColor.YELLOW + extangeAmount + " " + item.toString() + ChatColor.BLUE + " for $" + ChatColor.GREEN + buyCost + ChatColor.BLUE + ".");
			return true;
		}
		player.sendMessage(ChatColor.RED + "Insufficient Funds.");
		return false;

	}

	public static boolean isSignShop(String[] lines) {

		if (lines.length != 4) {
			return false;
		}
		for (String l : lines) {
			if (l == null) {
				return false;
			}
		}

		return true;
	}
}