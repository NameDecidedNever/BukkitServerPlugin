package com.ndn.bukkitplugin.ndnserverplugin;

public enum ChunckPerm {
	NONE(-1),RESTRICTED(0),FULL(1);
	
	private final int perm;
	
	ChunckPerm(int perm){
		this.perm = perm;
	}
}
