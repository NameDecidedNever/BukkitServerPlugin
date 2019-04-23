package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.FireworkEffect.Type;

public class FireworkManager {
	
	public static void makeFireworkAtPlayer(Plugin plugin, Player player) {
		makeFirework(plugin, player.getWorld(), player.getLocation());
	}
	
	public static void makeFirework(Plugin plugin, World world, Location loc) {
		 //Spawn the Firework, get the FireworkMeta.
        Firework fw = (Firework) world.spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
       
        //Our random generator
        Random r = new Random();   

        //Get the type
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;       
        if (rt == 1) type = Type.BALL;
        if (rt == 2) type = Type.BALL_LARGE;
        if (rt == 3) type = Type.BURST;
        if (rt == 4) type = Type.CREEPER;
        if (rt == 5) type = Type.STAR;
       
        //Get our random colours   
        Color c1 = Color.fromBGR(r.nextInt(255), r.nextInt(255), r.nextInt(255));
        Color c2 = Color.fromBGR(r.nextInt(255), r.nextInt(255), r.nextInt(255));
       
        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();
        

        //Then apply the effect to the meta
        fwm.addEffect(effect);
       
        //Generate some random power and set it
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);
       
        //Then apply this to our rocket
        fw.setFireworkMeta(fwm);           
	}
}
