package com.ndn.bukkitplugin.ndnutils;

import java.util.ArrayList;
import java.util.Stack;

public class Utils {
    public static boolean isNumeric(String str) {
	try {
	    Double.parseDouble(str);
	    return true;
	} catch (NumberFormatException e) {
	    return false;
	}
    }

    public static String[] shiftArgs(int shiftAmount, String[] args) {
	String[] newArgs = new String[args.length - shiftAmount];
	for (int i = 0; i < args.length - shiftAmount; i++) {
	    newArgs[i] = args[i + shiftAmount];
	}
	return newArgs;
    }

	public static int lowestFactor(int d) {
		for(int i = d-1; i > 1; i--) {
			if(d % i == 0) {
				return i;
			}
		}
		return d;
	}
}
