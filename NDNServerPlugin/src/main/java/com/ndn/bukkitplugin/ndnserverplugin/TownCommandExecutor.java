package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class TownCommandExecutor implements CommandExecutor{
	
	private final NDNServerPlugin plugin;
	private HashMap<String, Location> startPlotLocations = new HashMap<String, Location>();
	
	public TownCommandExecutor(NDNServerPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(command.getName().equals("found")) {
				if(!DataManager.getInstance().checkIfPlayerHasOwnTown(player.getName())){
				if(DataManager.getInstance().checkIfTownCanBeFounded(player.getLocation().getBlockX(), player.getLocation().getBlockZ())){
					if(DataManager.getInstance().getBalance(DataManager.getInstance().getPlayerPrimaryAccount(player.getName())) >= ConstantManager.constants.get("TOWN_FOUNDING_COST")) {
				DataManager.getInstance().addTown(args[0], (Player) sender, player.getLocation().getBlockX(), player.getLocation().getBlockZ());
				DataManager.getInstance().makePayExchange(DataManager.getInstance().getPlayerPrimaryAccount(player.getName()), DataManager.getInstance().getServerPrimaryAccount(), ConstantManager.constants.get("TOWN_FOUNDING_COST"), "Cost For Founding " + args[0]);
				sender.sendMessage(ChatColor.YELLOW + "Congratulations on founding " + args[0]);
				for (int i = 0; i < 3; i++) {
					FireworkManager.makeFireworkAtPlayer(plugin, player);
				}
				//Construct town wall
//				int townRadius = ConstantManager.constants.get("TOWN_DEFAULT_RADIUS").intValue();
//				int townCenterX = player.getLocation().getBlockX();
//				int townCenterZ = player.getLocation().getBlockZ();
//				for(int x = townCenterX - townRadius; x < townCenterX + townRadius; x++) {
//					int yLevel1 = plugin.getServer().getWorld("world").getHighestBlockAt(x, townCenterZ + townRadius).getLocation().getBlockY() + 1;
//					int yLevel2 = plugin.getServer().getWorld("world").getHighestBlockAt(x, townCenterZ - townRadius).getLocation().getBlockY() + 1;
//					for(int y = yLevel1; y > yLevel1 - 10; y--) {
//						Block block1 = plugin.getServer().getWorld("world").getBlockAt(x, y, townCenterZ + townRadius);
//						block1.setType(Material.COBBLESTONE_WALL);
//					}
//					for(int y = yLevel2; y > yLevel2 - 10; y--) {
//						Block block2 = plugin.getServer().getWorld("world").getBlockAt(x, y, townCenterZ - townRadius);
//						block2.setType(Material.COBBLESTONE_WALL);
//					}
//				}
//				for(int z = townCenterZ - townRadius; z < townCenterZ + townRadius; z++) {
//					int yLevel1 = plugin.getServer().getWorld("world").getHighestBlockAt(townCenterX + townRadius, z).getLocation().getBlockY() + 1;
//					int yLevel2 = plugin.getServer().getWorld("world").getHighestBlockAt(townCenterX - townRadius, z).getLocation().getBlockY() + 1;
//					for(int y = yLevel1; y > yLevel1 - 10; y--) {
//					Block block1 = plugin.getServer().getWorld("world").getBlockAt(townCenterX + townRadius, yLevel1 ,z);
//					block1.setType(Material.COBBLESTONE_WALL);
//					}
//					for(int y = yLevel2; y > yLevel2 - 10; y--) {
//					Block block2 = plugin.getServer().getWorld("world").getBlockAt(townCenterX - townRadius, yLevel2, z);
//					block2.setType(Material.COBBLESTONE_WALL);
//					}
//				}
				
					}else {
						sender.sendMessage(ChatColor.RED + "You do not have the required $" + ConstantManager.constants.get("TOWN_FOUNDING_COST") + " to found a town!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You are too close to another town. Towns must be founded such that their borders are at least " + ConstantManager.constants.get("MIN_TOWN_DISTANCE") + " apart!");
				}
				} else {
					sender.sendMessage(ChatColor.RED + "For the time being, you can only found one town per player!");
				}
				
			}else if(command.getName().equals("startplot")) {
				if(DataManager.getInstance().getTownIdFromOwnerName(player.getName()) == DataManager.getInstance().getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())){
				Bukkit.getLogger().log(Level.INFO, "Startplot called");
				int townid = DataManager.getInstance().getTownIdFromOwnerName(player.getName());
				if(townid != -1) {
					startPlotLocations.put(player.getName(), player.getLocation());
					String green1 = ChatColor.GREEN + "Plot starting location saved! Type ";
					String yellow1 = ChatColor.YELLOW + "/finishplot ";
					String green2 = ChatColor.GREEN + "at another location to create your plot!";
					player.sendMessage(green1 + yellow1 + green2);
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "You do not own a town! Create a town first with /found and then create plots within your town.");
				}
				}else {
					sender.sendMessage(ChatColor.RED + "You can only place plots within your town.");
				}
			}else if(command.getName().equals("finishplot")) {
				if(args.length == 3) {
					if(DataManager.getInstance().getTownIdFromOwnerName(player.getName()) == DataManager.getInstance().getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())){
				Bukkit.getLogger().log(Level.INFO, "Finishplot called");
				Location location1 = startPlotLocations.get(player.getName());
				Location location2 = player.getLocation();
				startPlotLocations.remove(player.getName());
				int width = Math.abs(location1.getBlockX() - location2.getBlockX());
				int length = Math.abs(location1.getBlockZ() - location2.getBlockZ());
				int x = Math.min(location1.getBlockX(), location2.getBlockX());
				int z = Math.min(location1.getBlockZ(), location2.getBlockZ());
				int townid = DataManager.getInstance().getTownIdFromOwnerName(player.getName());
				if(townid == -1) {
					sender.sendMessage(ChatColor.RED + "You do not own a town! Create a town first with /found and then create plots within your town.");
				} else {
					String plotTypeString = args[1];
					int plotType = 0;
					if(plotTypeString.equals("residential") || plotTypeString.equals("r")) {
						plotType = 1;
					}else if(plotTypeString.equals("market") || plotTypeString.equals("m")) {
						plotType = 2;
					}
					DataManager.getInstance().addPlot(args[0], x, z, width, length, plotType, Double.parseDouble(args[2]), townid);
					return true;
				}
					}else {
						sender.sendMessage(ChatColor.RED + "You can only place plots within your town. Try calling finishplot again but make sure it is within your town borders!");
					}
			}
			}
			return true;
		}
		return false;
	}

}
