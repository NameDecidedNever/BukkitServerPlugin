package com.ndn.bukkitplugin.ndnserverplugin;

import java.awt.Event;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class SignShopListner implements Listener {

	Plugin plugin;

	public SignShopListner(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock().getState() instanceof Sign) {
			String[] testMessage = new String[] { "You Right Clicked a Sign!", "Line 0: ", "Line 1: ", "Line 2: ",
					"Line 3: " };
			Sign sign = (Sign) event.getClickedBlock().getState();
			if (SignShop.isSignShop(sign.getLines())) {
				Player player = event.getPlayer();
				try {
					SignShop signShop = SignShop.makeSignShopFromSign(sign,
							SignShop.getChestFromSign(sign));
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						signShop.buy(player);
					} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
						signShop.sell(player);
					}
				} catch (Exception e) {
					player.sendMessage(e.getMessage());
					System.err.print(e.getMessage());
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {

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