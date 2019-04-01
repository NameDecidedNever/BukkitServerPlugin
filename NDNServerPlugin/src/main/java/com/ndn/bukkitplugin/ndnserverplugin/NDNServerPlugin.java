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

public class NDNServerPlugin extends JavaPlugin implements Listener{
    @Override
    public void onEnable() {
        // TODO Insert logic to be performed when the plugin is enabled
    	Bukkit.getServer().getPluginManager().registerEvents(this, this);
    }
    
    @Override
    public void onDisable() {
        // TODO Insert logic to be performed when the plugin is disabled
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (sender instanceof Player) {
    		DataManager dataManger = DataManager.getInstance();
            Player player = (Player) sender;
            switch (cmd.getName()) {
            case "account":
            	if(args[0].equals("bal") || args[0].equals("balance")) {
            		String balancePrompt = ChatColor.GREEN + "Account Balance : ";
            		String balance = ChatColor.YELLOW + "$" + dataManger.getPlayerBalance(player.getName());
            		player.sendMessage(balancePrompt + balance);
            	}else if(args[0].equals("send")) {
            		if(getServer().getPlayer(args[1]) != null) {
            		if(dataManger.makePayExchange(dataManger.getPlayerPrimaryAccount(player.getName()), dataManger.getPlayerPrimaryAccount(args[1]), Double.parseDouble(args[2]), args.length > 3 ? args[3] : "Player to Player Transaction")) {            			
            			player.sendMessage(ChatColor.GREEN + "Transaction Complete!");
            			Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GREEN + "You've just recieved " + ChatColor.YELLOW + "$" + args[2] + ChatColor.GREEN + " from " + ChatColor.BLUE + player.getName());
            		} else {
            			player.sendMessage(ChatColor.RED + "Insufficient Funds");
            		}
            		}else {
            			player.sendMessage(ChatColor.RED + "That player doesn't exist...");
            		}
            	}
            	return true;
            case "city":
            	
            case "stock":
            	
            }
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }
    	return false;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
    	DataManager.getInstance().addPlayerIfNotExists(evt.getPlayer().getName());
    	if(!DataManager.getInstance().checkPlayerIsVerified(evt.getPlayer().getName())) {
    		//Player is not verified, we need to give them a verification code.
    		String prompt = ChatColor.GREEN + "Your Website Verification Code : ";
    		String code = ChatColor.YELLOW + "" + DataManager.getInstance().getPlayerVerificationCode(evt.getPlayer().getName());
    		evt.getPlayer().sendMessage(prompt + code);
    	}
    }
}
  