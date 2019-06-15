package com.ndn.bukkitplugin.ndnserverplugin.datautils;

import java.util.HashMap;

public class ConstantManager {
	
	public static HashMap<String, Double> constants = new HashMap<String, Double>();
	
	static {
		constants = DataManager.getInstance().getConstantsFromDB();
	}

}
