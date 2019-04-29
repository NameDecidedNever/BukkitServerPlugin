package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.conversations.PlayerNamePrompt;

public class ChunckPremission {
	private String owner;

	HashMap<String, ChunckPerm> permLevel;
	List<Material> restricted_allowedmaterials;

	public ChunckPremission() {
		this("server");
	}

	public ChunckPremission(String owner) {
		this.owner = owner;
		permLevel.put(owner, ChunckPerm.FULL);
		this.permLevel = new HashMap<String, ChunckPerm>();
		this.restricted_allowedmaterials = new ArrayList<Material>();
	}

	public ChunckPremission(String owner, HashMap<String, ChunckPerm> permLevel,
			List<Material> restricted_allowedmaterials) {
		this.owner = owner;
		this.permLevel = permLevel;
		this.restricted_allowedmaterials = restricted_allowedmaterials;
	}

	public boolean isAllowedMine(String playerName) {
		return (permLevel.containsKey(playerName) && permLevel.get(playerName) == ChunckPerm.FULL);
	}

	public boolean isAllowedRightClick(String playerName, Material mat) {
		if (!permLevel.containsKey(playerName) || permLevel.get(playerName) == ChunckPerm.NONE) {
			return false;
		} else if (permLevel.get(playerName) == ChunckPerm.FULL) {
			return true;
		} else {
			if (restricted_allowedmaterials.contains(mat)) {
				return true;
			}
		}
		return false;

	}

	public void addPlayerPerm(String playerName, ChunckPerm perm) {
		permLevel.put(playerName, perm);
	}

	public void addAllowedItem(String material) {
		restricted_allowedmaterials.add(SignShop.getMaterial(material));
	}

	public void addAllowedItem(Material material) {
		restricted_allowedmaterials.add(material);
	}
	
	@Override
	public String toString() {
		return owner;
	}

}
