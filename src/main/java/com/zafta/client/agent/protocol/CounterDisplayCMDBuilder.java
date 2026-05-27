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
public class CounterDisplayCMDBuilder {
    //00 AA 55 AA 55 41 44 41 51 1D xx xx xx yy yy 64
    //private final static String CMD_COUNTER_DISPLAY = "00AA55AA55414441512D000000000064"; //AAAAAADD01A1B5010102EEEEEE
    //private final static String CMD_COUNTER_DISPLAY = "00AA55AA55414441512D000000000064";

    private final static String CMD_COUNTER_DISPLAY = "0060616263646566675A1000AA55AA55414441512D000000000064";
    private final static int INDEX_COUNTER_ADDRESS_LAST = 25;
    private final static int INDEX_INVOCATION_NUMBER_LAST = 23;
    // 0060616263646566675A10                                                00AA55AA55414441511D000000000064
    /*private final static int INDEX_COUNTER_ADDRESS_LAST = 14;
    private final static int INDEX_INVOCATION_NUMBER_LAST = 12;*/

    private CounterDisplayRequest counterDisplayRequest;

    public byte[] build(){
        byte[] counterDisplayCMDBytes = BytesUtils.fromHex(CMD_COUNTER_DISPLAY);

        int counterAddress = counterDisplayRequest.counterAddress();
        int invokedNumber = counterDisplayRequest.invokedNumber();

        byte[] counterAddressBytes = Commons.separateDigitsFromInt(counterAddress);
        byte[] invokedNumberBytes = Commons.separateDigitsFromInt(invokedNumber);


        if(counterAddressBytes.length > 2 || invokedNumberBytes.length > 3){
            //Throw Exception: Wrong Input
        }

        for(int i=0;i<counterAddressBytes.length;i++){
            counterDisplayCMDBytes[INDEX_COUNTER_ADDRESS_LAST - i] = counterAddressBytes[counterAddressBytes.length - 1 - i];
        }

        for(int i=0;i<invokedNumberBytes.length;i++){
            counterDisplayCMDBytes[INDEX_INVOCATION_NUMBER_LAST - i] = invokedNumberBytes[invokedNumberBytes.length - 1 - i];
        }

        //System.out.println("Counter Display CMD Before send: " + BytesUtils.toHexString(counterDisplayCMDBytes));
        return counterDisplayCMDBytes;
    }
}
