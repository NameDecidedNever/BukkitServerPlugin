package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class ChunckProtection implements Listener {

	Plugin plugin;

	public ChunckProtection(Plugin plugin) {
		this.plugin = plugin;
	}

	/*
	 * Event Handlers
	 */

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent e) {
		if (!isAllowed(e.getBlock().getLocation(), e.getPlayer()) && !e.getPlayer().isOp()) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You can't break blocks here!");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlaceEvent(BlockPlaceEvent e) {
		if (!isAllowed(e.getBlock().getLocation(), e.getPlayer()) && !e.getPlayer().isOp()) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You can't place blocks here!");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (!isAllowed(e) && !e.getPlayer().isOp()) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.RED + "You can't use blocks here!");
		}
	}

	/**
	 * Method to check if a player is allowed to interact with objects at his
	 * location
	 */

	public boolean isAllowed(Location loc, Player p) {
		Random rand = new Random();
		return rand.nextBoolean();

	}

	// spific isAllowed to check if player can use a block in a area
	// TODO: add block differation
	public boolean isAllowed(PlayerInteractEvent e) {
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			switch (e.getClickedBlock().getType()) {
			case CHEST:
				return isAllowed(e.getClickedBlock().getLocation(), e.getPlayer());
			case FURNACE:
				return isAllowed(e.getClickedBlock().getLocation(), e.getPlayer());
			case ENCHANTING_TABLE:
				return isAllowed(e.getClickedBlock().getLocation(), e.getPlayer());
			default:
				return true;
			}
			
		}
		return true;
	}
}
