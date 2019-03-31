package com.ndn.bukkitplugin.ndnserverplugin;

import org.bukkit.Bukkit;
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
            Player player = (Player) sender;
            switch (cmd.getName()) {
            case "account":
            	
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
    		evt.getPlayer().sendMessage("Your Website Verification Code : " + DataManager.getInstance().getPlayerVerificationCode(evt.getPlayer().getName()));
    	}
    }
}
