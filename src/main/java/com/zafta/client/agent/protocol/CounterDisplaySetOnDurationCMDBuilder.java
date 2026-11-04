package com.zafta.client.agent.protocol;

import com.zafta.client.agent.utilities.BytesUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class CounterDisplaySetOnDurationCMDBuilder {

    /*private final static String CMD_SET_ON_DURATION_HEADER = "00AA55AA5541444151EF00000064";

    private final static int INDEX_ON_DURATION_VALUE_LAST = 12;*/

    private final static String CMD_SET_ON_DURATION_HEADER = "0060616263646566675A0E00AA55AA5541444151EF00000064";
                                                                                 // 00AA55AA5541444151EF00010064 (10 Seconds)
    private final static int INDEX_ON_DURATION_VALUE_LAST = 23;

    public static byte[] build(int duration){
        byte[] counterDisplaySetOnDurationCommandBytes = BytesUtils.fromHex(CMD_SET_ON_DURATION_HEADER);

        byte[] durationValueBytes = Commons.separateDigitsFromInt(duration);


        if(durationValueBytes.length > 3){
            //Throw Exception: Wrong Input
        }

        for(int i=0;i<durationValueBytes.length;i++){
            counterDisplaySetOnDurationCommandBytes[INDEX_ON_DURATION_VALUE_LAST - i] = durationValueBytes[durationValueBytes.length - 1 - i];
        }


        //System.out.println("Counter Display Set On-Duration CMD Before sending: " + BytesUtils.toHexString(counterDisplaySetOnDurationCommandBytes));
        return counterDisplaySetOnDurationCommandBytes;
    }

}
