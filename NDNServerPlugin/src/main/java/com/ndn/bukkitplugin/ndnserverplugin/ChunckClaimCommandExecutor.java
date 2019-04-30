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
							ChunckCord.getCordFromLoc(player.getLocation()));
				} catch (Exception e) {
					sender.sendMessage("Exception: " + e.getMessage() + " " + e.getLocalizedMessage() + " " + e.fillInStackTrace() + e.getCause());
				}

			}
		}
		for (ChunckCord cc : ChunckProtection.chunkPremmisions.keySet()) {
			sender.sendMessage("" + cc + " " + ChunckProtection.chunkPremmisions.get(cc));
			sender.sendMessage("got here");
		}
		return true;
	}

}
