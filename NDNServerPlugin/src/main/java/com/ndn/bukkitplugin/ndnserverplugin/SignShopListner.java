package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnutils.Utils;

public class SignShopListner implements Listener, CommandExecutor {

	Plugin plugin;

	public SignShopListner(Plugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign) {
			// String[] testMessage = new String[] { "You Right Clicked a Sign!", "Line 0:
			// ", "Line 1: ", "Line 2: ",
			// "Line 3: " };
			Sign sign = (Sign) event.getClickedBlock().getState();
			if (SignShop.isSignShop(sign.getLines())) {
				Player player = event.getPlayer();
				if (DataManager.getInstance().getPlotType(event.getClickedBlock().getX(),
						event.getClickedBlock().getZ()) == 2) {
					try {
						SignShop signShop = SignShop.makeSignShopFromSign(sign, SignShop.getChestFromSign(sign));
						if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
							if(signShop.buy(player)) {
								if(plugin.getServer().getPlayer(sign.getLine(0)) != null) {
									plugin.getServer().getPlayer(sign.getLine(0)).sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.BLUE + " purchased " + ChatColor.YELLOW + sign.getLine(1) + ChatColor.BLUE + " for" + ChatColor.GREEN + " $" + signShop.getBuyCost());
								}
							}
						} else if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
							if(signShop.sell(player)) {
								if(plugin.getServer().getPlayer(sign.getLine(0)) != null) {
									plugin.getServer().getPlayer(sign.getLine(0)).sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.BLUE + " sold " + ChatColor.YELLOW + sign.getLine(1) + ChatColor.BLUE + " for" + ChatColor.GREEN + " $" + signShop.getBuyCost());
								}
							}
						}
					} catch (Exception e) {
						player.sendMessage(e.getMessage());
						System.err.print(e.getMessage());
					}
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "You can only use shops that exist in market plots!");
				}
			}
		}
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (SignShop.isSignShop(event.getLines())) {
			if (DataManager.getInstance().getPlotEditableCode(event.getBlock().getX(), event.getBlock().getZ(), event.getPlayer().getName()) != 2) {
				event.getPlayer().sendMessage(ChatColor.RED + "You can only create sign shops in market zoned plots!");
				event.setCancelled(true);
			} else if ((!Utils.isNumeric(event.getLine(0)) && !event.getPlayer().getName().equals(event.getLine(0))) || DataManager.getInstance().getPlayerPrimaryAccount(event.getPlayer().getName()) != Double.parseDouble(event.getLine(0))) {
				event.getPlayer().sendMessage(ChatColor.RED + "You can only use your name or an account number.");
				event.setCancelled(true);
			} else {
				event.setLine(0, "§2" + event.getLine(0));
			}
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		sender.sendMessage("SignShop help: https://github.com/NameDecidedNever/BukkitServerPlugin/wiki/SignShop-Tutorial");
		return true;
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