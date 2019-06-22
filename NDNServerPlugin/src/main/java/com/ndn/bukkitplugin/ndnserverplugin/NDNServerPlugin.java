package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.ndn.bukkitplugin.ndnserverplugin.datautils.DataManager;

public class NDNServerPlugin extends JavaPlugin implements Listener {
    MoneyCommandExecutor mce;
    SimplePaidCommandExecutor spce;
    SignShopListner ssl;
    ProtectionListener pl;
    ChatCencorListner ccl;
    TownCommandExecutor tce;
    DebugCommandExecutor dce;

    @Override
    public void onEnable() {
	Bukkit.getServer().getPluginManager().registerEvents(this, this);

	mce = new MoneyCommandExecutor(this);
	spce = new SimplePaidCommandExecutor(this);
	tce = new TownCommandExecutor(this);
	dce = new DebugCommandExecutor(this);

	ssl = new SignShopListner(this);
	ccl = new ChatCencorListner(this);
	pl = new ProtectionListener(this);
	getServer().getPluginManager().registerEvents(ssl, this);
	getServer().getPluginManager().registerEvents(ccl, this);
	getServer().getPluginManager().registerEvents(pl, this);
	
	getCommand("account").setExecutor(mce);
	getCommand("spawn").setExecutor(spce);
	getCommand("clearweather").setExecutor(spce);
	getCommand("town").setExecutor(tce);
	getCommand("dbinfo").setExecutor(dce);
	getCommand("signshop").setExecutor(ssl);

	recipieFurnace();

	Bukkit.getServer().getPluginManager().registerEvents(new MobMoney(this), this);
	Bukkit.getServer().getPluginManager().registerEvents(new AboutPageUpdater(), this);

	scheduleRepeatAtTime(this, new Runnable() {
	    public void run() {
		Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "Processing daily expenses... Check your Finance tab on the website to view info about your money gained / lost!");
		DataManager.getInstance().executeDailyExpenses();
	    }
	}, 0);
    }

    @Override
    public void onDisable() {
	// TODO Insert logic to be performed when the plugin is disabled
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	switch (cmd.getName()) {
	case "pay":
	    if (args.length > 1) {
		// create reason from args
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
	case "firework":
	    if (sender.isOp() && sender instanceof Player) {
		FireworkManager.makeFireworkAtPlayer(this, (Player) sender);
	    } else {
		sender.sendMessage("You must be an OP");
	    }
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
	    String code = ChatColor.YELLOW + "" + DataManager.getInstance().getPlayerVerificationCode(evt.getPlayer().getName());
	    evt.getPlayer().sendMessage(prompt + code);
	}
    }

    private void recipieFurnace() {
	getServer().addRecipe(new FurnaceRecipe(new ItemStack(Material.LEATHER), Material.ROTTEN_FLESH));

    }

    // Special thanks to "Courier" on the Bukkit.org fourms
    // https://bukkit.org/threads/performing-actions-at-specific-times.103357/
    /**
     * Schedules a task to run at a certain hour every day.
     * 
     * @param plugin The plugin associated with this task
     * @param task   The task to run
     * @param hour   [0-23] The hour of the day to run the task
     * @return Task id number (-1 if scheduling failed)
     */
    public static int scheduleRepeatAtTime(Plugin plugin, Runnable task, int hour) {
	// Calendar is a class that represents a certain time and date.
	Calendar cal = Calendar.getInstance(); // obtains a calendar instance that represents the current time and date

	// time is often represented in milliseconds since the epoch,
	// as a long, which represents how many milliseconds a time is after
	// January 1st, 1970, 00:00.

	// this gets the current time
	long now = cal.getTimeInMillis();
	// you could also say "long now = System.currentTimeMillis()"

	// since we have saved the current time, we need to figure out
	// how many milliseconds are between that and the next
	// time it is 7:00pm, or whatever was passed into hour
	// we do this by setting this calendar instance to the next 7:00pm (or whatever)
	// then we can compare the times

	// if it is already after 7:00pm,
	// we will schedule it for tomorrow,
	// since we can't schedule it for the past.
	// we are not time travelers.
	if (cal.get(Calendar.HOUR_OF_DAY) >= hour)
	    cal.add(Calendar.DATE, 1); // do it tomorrow if now is after "hours"

	// we need to set this calendar instance to 7:00pm, or whatever.
	cal.set(Calendar.HOUR_OF_DAY, hour);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	cal.set(Calendar.MILLISECOND, 0);

	// cal is now properly set to the next time it will be 7:00pm

	long offset = cal.getTimeInMillis() - now;
	long ticks = offset / 50L; // there are 50 milliseconds in a tick

	// we now know how many ticks are between now and the next time it is 7:00pm
	// we schedule an event to go off the next time it is 7:00pm,
	// and repeat every 24 hours.
	return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, ticks, 1728000L);
	// 24 hrs/day * 60 mins/hr * 60 secs/min * 20 ticks/sec = 1728000 ticks
    }
}
