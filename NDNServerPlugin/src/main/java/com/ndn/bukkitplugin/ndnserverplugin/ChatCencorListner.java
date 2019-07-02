package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class ChatCencorListner implements Listener {
	static String[] bannedList = { "nigger", "chink", "gouk", "towelhead", "negro", "beaner", "nigga", "nibba",
			"nigg" };

	Plugin plugin;

	public ChatCencorListner(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
		if (event.getMessage().toLowerCase().contains("n word")) {
			plugin.getServer().broadcastMessage(
					"" + ChatColor.BOLD + ChatColor.ITALIC + ChatColor.YELLOW + "YOU CANT SAY THAT THATS RACIST!");
			event.setMessage(event.getMessage().toString());
			return;
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
			if(s.contains("n") && s.contains("g") || advancedNwordDetection(s)) {
				event.getPlayer().sendMessage("advanced N Word detection tripped. get recked nerd");
				event.setMessage(ChatColor.BOLD + "I Tried to say the NWord. James stoped me.");
			}
			if (s.equals("sleep") && Math.random() > .8 || s.equals("slep")) {
				s = ChatColor.RED + "slep" + ChatColor.RESET;
				message.append(s + " ");
				if (Math.random() > .85) {
					plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Serously, Just sleep already.");
					
				} else if (Math.random() > .85) {
					plugin.getServer().broadcastMessage(ChatColor.YELLOW + "Nobody likes the phantoms, sleep!");
				}
			} else if (s.equalsIgnoreCase("yeet")) {
				s = "" + ChatColor.YELLOW + ChatColor.BOLD + ChatColor.ITALIC + "YEET" + ChatColor.RESET;
			} else if (s.equalsIgnoreCase("gay")) {
				s = ChatColor.LIGHT_PURPLE + s + ChatColor.RESET;
				message.append(s + " ");
			} else if (s.equalsIgnoreCase("zzz")) {
				s = "" + ChatColor.MAGIC;
				message.append(s + " ");
			} else if (s.equalsIgnoreCase("#red"))
				message.append(ChatColor.RED);
			else if (s.equalsIgnoreCase("#green"))
				message.append(ChatColor.GREEN);
			else if (s.equalsIgnoreCase("#blue"))
				message.append(ChatColor.BLUE);
			else if (s.equalsIgnoreCase("#yello"))
				message.append(ChatColor.YELLOW);
			else if (s.equalsIgnoreCase("#bold"))
				message.append(ChatColor.BOLD);
			else
				message.append(s + " ");
		}
		
		event.setMessage(message.toString());
		message = null;
	}
	
	//yes, i was up at 2 wrighting nword detection as a joke
	private boolean advancedNwordDetection(String s) {
		int stage = 0;
		for(char c : s.toLowerCase().toCharArray()) {
			if(c == 'n' && stage == 0) {
				stage = 1;
			} else if((c == 'i' || c == '1') && stage == 1) {
				stage = 2;
			} else if(c == 'g' && (stage == 3 || stage == 4)) {
				stage += 1;
			} else if((c == 'e' || c == '3') && stage == 5) {
				return true;
			}
		}
		return false;
	}
}
