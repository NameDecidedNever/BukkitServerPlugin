package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.loot.Lootable;
import org.bukkit.material.Button;
import org.bukkit.plugin.Plugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.PlayerCombatPermissions;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.QuestData;

public class QuestListener implements Listener {

	Plugin plugin;

	public QuestListener(Plugin plugin) {
		this.plugin = plugin;
	}
	
	Location locationOfHSDBunker = new Location(Bukkit.getWorld("world"), 719, 79, 865);
	Location locationOfHSDBunkerSearchArea = new Location(Bukkit.getWorld("world"), 719, 61, 851);
	Location locationOfDoctorLab = new Location(Bukkit.getWorld("world"), 1222, 96, 1335);
	Location locationOfRadioButton = new Location(Bukkit.getWorld("world"), 1218, 79, 1332);
	Location zombieSpawnLocation = new Location(Bukkit.getWorld("world"), 1227, 96, 1337);
	Location aromorStandSpawnLocation = new Location(Bukkit.getWorld("world"), 1234, 96, 1340);


	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getPlayer().getLocation().getWorld() == Bukkit.getWorld("world")) {
			if(QuestData.questProgress.containsKey(event.getPlayer().getName())) {
			if(QuestData.questProgress.get(event.getPlayer().getName()) == 0) {
				if(event.getPlayer().getLocation().distance(locationOfHSDBunker) < 20) {
					QuestData.continueQuestProgress(event.getPlayer());
				}
			}else if(QuestData.questProgress.get(event.getPlayer().getName()) == 2) {
				if(event.getPlayer().getLocation().distance(locationOfDoctorLab) < 15) {
					QuestData.continueQuestProgress(event.getPlayer());
				}
			}else if(QuestData.questProgress.get(event.getPlayer().getName()) == 4) {
				if(event.getPlayer().getLocation().distance(locationOfDoctorLab) > 30) {
					QuestData.continueQuestProgress(event.getPlayer());
					event.getPlayer().sendTitle(ChatColor.YELLOW + "Quest Complete!", ChatColor.BLUE + "Distress Signal", 5, 100, 5);
					event.getPlayer().sendMessage(ChatColor.GREEN + "Thank you for completing the first quest! New quests that build on the story will be coming in the following days / weeks! Stay tuned!");
					event.getPlayer().sendMessage(ChatColor.GREEN + "Recieved " + ChatColor.YELLOW  + "$100 " + ChatColor.GREEN + "for completing a quest!");
					DataManager.getInstance().makePayExchange(DataManager.getInstance().getServerPrimaryAccount(), DataManager.getInstance().getPlayerPrimaryAccount(event.getPlayer().getName()), 100.0, "Quest Reward - Distress Signal");
				}
			}
		}
	}
	}
	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getPlayer().getLocation().getWorld() == Bukkit.getWorld("world")) {
			if(QuestData.questProgress.containsKey(event.getPlayer().getName())) {
			if(QuestData.questProgress.get(event.getPlayer().getName()) == 1) {
				if(event.getPlayer().getLocation().distance(locationOfHSDBunkerSearchArea) < 200) {
					
					if(event.getClickedBlock().getState() instanceof Lootable) {
						if(Math.random() > 0.8) {
							event.setCancelled(true);
							QuestData.continueQuestProgress(event.getPlayer());
						}
					}
				}
			}else if(QuestData.questProgress.get(event.getPlayer().getName()) == 3){
				if(event.getClickedBlock() != null) {
				if(event.getClickedBlock().getLocation().distance(locationOfRadioButton) < 3 && event.getClickedBlock().getType() == Material.STONE_BUTTON) {
						QuestData.continueQuestProgress(event.getPlayer());
						Bukkit.getWorld("world").spawnEntity(zombieSpawnLocation, EntityType.ZOMBIE);
						Bukkit.getWorld("world").spawnEntity(zombieSpawnLocation, EntityType.ZOMBIE);
						Bukkit.getWorld("world").spawnEntity(zombieSpawnLocation, EntityType.ZOMBIE);
						Bukkit.getWorld("world").spawnEntity(zombieSpawnLocation, EntityType.ZOMBIE);
						Bukkit.getWorld("world").spawnEntity(zombieSpawnLocation, EntityType.ZOMBIE);
						Zombie z = (Zombie) Bukkit.getWorld("world").spawnEntity(zombieSpawnLocation, EntityType.ZOMBIE);
						LivingEntity target = (LivingEntity) Bukkit.getWorld("world").spawnEntity(aromorStandSpawnLocation, EntityType.ARMOR_STAND);
						z.setTarget(target);
				}}
				}
			}
			}
		}
}
