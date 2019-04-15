package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
			if (plugin.getServer().getPlayer(reciver) != null) {
				if (DataManager.getInstance().makePayExchange(
						DataManager.getInstance().getPlayerPrimaryAccount(player.getName()),
						DataManager.getInstance().getPlayerPrimaryAccount(reciver), amount, reason)) {
					player.sendMessage(ChatColor.GREEN + "Transaction Complete!");
					Bukkit.getPlayer(reciver)
							.sendMessage(ChatColor.GREEN + "You've just recieved " + ChatColor.YELLOW + "$" + amount
									+ ChatColor.GREEN + " from " + ChatColor.BLUE + player.getName() + " '" + reason
									+ "'");
				} else {
					player.sendMessage(ChatColor.RED + "Insufficient Funds");
				}
			} else {
				player.sendMessage(ChatColor.RED + "That player doesn't exist...");
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//TODO: implement take command for OPs
		if (sender instanceof Player) {
			DataManager dataManger = DataManager.getInstance();
			Player player = (Player) sender;

			if (args.length == 0 || args[0] == "help") {
				sendSenderHelpText(sender);
				return true;
			} else if (args[0].equals("bal") || args[0].equals("balance")) {
				String balancePrompt = ChatColor.GREEN + "Account Balance : ";
				String balance = ChatColor.YELLOW + "$" + dataManger.getPlayerBalance(player.getName());
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

				return sendMoney(sender, args[1], Double.parseDouble(args[2]), reason.trim());

			} else if(player.isOp() && args[0].equals("take")) {
				DataManager.getInstance().makePayExchange(DataManager.getInstance().getPlayerPrimaryAccount(args[1]),
						DataManager.getInstance().getPlayerPrimaryAccount(player.getName()), Double.parseDouble(args[2]), "Taken by admin");
				sender.sendMessage("You took $" + args[2] + " from " + args[1]);
				return true;
			} else {
				sendSenderHelpText(sender);
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
