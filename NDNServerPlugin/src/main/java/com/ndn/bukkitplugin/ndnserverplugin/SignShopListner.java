package com.ndn.bukkitplugin.ndnserverplugin;

import java.awt.Event;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class SignShopListner implements Listener {
	
	Plugin plugin;
	public SignShopListner(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerUse(PlayerInteractEvent event) {
		if ((event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.WALL_SIGN)
				|| event.getClickedBlock().getType().equals(Material.LEGACY_SIGN) || event.getClickedBlock().getType().equals(Material.LEGACY_WALL_SIGN))
				&& (true || event.getAction().equals(Event.KEY_ACTION))) {
			Player p = event.getPlayer();
			p.sendMessage("You Clicked a Sign!");
		}
	}
	
//	@EventHandler
//	public void ballFiring(PlayerInteractEvent event) {
//		Player p = event.getPlayer();
//		p.sendMessage("You Right Clicked: " + event.getClickedBlock().getType());
//		if (event.getClickedBlock().getType().equals( Material.SIGN)) {
//			
//			p.sendMessage("You Clicked a Sign!");
//		}
//	}

}
