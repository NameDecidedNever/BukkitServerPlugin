package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Furnace;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
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
import org.bukkit.loot.Lootable;
import org.bukkit.plugin.Plugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.PlayerCombatPermissions;

public class ProtectionListener implements Listener {

	Plugin plugin;

	public ProtectionListener(Plugin plugin) {
		this.plugin = plugin;
	}

	// -1 = No plot can edit
	// 0 = Can't edit
	// 1 = Can Edit Residential
	// 2 = Can Edit Market

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		int editCode = DataManager.getInstance().getPlotEditableCode(event.getBlock().getX(), event.getBlock().getZ(),
				event.getPlayer().getName());
		if (editCode == 0) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Cannot edit this area!");
		}
		
		//-1 = No plot can edit
		//0 = Can't edit
		//1 = Can Edit Residential
		//2 = Can Edit Market
		
		@EventHandler
		public void onBlockBreak(BlockBreakEvent event){
			if (event.getPlayer().getLocation().getWorld() == Bukkit.getWorld("world")) {
			int editCode = DataManager.getInstance().getPlotEditableCode(event.getBlock().getX(), event.getBlock().getZ(), event.getPlayer().getName());
			if(editCode == 0){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "Cannot edit this area!");
			}
			}
		}
		
		@EventHandler
		public void onBlockPlace(BlockPlaceEvent event){
			if (event.getPlayer().getLocation().getWorld() == Bukkit.getWorld("world")) {
			int editCode = DataManager.getInstance().getPlotEditableCode(event.getBlock().getX(), event.getBlock().getZ(), event.getPlayer().getName());
			if(editCode == 0){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "Cannot edit this area!");
			}
			}
		}
		
		  @EventHandler
		    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){
			  if (event.getDamager().getLocation().getWorld() == Bukkit.getWorld("world")) {
			  	if(event.getDamager() instanceof Player) {
			  		if(!(event.getEntity() instanceof Monster)) {
			  		String playerName = ((Player) event.getDamager()).getName();
		    		int editCode = DataManager.getInstance().getPlotEditableCode((int) event.getEntity().getLocation().getX(), (int) event.getEntity().getLocation().getZ(), playerName);
		    		if(editCode == 0) {
		    			event.setCancelled(true);
		    		}
			  		}
			  	}
			  	}
		    }
		
	    @EventHandler
	    public void onPlayerInteractEvent(PlayerInteractEvent event){
	    	if(!(event == null)) {
	    		if (event.getPlayer().getLocation().getWorld() == Bukkit.getWorld("world")) {
	    		if(event.getClickedBlock() != null) {
	    		int editCode = DataManager.getInstance().getPlotEditableCode(event.getClickedBlock().getX(), event.getClickedBlock().getZ(), event.getPlayer().getName());
	    		if(editCode == 0) {
	    			if(!(event.getClickedBlock().getState() instanceof Sign) && (event.getClickedBlock().getState() instanceof Lootable || event.getClickedBlock().getType() == Material.OAK_DOOR || event.getClickedBlock().getBlockData() instanceof Furnace) || (event.getItem() != null && (event.getItem().getType() == Material.ITEM_FRAME || event.getItem().getType() == Material.ARMOR_STAND || event.getItem().getType() == Material.TNT_MINECART)|| (event.getItem() != null && (event.getItem().getType() != null
									&& (event.getItem().getType().toString().toUpperCase().contains("BUCKET")
											|| event.getItem().getType() == Material.END_CRYSTAL
											|| event.getItem().getType() == Material.FIRE_CHARGE
											|| event.getItem().getType() == Material.FLINT_AND_STEEL)))) {
	    				event.setCancelled(true);
	    				event.getPlayer().sendMessage(ChatColor.RED + "Cannot interact in this area!");
	    			}
	    		}
	    		}
	    		}
	    		}
	    	}
		
		@EventHandler
		public void onEntityExplode(EntityExplodeEvent event){
			if (event.getEntity().getLocation().getWorld() == Bukkit.getWorld("world")) {
			boolean shouldCancel = false;
			for(Block b : event.blockList()) {
				if(DataManager.getInstance().getPlotEditableCode(b.getX(), b.getZ(), "") == 0) {
					shouldCancel = true;
				}
			}
			if(shouldCancel) {
				event.setCancelled(true);
			}
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		boolean shouldCancel = false;
		for (Block b : event.blockList()) {
			if (DataManager.getInstance().getPlotEditableCode(b.getX(), b.getZ(), "") == 0) {
				shouldCancel = true;
			}
		}
		if (shouldCancel) {
			event.setCancelled(true);
		}
	}
}
