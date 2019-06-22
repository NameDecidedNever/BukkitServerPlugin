package com.ndn.bukkitplugin.ndnserverplugin;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.rmi.CORBA.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnutils.TeleportLogic;
import com.ndn.bukkitplugin.ndnutils.Utils;

public class TownCommandExecutor implements CommandExecutor {

	private final NDNServerPlugin plugin;
	private HashMap<String, Location> startPlotLocations = new HashMap<String, Location>();

	public TownCommandExecutor(NDNServerPlugin plugin) {
		this.plugin = plugin;
	}

	private boolean found(CommandSender sender, Command command, String label, String[] args, Player player) {
		if (player.getWorld().getName().trim().equals("world")) {
			if (args.length < 1) {
				player.sendMessage(ChatColor.RED + "You need a town name!");
				return true;
			}
			if (!DataManager.getInstance().checkIfPlayerHasOwnTown(player.getName())) {
				if (DataManager.getInstance().checkIfTownCanBeFounded(player.getLocation().getBlockX(),
						player.getLocation().getBlockZ())) {
					if (DataManager.getInstance().getBalance(DataManager.getInstance().getPlayerPrimaryAccount(
							player.getName())) >= ConstantManager.constants.get("TOWN_FOUNDING_COST")) {
						if (args[0].length() < 40) {
							DataManager.getInstance().addTown(args[0], (Player) sender,
									player.getLocation().getBlockX(), player.getLocation().getBlockZ(),
									player.getLocation().getBlockY());
							DataManager.getInstance().makePayExchange(
									DataManager.getInstance().getPlayerPrimaryAccount(player.getName()),
									DataManager.getInstance().getServerPrimaryAccount(),
									ConstantManager.constants.get("TOWN_FOUNDING_COST"),
									"Cost For Founding " + args[0]);
							sender.sendMessage(ChatColor.YELLOW + "Congratulations on founding " + args[0]);
							for (int i = 0; i < 3; i++) {
								FireworkManager.makeFireworkAtPlayer(plugin, player);
							}
						} else {
							sender.sendMessage(
									ChatColor.RED + "Town name is too long. Try something less than 40 characters!");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "You do not have the required $"
								+ ConstantManager.constants.get("TOWN_FOUNDING_COST") + " to found a town!");
					}
				} else {
					sender.sendMessage(ChatColor.RED
							+ "You are too close to another town. Towns must be founded such that their borders are at least "
							+ ConstantManager.constants.get("MIN_TOWN_DISTANCE") + " apart!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "For the time being, you can only found one town per player!");
			}
		} else {
			sender.sendMessage(
					ChatColor.RED + "For the time being, you can only found a town in the main world...nice try!");
		}
		return true;
	}

	private boolean startPlot(CommandSender sender, Command command, String label, String[] args, Player player) {
		if (DataManager.getInstance().getTownIdFromOwnerName(player.getName()) == DataManager.getInstance()
				.getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
			Bukkit.getLogger().log(Level.INFO, "Startplot called");
			int townid = DataManager.getInstance().getTownIdFromOwnerName(player.getName());
			if (townid != -1) {
				startPlotLocations.put(player.getName(), player.getLocation());
				String green1 = ChatColor.GREEN + "Plot starting location saved! Type ";
				String yellow1 = ChatColor.YELLOW + "town plot finish [plotName] [plotType] [pricePerDay] ";
				String green2 = ChatColor.GREEN + "at another location to create your plot!";
				player.sendMessage(green1 + yellow1 + green2);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You do not own a town! Create a town first with (/town found [townName]) and then create plots within your town.");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "You can only place plots within your town.");
		}
		return true;
	}

	private boolean finishPlot(CommandSender sender, Command command, String label, String[] args, Player player) {
		if (args.length == 3) {
			if (args[0].length() < 40) {
				if (DataManager.getInstance().getTownIdFromOwnerName(player.getName()) == DataManager.getInstance()
						.getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
					Bukkit.getLogger().log(Level.INFO, "Finishplot called");
					Location location1 = startPlotLocations.get(player.getName());
					Location location2 = player.getLocation();
					startPlotLocations.remove(player.getName());
					int width = Math.abs(location1.getBlockX() - location2.getBlockX());
					int length = Math.abs(location1.getBlockZ() - location2.getBlockZ());
					int x = Math.min(location1.getBlockX(), location2.getBlockX());
					int z = Math.min(location1.getBlockZ(), location2.getBlockZ());
					int townid = DataManager.getInstance().getTownIdFromOwnerName(player.getName());
					if (townid == -1) {
						sender.sendMessage(ChatColor.RED
								+ "You do not own a town! Create a town first with (/town found [townName]) and then create plots within your town.");
					} else {
						String plotTypeString = args[1];
						int plotType = 0;
						if (plotTypeString.equalsIgnoreCase("residential") || plotTypeString.equalsIgnoreCase("r")) {
							plotType = 1;
						} else if (plotTypeString.equalsIgnoreCase("market")
								|| plotTypeString.equalsIgnoreCase("marketplace")
								|| plotTypeString.equalsIgnoreCase("m")) {
							plotType = 2;
						} else {
							sender.sendMessage(ChatColor.RED
									+ "Invalid plot type. Your second parameter should be either 'r' for residential or 'm' for market.");
							return true;
						}
						DataManager.getInstance().addPlot(args[0], x, z, width, length, plotType,
								Double.parseDouble(args[2]), townid);
						String messagePart1 = ChatColor.GREEN + "Successfully created plot ";
						String messagePart2 = ChatColor.BLUE + "\"" + args[0] + "\"";
						String messagePart3 = ChatColor.GREEN
								+ " The plot is now available for purchase on the website!";
						player.sendMessage(messagePart1 + messagePart2 + messagePart3);
						return true;
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Plot name is too long. Try something less than 40 characters!");
				}
			} else {
				sender.sendMessage(ChatColor.RED
						+ "You can only place plots within your town. Try calling finishplot again but make sure it is within your town borders!");
			}
		} else {
			player.sendMessage(ChatColor.RED
					+ "Make sure to include the correct parameters when you use this command. See the help menu for details, and try again!");
		}
		return true;
	}

	private boolean setWarp(CommandSender sender, Command command, String label, String[] args, Player player) {
		if (DataManager.getInstance().getTownIdFromOwnerName(player.getName()) == DataManager.getInstance()
				.getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ())) {
			DataManager.getInstance().setTownWarpLocation(player.getLocation(), DataManager.getInstance()
					.getTownByArea(player.getLocation().getBlockX(), player.getLocation().getBlockZ()));
			sender.sendMessage(ChatColor.GREEN
					+ "Successfully moved the warp location for your town! Players will now warp here.");
		} else {
			sender.sendMessage(ChatColor.RED + "You can only edit the warp location within your own town!");
		}
		return true;
	}

	private boolean viewplot(CommandSender sender, Command command, String label, String[] args, Player player) {
		StringBuilder sb = new StringBuilder();
		boolean inPlot = true;
		sb.append(ChatColor.GREEN + "You are in a ");
		switch (DataManager.getInstance().getPlotType(player.getLocation().getBlockX(),
				player.getLocation().getBlockZ())) {
		case 2:
			sb.append("Market Plot. ");
			break;
		case 1:
			sb.append("Residential Plot. ");
			break;
		case 0:
			sb.append("Unzoned Area. ");
			break;
		case -1:
			sb = new StringBuilder();
			sb.append(ChatColor.GREEN + "You are not in a plot. ");
			inPlot = false;
		}
		sb.append("You can ");
		if (DataManager.getInstance().getPlotEditableCode(player.getLocation().getBlockX(),
				player.getLocation().getBlockZ(), player.getName()) == 0) {
			sb.append("not break here.");
		} else {
			sb.append("break here.");
		}
		player.sendMessage(sb.toString());

		if (inPlot) {
			Rectangle plot = DataManager.getInstance().getPlotBoundary(player.getLocation().getBlockX(),
					player.getLocation().getBlockZ());
//			player.sendBlockChange(player.getWorld().getHighestBlockAt((int)plot.getMinX(), (int)plot.getMinY()).getLocation(), Material.GLOWSTONE, (byte) 0);
//			player.sendBlockChange(player.getWorld().getHighestBlockAt((int) plot.getMinX(), (int) plot.getMaxY()).getLocation(), Material.GLOWSTONE, (byte) 0);
//			player.sendBlockChange(player.getWorld().getHighestBlockAt((int) plot.getMaxX(), (int) plot.getMinY()).getLocation(), Material.GLOWSTONE, (byte) 0);
//			player.sendBlockChange(player.getWorld().getHighestBlockAt((int) plot.getMaxX(), (int) plot.getMaxY()).getLocation(), Material.GLOWSTONE, (byte) 0);
			//x forloop
			for(int x = (int) plot.getMinX(); x <= plot.getMaxX(); x+= Utils.lowestFactor((int) (plot.getMaxX()-plot.getMinX()))) {
				player.sendBlockChange(player.getWorld().getHighestBlockAt(x, (int) plot.getMinY()).getLocation().subtract(new Vector(0,1,0)), Material.GLOWSTONE, (byte) 0);
				player.sendBlockChange(player.getWorld().getHighestBlockAt(x, (int) plot.getMaxY()).getLocation().subtract(new Vector(0,1,0)), Material.GLOWSTONE, (byte) 0);
			}
			
			for(int y = (int) plot.getMinY() + Utils.lowestFactor((int) (plot.getMaxY()-plot.getMinY())); y < plot.getMaxY(); y+= Utils.lowestFactor((int) (plot.getMaxY()-plot.getMinY()))) {
				player.sendBlockChange(player.getWorld().getHighestBlockAt((int) plot.getMinX(), y).getLocation().subtract(new Vector(0,1,0)), Material.GLOWSTONE, (byte) 0);
				player.sendBlockChange(player.getWorld().getHighestBlockAt((int) plot.getMaxX(), y).getLocation().subtract(new Vector(0,1,0)), Material.GLOWSTONE, (byte) 0);
			}
			player.sendMessage(ChatColor.YELLOW + "The bounds are shown in GlowStone");

		}
		return true;
	}


	private boolean warp(CommandSender sender, Command command, String label, String[] args, Player player) {
		if (args.length == 1) {
			String townName = args[0];
			int townId = DataManager.getInstance().getTownIdFromName(townName);
			if (townId != -1) {
				double costToWarp = TeleportLogic.getTeleportCost(player.getLocation(),
						DataManager.getInstance().getTownWarpLocation(townId));
				double additionalCostToPlayer = costToWarp * DataManager.getInstance().getTownWarpTax(townId);
				double totalCostToPlayer = costToWarp + additionalCostToPlayer;
				if (DataManager.getInstance().getPlayerBalance(player.getName()) >= totalCostToPlayer) {
					DataManager.getInstance().makePayExchange(
							DataManager.getInstance().getPlayerPrimaryAccount(player.getName()),
							DataManager.getInstance().getServerPrimaryAccount(), costToWarp, "Warp To " + townName);
					DataManager.getInstance().makePayExchange(
							DataManager.getInstance().getPlayerPrimaryAccount(player.getName()),
							DataManager.getInstance()
									.getPlayerPrimaryAccount(DataManager.getInstance().getTownOwnerName(townId)),
							additionalCostToPlayer, "Warp Tax for " + townName);
					player.teleport(DataManager.getInstance().getTownWarpLocation(townId));
					String message1 = ChatColor.GREEN + "Paid ";
					String message2 = ChatColor.YELLOW + "$"
							+ new java.text.DecimalFormat("0.00").format(totalCostToPlayer);
					String message3 = ChatColor.GREEN + " for your warp";
					String message4 = ChatColor.YELLOW + "$"
							+ new java.text.DecimalFormat("0.00").format(additionalCostToPlayer);
					player.sendMessage(message1 + message2 + message3);
					player.sendTitle(ChatColor.YELLOW + townName, ChatColor.GREEN + DataManager.getInstance().getTownMotd(townId), 5, 55, 5);
					try {
						plugin.getServer().getPlayer(DataManager.getInstance().getTownOwnerName(townId))
								.sendMessage(ChatColor.GREEN + "Recieved town warp tax income " + message4);
					} catch (Exception e) {

					}
				} else {
					player.sendMessage(ChatColor.RED + "You do not have enough account balance to pay for this warp!");
				}
			} else {
				player.sendMessage(ChatColor.RED + "Inavlid town name!");
			}
		} else {
			player.sendMessage(ChatColor.RED + "Please provide the name of the warp! See the help menu for details.");
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length < 1 || args[0].equalsIgnoreCase("help")) {
				displayHelpText(player);
				return true;
			}
			if (args[0].equalsIgnoreCase("found")) {
				return found(sender, command, label, Utils.shiftArgs(1, args), player);
			} else if (args[0].equalsIgnoreCase("plot")) {
				if (args.length < 2 || args[1].equalsIgnoreCase("help")) {
					displayHelpTextPlots(player);
					return true;
				} else if (args[1].equalsIgnoreCase("start")) {
					return startPlot(sender, command, label, Utils.shiftArgs(2, args), player);
				} else if (args[1].equalsIgnoreCase("finish")) {
					return finishPlot(sender, command, label, Utils.shiftArgs(2, args), player);
				} else if (args[1].equalsIgnoreCase("view")) {
					return viewplot(sender, command, label, Utils.shiftArgs(2, args), player);
				}
			} else if (args[0].equalsIgnoreCase("setwarp")) {
				return setWarp(sender, command, label, Utils.shiftArgs(1, args), player);
			} else if (args[0].equalsIgnoreCase("warp")) {
				return warp(sender, command, label, Utils.shiftArgs(1, args), player);
			}
			return true;
		}
		return false;
	}

	private void displayHelpTextPlots(Player p) {
		p.sendMessage(ChatColor.GREEN + "--== Plot Help ==--");
		p.sendMessage(ChatColor.YELLOW + "/town plot start : " + ChatColor.GREEN
				+ "Starts a plot with the first corner set to the location at which you call this command");
		p.sendMessage(ChatColor.YELLOW + "/town plot finish [plotName] [plotType ('r' or 'm')] [pricePerDay] : "
				+ ChatColor.GREEN
				+ "Finishes a plot by setting the second corner to the player position. Make sure to include all required arguments! For the plot type, 'r' will create a residential plot, while 'm' will create a market");
		p.sendMessage(ChatColor.YELLOW + "/town plot view : " + ChatColor.GREEN
				+ "Views plot info for where you are standing");
	}

	private void displayHelpText(Player p) {
		p.sendMessage(ChatColor.GREEN + "--== Town Help ==--");
		p.sendMessage(ChatColor.YELLOW + "/town found : " + ChatColor.GREEN
				+ "Found your very own town (with a starting radius of "
				+ ConstantManager.constants.get("TOWN_DEFAULT_RADIUS") + "), centered from where you are standing");
		p.sendMessage(ChatColor.YELLOW + "/town warp [townName] : " + ChatColor.GREEN
				+ "Warps to the specified town.....for a price");
		p.sendMessage(ChatColor.YELLOW + "/town setwarp : " + ChatColor.GREEN
				+ "Sets the default location players warp to in your town");
		p.sendMessage(ChatColor.BLUE + "For info on creating and managing plots, use" + ChatColor.YELLOW
				+ " /town plot help");
	}
}
