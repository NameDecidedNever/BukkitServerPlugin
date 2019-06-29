package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.ConstantManager;
import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;
import com.ndn.bukkitplugin.ndnutils.TeleportLogic;

public class DebugCommandExecutor implements CommandExecutor {

    private final NDNServerPlugin plugin;

    public Location[] waypoints = {
    		new Location(Bukkit.getWorld("world"), 1140, 67, 1543),
    		new Location(Bukkit.getWorld("world"), 1135, 96, 1543),
    		new Location(Bukkit.getWorld("world"), 1135, 96, 1538),
    		new Location(Bukkit.getWorld("world"), 1140, 96, 1538)
    };
    
    public LivingEntity[] targets = new LivingEntity[4];
    
    public static Zombie z = null;
    
    public static int targetNum = 0;
    
    public static Player p;
    public static double seenPerc = 0.0;
    public static BossBar bar = null;

    public DebugCommandExecutor(NDNServerPlugin plugin) {
	this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
	if(sender.isOp()) {
	    if(command.getName().equals("dbinfo")) {
		long secondsSinceStart = DataManager.getInstance().getConnectionLifetime();
		int day = (int)TimeUnit.SECONDS.toDays(secondsSinceStart);        
	        long hours = TimeUnit.SECONDS.toHours(secondsSinceStart) - (day *24);
	        long minute = TimeUnit.SECONDS.toMinutes(secondsSinceStart) - (TimeUnit.SECONDS.toHours(secondsSinceStart)* 60);
	        long second = TimeUnit.SECONDS.toSeconds(secondsSinceStart) - (TimeUnit.SECONDS.toMinutes(secondsSinceStart) *60);
		sender.sendMessage(ChatColor.GREEN + "DB Connection Lifetime = " + ChatColor.YELLOW + day + " days, " + hours + "h" + minute + "m" + second + "s");
	    }else if(command.getName().equals("executeexpenses")) {
	    	Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "A server operator manually executed daily expenses. If this is not for testing, maybe start to panic!");
	    	DataManager.getInstance().executeDailyExpenses();
	    }else if(command.getName().equals("zombiepatrol")) {
//	    	p = (Player) sender;
//	    	z = (Zombie) Bukkit.getWorld("world").spawnEntity(waypoints[0], EntityType.ZOMBIE);
//			targets[0] = (LivingEntity) Bukkit.getWorld("world").spawnEntity(waypoints[0], EntityType.ARMOR_STAND);
//			targets[1] = (LivingEntity) Bukkit.getWorld("world").spawnEntity(waypoints[1], EntityType.ARMOR_STAND);
//			targets[2] = (LivingEntity) Bukkit.getWorld("world").spawnEntity(waypoints[2], EntityType.ARMOR_STAND);
//			targets[3] = (LivingEntity) Bukkit.getWorld("world").spawnEntity(waypoints[3], EntityType.ARMOR_STAND);
//			Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
//				
//				@Override
//				public void run() {
//					if(z != null) {
//						targetNum++;
//						if(targetNum == 4) {
//							targetNum = 0;
//						}
//						z.setTarget(targets[targetNum]);
//					}
//				}
//			}, 20, 60);
//			bar = Bukkit.createBossBar("Detection", BarColor.BLUE, BarStyle.SOLID);
//			bar.addPlayer(p);
//			Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
//				@Override
//				public void run() {
//					if(p != null && bar != null) {
//						if(z.hasLineOfSight(p)) {
//							if(seenPerc <= 0.8) {
//								seenPerc += 0.2;
//							}else {
//								seenPerc += 1.0 - seenPerc;
//							}
//						}else if(seenPerc > 0.05){
//							seenPerc -= 0.05;
//						}
//						if(seenPerc > 1.0) {
//							seenPerc = 1.0;
//						}
//						bar.setProgress(seenPerc);
//					}
//				}
//			}, 5, 5);
	    }
	}else {
	    sender.sendMessage(ChatColor.RED + "You must be oped in order to use debug commands!");
	}
	return true;
    }

}
