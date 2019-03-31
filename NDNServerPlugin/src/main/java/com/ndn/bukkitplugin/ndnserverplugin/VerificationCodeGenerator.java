package com.ndn.bukkitplugin.ndnserverplugin;

import java.util.Random;

public class VerificationCodeGenerator {
	
	//Graciously contributed by Aaron Glick 2019
	public static String generate(){
        Random rand = new Random();
        Random randChar = new Random();
        String code = "";
        for (int i = 0; i < 2; i++) {
            int num = rand.nextInt(10);
            char letter = (char) (65 + randChar.nextInt(26));
            int randomOrder = rand.nextInt(20);
            if(randomOrder > 10){
                code+= "" + num;
                code+=letter;
            }
            else if(randomOrder<=10){
                code+=letter;
                code+= "" + num;
            }
        }
        return code;
    }

}
