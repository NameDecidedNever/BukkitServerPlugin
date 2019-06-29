package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class MoneyCommandExecutor implements CommandExecutor {
	private final NDNServerPlugin plugin;

	public MoneyCommandExecutor(NDNServerPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean sendMoney(CommandSender sender, String reciver, double amount, String reason) {
		if (sender instanceof Player) {
			// TODO: find better solution than shift int. Possible passed in arg?
			Player player = (Player) sender;
			if (!(DataManager.getInstance().getPlayerTownAffiliation(player.getName()) == DataManager.getInstance()
					.getPlayerTownAffiliation(reciver))
					|| DataManager.getInstance().getPlayerTownAffiliation(player.getName()) == -1) {
				double tax = (ConstantManager.constants.get("PLAYER_TRANSFER_TAX_PERCENT") / 100.0) * amount;
				DataManager.getInstance().makePayExchange(
						DataManager.getInstance().getPlayerPrimaryAccount(player.getName()),
						DataManager.getInstance().getServerPrimaryAccount(), tax, "Player To Player Transfer Tax");
				player.sendMessage(ChatColor.GREEN + "Charging " + ChatColor.YELLOW + "$"
						+ new java.text.DecimalFormat("0.00").format(tax) + ChatColor.GREEN
						+ " because players are not residents of the same town...");
			}
			if (DataManager.getInstance().makePayExchange(
					DataManager.getInstance().getPlayerPrimaryAccount(player.getName()),
					DataManager.getInstance().getPlayerPrimaryAccount(reciver), amount, reason)) {
				player.sendMessage(ChatColor.GREEN + "Transaction Complete!");
				if (plugin.getServer().getPlayer(reciver) != null) {
					Bukkit.getPlayer(reciver)
							.sendMessage(ChatColor.GREEN + "You've just recieved " + ChatColor.YELLOW + "$"
									+ new java.text.DecimalFormat("0.00").format(amount) + ChatColor.GREEN + " from "
									+ ChatColor.BLUE + player.getName() + " '" + reason + "'");
				}
			} else {
				player.sendMessage(ChatColor.RED + "Insufficient Funds");
			}
			player = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (args.length == 0 || args[0] == "help") {
				sendSenderHelpText(sender);
				return true;
			} else if (args[0].equals("bal") || args[0].equals("balance")) {
				String balancePrompt = ChatColor.GREEN + "Account Balance (Account Num: "
						+ DataManager.getInstance().getPlayerPrimaryAccount(player.getName()) + ") : ";
				String balance = ChatColor.YELLOW + "$" + new java.text.DecimalFormat("0.00")
						.format(DataManager.getInstance().getPlayerBalance(player.getName()));
				player.sendMessage(balancePrompt + balance);
				return true;
			} else if (args[0].equals("send") || args[0].equals("pay")) {

				// create reason from args
				String reason = "Player to Player Transaction";
				if (args.length >= 4) {
					reason = "";
					for (int i = 3; i < args.length; i++) {
						reason += args[i] + " ";
					}
				}

				player = null;
				return sendMoney(sender, args[1], Double.parseDouble(args[2]), reason.trim());

			} else if (player.isOp() && args[0].equals("take")) {
				DataManager.getInstance().makePayExchange(DataManager.getInstance().getPlayerPrimaryAccount(args[1]),
						DataManager.getInstance().getPlayerPrimaryAccount(player.getName()),
						Double.parseDouble(args[2]), "Taken by admin");
				sender.sendMessage("You took $" + args[2] + " from " + args[1]);
				player = null;
				return true;
			} else {
				sendSenderHelpText(sender);
				player = null;
				return true;
			}

		} else {
			sender.sendMessage("Your a Server!");
			return false;
		}
	}

	public void sendSenderHelpText(CommandSender sender) {
		sender.sendMessage(ChatColor.BLUE + "Account Commands:");
		sender.sendMessage("(send,pay) [player] [amount] [message(optional)] : send money to another player");
		sender.sendMessage("(bal,balance) : displays balance");
		sender.sendMessage("help : displays this text");
		if (sender.isOp()) {
			sender.sendMessage("take(ops only) [player] [amount] : takes money from an account into yours");
		}

	}
}
