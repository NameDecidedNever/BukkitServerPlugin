package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class NDNServerPlugin extends JavaPlugin implements Listener {
	MoneyCommandExecutor mce;
	SimplePaidCommandExecutor spce;
	
	@Override
	public void onEnable() {
		// TODO Insert logic to be performed when the plugin is enabled
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		mce = new MoneyCommandExecutor(this);
		spce = new SimplePaidCommandExecutor(this);
		getCommand("account").setExecutor(mce);
		getCommand("spawn").setExecutor(spce);
		getCommand("clearweather").setExecutor(spce);
	}

	@Override
	public void onDisable() {
		// TODO Insert logic to be performed when the plugin is disabled
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName()) {
		case "pay": 
			if(args.length > 1) {
				//create reason from args
				String reason = "Player to Player Transaction";
				if (args.length >= 3) {
					reason = "";
					for (int i = 2; i < args.length; i++) {
						reason += args[i] + " ";
					}
				}
				
				return mce.sendMoney(sender, args[0], Double.parseDouble(args[1]), reason.trim());
			}
			sender.sendMessage("Invalad Arguments in the pay command, must be /pay [player] [amount]");
			return true;
		}
		return false;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent evt) {
		DataManager.getInstance().addPlayerIfNotExists(evt.getPlayer().getName());
		if (!DataManager.getInstance().checkPlayerIsVerified(evt.getPlayer().getName())) {
			// Player is not verified, we need to give them a verification code.
			String prompt = ChatColor.GREEN + "Your Website Verification Code : ";
			String code = ChatColor.YELLOW + ""
					+ DataManager.getInstance().getPlayerVerificationCode(evt.getPlayer().getName());
			evt.getPlayer().sendMessage(prompt + code);
		}
	}
}
