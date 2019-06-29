package com.ndn.bukkitplugin.ndnserverplugin.datautils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.ndn.bukkitplugin.ndnserverplugin.NDNServerPlugin;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnutils.TeleportLogic;

public class QuestCommandExecutor implements CommandExecutor {

    private final NDNServerPlugin plugin;

    public QuestCommandExecutor(NDNServerPlugin plugin) {
    	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if (sender instanceof Player) {
	    Player player = (Player) sender;

	    if (command.getName().equals("quest")) {
	    	if(args[0].equals("resume") || args[0].equals("start") || args[0].equals("view")) {
	    		QuestData.loadPlayerQuestPrgress(player);
	    	}else if(args[0].equals("pause") || args[0].equals("stop") ) {
	    		QuestData.unloadPlayerQuestProgress(player);
	    	}else if(args[0].equals("next") && player.isOp()) {
	    		QuestData.continueQuestProgress(player);
	    	}else if(args[0].equals("reset") && player.isOp()) {
	    		QuestData.setQuestProgress(player, 0);;
	    	}else if(args[0].equals("help")) {
	    		player.sendMessage(ChatColor.GREEN + "Use " + ChatColor.YELLOW + "/quest view"  + ChatColor.GREEN + " to view your current quest task!");
	    	}
	    }
	    return true;

}
	 return true;
   } 
}
