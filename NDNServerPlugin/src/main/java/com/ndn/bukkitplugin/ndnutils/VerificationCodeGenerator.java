package com.ndn.bukkitplugin.ndnutils;

import java.util.Random;

public class VerificationCodeGenerator {

	// Graciously contributed by Aaron Glick 2019
	public static String generate() {
		Random rand = new Random();
		String code = "";
		for (int i = 0; i < 2; i++) {
			int num = rand.nextInt(10);
			char letter = (char) (65 + rand.nextInt(26));
			if (rand.nextBoolean()) {
				code += "" + num;
				code += letter;
			} else {
				code += letter;
				code += "" + num;
			}
		}
		return code;
	}

}
