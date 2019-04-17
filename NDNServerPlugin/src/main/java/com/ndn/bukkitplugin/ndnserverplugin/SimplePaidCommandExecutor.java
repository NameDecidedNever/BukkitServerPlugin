package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnutils.TeleportLogic;

public class SimplePaidCommandExecutor implements CommandExecutor {

	private final NDNServerPlugin plugin;

	// Hard coded in cords for spawn
	private final Location SPAWN_CORDS;

	public SimplePaidCommandExecutor(NDNServerPlugin plugin) {
		this.plugin = plugin;
		SPAWN_CORDS = plugin.getServer().getWorld("world").getSpawnLocation();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (command.getName().equals("clearweather")) {
				if (DataManager.getInstance().getPlayerBalance(player.getName()) >= 10) {
					// 0 is the server account. if this code errors set the '0' account to the
					// server account
					DataManager.getInstance().makePayExchange(
							DataManager.getInstance().getPlayerPrimaryAccount(player.getName()), DataManager.getInstance().getServerPrimaryAccount(), 10,
							"Clear Weather");
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "weather clear");
					sender.sendMessage(ChatColor.GREEN + "The clouds part to reveal open skies.");
					plugin.getServer().broadcastMessage(player.getName() + " has paid for clear weather.");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "Insufficient funds.");
					return true;
				}

			} else if (command.getName().equals("spawn")) {
				if (!player.getWorld().getName().trim().equals("world")) {
					sender.sendMessage(ChatColor.RED + "Wrong World");
					return true;
				}
				double teleCost = TeleportLogic.getTeleportCost(SPAWN_CORDS, player.getLocation());
				if (teleCost < 0) {
					return false;
				} else if (teleCost == 0) {
					player.teleport(SPAWN_CORDS);
					sender.sendMessage(ChatColor.BLUE + "You were teleported to spawn for free!");
					return true;
				} else if (DataManager.getInstance().getPlayerBalance(player.getName()) >= teleCost) {
					DataManager.getInstance().makePayExchange(
							DataManager.getInstance().getPlayerPrimaryAccount(player.getName()), DataManager.getInstance().getServerPrimaryAccount(), teleCost,
							"Spawn Teleportation");
					player.teleport(SPAWN_CORDS);
					sender.sendMessage(ChatColor.BLUE + "You were teleported to spawn for $" + teleCost + ".");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "Insufficient funds to teleport to spawn. From your position it would cost $" + teleCost);
					return true;
				}

			}

		} else {
			sender.sendMessage("That command is for clients only!");
			return true;
		}
		return false;
	}

}
