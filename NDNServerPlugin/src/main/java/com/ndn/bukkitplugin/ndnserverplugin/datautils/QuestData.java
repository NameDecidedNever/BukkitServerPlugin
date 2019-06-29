package com.ndn.bukkitplugin.ndnserverplugin.datautils;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class QuestData {
	
	public static HashMap<String, Integer> questProgress = new HashMap<String, Integer>();
	
	public static int QUEST_MESSAGE_DURATION = 200;
	
	public static String[] questMessages = {
			"Search for the H.S.D. bunker (719, 79, 865)",
			"Look inside the bunker for clues about the Doctor's whereabouts",
			"Found Doctor's Last known location! Head towards (1222, 96, 1334)",
			"Lab found! The Doctor seems to be missing...search for a radio and send a distress signal!",
			"Distress signal sent. Escape the lab area!",
			"Quest complete!"
	};
	
	public static void setQuestProgress(Player p, int progress) {
		String username = p.getName();
		if(questProgress.containsKey(username)) {
			questProgress.remove(username);
		}
			questProgress.put(username, progress);
			DataManager.getInstance().setQuestProgress(username, progress);
			//ActionBarAPI.sendActionBar(p, ChatColor.YELLOW + questMessages[progress], QUEST_MESSAGE_DURATION);	
	}

	public static void loadPlayerQuestPrgress(Player p) {
		String username = p.getName();
		if(questProgress.containsKey(username)) {
			questProgress.remove(username);
		}
		questProgress.put(username, new Integer(DataManager.getInstance().getQuestProgress(username)));
		//ActionBarAPI.sendActionBar(p, ChatColor.YELLOW + questMessages[DataManager.getInstance().getQuestProgress(username)], QUEST_MESSAGE_DURATION);	
	}
	public static void unloadPlayerQuestProgress(Player p) {
		String username = p.getName();
		questProgress.remove(username);
		//ActionBarAPI.sendActionBar(p, "", QUEST_MESSAGE_DURATION);
	}
	public static void continueQuestProgress(Player p) {
		loadPlayerQuestPrgress(p);
		setQuestProgress(p, questProgress.get(p.getName()) + 1);
	}
}