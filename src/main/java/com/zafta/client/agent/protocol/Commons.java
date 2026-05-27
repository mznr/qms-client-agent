package com.zafta.client.agent.protocol;


import com.zafta.client.agent.utilities.BytesUtils;

public class Commons {

    public static byte[] separateDigitsFromInt(int num){
        String number = String.valueOf(num);
        String separatedDigitsStr = "";
        for(int i = 0; i < number.length(); i++) {
            int j = Character.digit(number.charAt(i), 10);
            // System.out.println("0" + j);
            separatedDigitsStr = separatedDigitsStr + "0" + j;
        }

        //System.out.println("**************************************");
        return BytesUtils.fromHex(separatedDigitsStr);
    }
}
