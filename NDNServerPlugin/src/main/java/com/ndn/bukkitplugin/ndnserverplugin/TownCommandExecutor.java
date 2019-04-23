package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class TownCommandExecutor implements CommandExecutor{
	
	private final double TOWN_FOUND_FEE = 10.0;

	private final NDNServerPlugin plugin;
	
	public TownCommandExecutor(NDNServerPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(command.getName().equals("found")) {
				DataManager.getInstance().addTown(args[0], (Player) sender, player.getLocation().getBlockX(), player.getLocation().getBlockZ());
				DataManager.getInstance().makePayExchange(DataManager.getInstance().getPlayerPrimaryAccount(player.getName()), DataManager.getInstance().getServerPrimaryAccount(), TOWN_FOUND_FEE, "Cost For Founding " + args[0]);
				sender.sendMessage("Congratulations on founding " + args[0]);
				for (int i = 0; i < 3; i++) {
					FireworkManager.makeFireworkAtPlayer(plugin, player);
				}
				
			}
			return true;
		}
		return false;
	}

}
