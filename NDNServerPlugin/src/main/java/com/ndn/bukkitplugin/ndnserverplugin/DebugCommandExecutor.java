package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnutils.TeleportLogic;

public class DebugCommandExecutor implements CommandExecutor {

    private final NDNServerPlugin plugin;



    public DebugCommandExecutor(NDNServerPlugin plugin) {
	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if(sender.isOp()) {
	    if(command.getName().equals("dbinfo")) {
		long secondsSinceStart = DataManager.getInstance().getConnectionLifetime();
		int day = (int)TimeUnit.SECONDS.toDays(secondsSinceStart);        
	        long hours = TimeUnit.SECONDS.toHours(secondsSinceStart) - (day *24);
	        long minute = TimeUnit.SECONDS.toMinutes(secondsSinceStart) - (TimeUnit.SECONDS.toHours(secondsSinceStart)* 60);
	        long second = TimeUnit.SECONDS.toSeconds(secondsSinceStart) - (TimeUnit.SECONDS.toMinutes(secondsSinceStart) *60);
		sender.sendMessage(ChatColor.GREEN + "DB Connection Lifetime = " + ChatColor.YELLOW + day + " days, " + hours + "h" + minute + "m" + second + "s");
	    }
	}else {
	    sender.sendMessage(ChatColor.RED + "You must be oped in order to use debug commands!");
	}
	return true;
    }

}
