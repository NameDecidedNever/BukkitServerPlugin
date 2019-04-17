package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.ChatColor;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class MobMoney implements Listener {
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Monster) {
			Monster monsterEnt = (Monster) event.getEntity();
			Player player = monsterEnt.getKiller();
			if (player == null) {
				return;
			}
			double serverBalance = DataManager.getInstance()
					.getBalance(DataManager.getInstance().getServerPrimaryAccount());
			double playerPayment = (serverBalance / 10000) * monsterEnt.getMaxHealth();
			DataManager.getInstance().makePayExchange(DataManager.getInstance().getServerPrimaryAccount(),
					DataManager.getInstance().getPlayerPrimaryAccount(player.getName()), playerPayment,
					"Mob kill reward");
			String mob_reward = ChatColor.GREEN + "Mob Kill Reward";
			player.sendMessage(ChatColor.YELLOW + "+$" + new java.text.DecimalFormat("0.00").format( playerPayment ) + " " + mob_reward);
		}
	}
}
