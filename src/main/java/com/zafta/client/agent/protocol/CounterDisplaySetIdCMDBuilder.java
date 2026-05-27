package com.zafta.client.agent.protocol;


import com.zafta.client.agent.utilities.BytesUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class CounterDisplaySetIdCMDBuilder {
    //private final static String CMD_SET_ID_HEADER = "00AA55AA5541444151AD0000000064";
    //private final static int INDEX_OLD_ID_LAST = 13;
    //private final static int INDEX_NEW_ID_LAST = 11;
    private final static String CMD_SET_ID_HEADER = "0060616263646566675A0F00AA55AA5541444151AD0000000064";

    private final static int INDEX_OLD_ID_LAST = 24;
    private final static int INDEX_NEW_ID_LAST = 22;

    public static byte[] build(int oldId, int newId){
        byte[] counterDisplaySetIDCommandBytes = BytesUtils.fromHex(CMD_SET_ID_HEADER);

        byte[] oldIdBytes = Commons.separateDigitsFromInt(oldId);
        byte[] newIdBytes = Commons.separateDigitsFromInt(newId);


        if(oldIdBytes.length > 2 || newIdBytes.length > 2){
            //Throw Exception: Wrong Input
        }

        for(int i=0;i<oldIdBytes.length;i++){
            counterDisplaySetIDCommandBytes[INDEX_OLD_ID_LAST - i] = oldIdBytes[oldIdBytes.length - 1 - i];
        }

        for(int i=0;i<newIdBytes.length;i++){
            counterDisplaySetIDCommandBytes[INDEX_NEW_ID_LAST - i] = newIdBytes[newIdBytes.length - 1 - i];
        }

        //System.out.println("Counter Display Set Id CMD Before sending: " + BytesUtils.toHexString(counterDisplaySetIDCommandBytes));
        return counterDisplaySetIDCommandBytes;
    }
}
