package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ChunckClaimCommandExecutor implements CommandExecutor {

	Plugin plugin;

	public ChunckClaimCommandExecutor(Plugin plugin) {
		this.plugin = plugin;
		ChunckProtection.retreiveFromDatabase();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("claim")) {
			if (sender.isOp()) {
				Player player = (Player) sender;
				try {
					ChunckProtection.setChunkPremission(new ChunckPremission(player.getName()),
							new ChunckCord(player.getLocation().getBlockX(), player.getLocation().getBlockZ()));
				} catch (Exception e) {
					sender.sendMessage(e.getMessage());
				}
				return true;

			}
		} else {
			for (ChunckCord cc : ChunckProtection.chunkPremmisions.keySet()) {
				sender.sendMessage("" + cc + " " + ChunckProtection.chunkPremmisions.get(cc));
				return true;
			}
		}
		return false;
	}

}
