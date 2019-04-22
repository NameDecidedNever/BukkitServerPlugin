package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class AboutPageUpdater implements Listener{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		DataManager.getInstance().updateCurrentPlayersNumber(Bukkit.getServer().getOnlinePlayers().size());
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent evt) {
		DataManager.getInstance().updateCurrentPlayersNumber(Bukkit.getServer().getOnlinePlayers().size() - 1);
	}
}
