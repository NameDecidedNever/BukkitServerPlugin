package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.Plugin;
import org.omg.CORBA.FREE_MEM;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class MobMoney implements Listener {

    Plugin plugin;

    public MobMoney(Plugin p) {
	this.plugin = p;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
	if (event.getEntity() instanceof Monster) {
	    Monster monsterEnt = (Monster) event.getEntity();
	    Player player = monsterEnt.getKiller();
	    if (player == null) { return; }
	    double fractionToTownOwner = 0.0;
	    int townId = DataManager.getInstance().getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ());
	    if (townId != -1) {
		fractionToTownOwner = DataManager.getInstance().getTownMobTax(townId);
	    }
	    double serverBalance = DataManager.getInstance().getBalance(DataManager.getInstance().getServerPrimaryAccount());
	    double payment = (serverBalance * ConstantManager.constants.get("MOB_KILL_FACTOR")) * monsterEnt.getMaxHealth();
	    double playerPayment = payment * (1.0 - fractionToTownOwner);
	    double townPayment = payment * fractionToTownOwner;
	    DataManager.getInstance().makePayExchange(DataManager.getInstance().getServerPrimaryAccount(), DataManager.getInstance().getPlayerPrimaryAccount(player.getName()), playerPayment, "Mob kill reward");
	    String mob_reward = ChatColor.GREEN + "Mob Kill Reward";
	    player.sendMessage(ChatColor.YELLOW + "+$" + new java.text.DecimalFormat("0.00").format(playerPayment) + " " + mob_reward);
	    if (townId != -1) {
		int townOwnerAccountId = DataManager.getInstance().getPlayerPrimaryAccount(DataManager.getInstance().getTownOwnerName(townId));
		DataManager.getInstance().makePayExchange(DataManager.getInstance().getPlayerPrimaryAccount(player.getName()), townOwnerAccountId, townPayment, "Mob Kill Tax on " + player.getName() + "");
		try {
		    Player townOwnerPlayer = plugin.getServer().getPlayer(DataManager.getInstance().getTownOwnerName(townId));
		    String valueMessage = ChatColor.YELLOW + "$" + new java.text.DecimalFormat("0.00").format(townPayment);
		    townOwnerPlayer.sendMessage(ChatColor.GREEN + "Recieved a mob kill tax reward of " + valueMessage);
		} catch (Exception e) {

		}
	    }
	}
    }
}
