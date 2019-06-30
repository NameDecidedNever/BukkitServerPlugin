package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class ChatCencorListner implements Listener {
    static String[] bannedList = { "nigger", "chink", "gouk", "towelhead", "negro", "beaner", "nigga", "nibba", "nigg" };

    Plugin plugin;

    public ChatCencorListner(Plugin plugin) {
	this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
	if (event.getMessage().toLowerCase().contains("n word")) {
	    plugin.getServer().broadcastMessage("" + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.YELLOW + "YOU CANT SAY THAT THATS RACIST!");
	}
	StringBuilder message = new StringBuilder();
	for (String s : event.getMessage().split("\\s+")) {
	    for (String b : bannedList) {
		if (s.toLowerCase().contains(b)) {
		    StringBuilder sSB = new StringBuilder();
		    System.out.println(event.getPlayer().getName() + " said banned word: " + b);
		    for (int i = 0; i < s.length(); i++) {
			sSB.append("*");
		    }
		    s = sSB.toString();
		}
	    }
	    if (s.equals("sleep") && Math.random() > .8 || s.equals("slep")) {
		s = ChatColor.RED + "slep" + ChatColor.RESET;
		message.append(s + " ");
	    }
	    else if (s.equalsIgnoreCase("yeet")) {
		s = "" + ChatColor.YELLOW + ChatColor.BOLD + ChatColor.ITALIC + "YEET" + ChatColor.RESET;
	    }
	    else if (s.equalsIgnoreCase("gay")) {
		s = ChatColor.LIGHT_PURPLE + s + ChatColor.RESET;
		message.append(s + " ");
	    }
	    else if (s.equalsIgnoreCase("zzz")) {
		s = "" + ChatColor.MAGIC;
		message.append(s + " ");
	    }
	    else if(s.equalsIgnoreCase("#red"))
	    message.append(ChatColor.RED);
	    else if(s.equalsIgnoreCase("#green"))
		message.append(ChatColor.GREEN);
	    else if(s.equalsIgnoreCase("#blue"))
		message.append(ChatColor.BLUE);
	    else if(s.equalsIgnoreCase("#yello"))
		message.append(ChatColor.YELLOW);
	    else
	    message.append(s + " ");
	}
	event.setMessage(message.toString());
	message = null;
    }
}
