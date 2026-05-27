package com.zafta.client.agent.protocol;


import com.zafta.client.agent.models.counter.CounterDisplayRequest;
import com.zafta.client.agent.utilities.BytesUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CounterDisplayCMDBuilder_7Segment {
    //00 AA 55 AA 55 41 44 41 51 1D xx xx xx yy yy 64
    //AA AA AA DD xx A1 B5 yy yy yy EE EE EE

    // 0060616263646566675A0DAAAAAADD01A1B5010106EEEEEE
    //private final static String CMD_COUNTER_DISPLAY = "AAAAAADD00A1B5000000EEEEEE";
    private final static String CMD_COUNTER_DISPLAY = "0060616263646566675A0DAAAAAADD00A1B5000000EEEEEE";
    private final static int INDEX_COUNTER_ADDRESS_LAST = 15;
    private final static int INDEX_INVOCATION_NUMBER_LAST = 20;
    /*private final static int INDEX_COUNTER_ADDRESS_LAST = 4;
    private final static int INDEX_INVOCATION_NUMBER_LAST = 9;*/

    private CounterDisplayRequest counterDisplayRequest;

    public byte[] build(){
        byte[] counterDisplayCMDBytes = BytesUtils.fromHex(CMD_COUNTER_DISPLAY);

        counterDisplayCMDBytes[INDEX_COUNTER_ADDRESS_LAST] = (byte)(counterDisplayRequest.counterAddress() & 0xFF);
        int invokedNumber = counterDisplayRequest.invokedNumber();
        byte[] invokedNumberBytes = Commons.separateDigitsFromInt(invokedNumber);


        if(invokedNumberBytes.length > 3){
            //Throw Exception: Wrong Input
        }


        for(int i=0;i<invokedNumberBytes.length;i++){
            counterDisplayCMDBytes[INDEX_INVOCATION_NUMBER_LAST - i] = invokedNumberBytes[invokedNumberBytes.length - 1 - i];
        }

        //System.out.println("Counter Display (7-segment) CMD Before send: " + BytesUtils.toHexString(counterDisplayCMDBytes));
        return counterDisplayCMDBytes;
    }
}