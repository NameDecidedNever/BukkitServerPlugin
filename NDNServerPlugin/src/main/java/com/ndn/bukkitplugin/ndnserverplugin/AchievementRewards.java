package com.ndn.bukkitplugin.ndnserverplugin;

import javax.jws.Oneway;

import org.bukkit.ChatColor;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class AchievementRewards implements Listener {

	static final double ADVANCEMENT_REWARD_AMOUNT = 10.0;

	@EventHandler
	public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
		//TODO : Add $ with a SQL command because this is technically a bad way to do things...
		double bal = DataManager.getInstance()
				.getBalance(DataManager.getInstance().getPlayerPrimaryAccount(event.getPlayer().getName()));
		DataManager.getInstance().setBalance(
				DataManager.getInstance().getPlayerPrimaryAccount(event.getPlayer().getName()),
				bal + ADVANCEMENT_REWARD_AMOUNT);
		String moneyDisplay = ChatColor.YELLOW + "$" + new java.text.DecimalFormat("0.00").format( ADVANCEMENT_REWARD_AMOUNT );
		event.getPlayer().sendMessage(ChatColor.GREEN + "Recieved " + moneyDisplay + " " + ChatColor.GREEN + "for your achievement!");
	}

}
