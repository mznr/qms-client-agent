package com.zafta.client.agent.protocol;

import com.zafta.client.agent.utilities.BytesUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.UnsupportedEncodingException;

@Getter
@Setter
//@AllArgsConstructor
@NoArgsConstructor
public class CounterDisplaySetTextCMDBuilder {

    //private final static String CMD_SET_TEXT_HEADER = "00AA55AA55414441519E";
    private final static String CMD_SET_TEXT_HEADER = "0060616263646566675A0A00AA55AA55414441519E";

    private final static int INDEX_TOTAL_LENGTH = 10;
    public static byte[] buildCMDSetText(String textToBeDisplayed) throws UnsupportedEncodingException {


        /* As we are splitting into 2-byte chunks and
        the only characters that are encoded to UTF-8
        with a Single byte are Space and 0-9 digits,
        then replace these characters with duplicate
        of the same character to have a single algorithm for conversion*/

        //int length = textToBeDisplayed.length();
        String newStr = textToBeDisplayed;
        /*for (int i = 0; i < length; i++) {
            // checks whether the character is not a digit and not a space
            if ((Character.isDigit(textToBeDisplayed.charAt(i)))) {
                //String toBeReplaced = String.valueOf(textToBeDisplayed.charAt(i));
                //newStr = newStr.replace(toBeReplaced, toBeReplaced+toBeReplaced );

            }
        }*/

        for (int i=0;i<10;i++){
            if(textToBeDisplayed.contains(String.valueOf(i))){
                newStr = newStr.replaceAll(String.valueOf(i),String.valueOf(i)+ i);
            }
        }

        textToBeDisplayed = newStr;

        textToBeDisplayed = textToBeDisplayed.replaceAll(" ", "  ");

        byte[] command_header = BytesUtils.toByteArray(CMD_SET_TEXT_HEADER);

        byte[] utf8_bytes = textToBeDisplayed.getBytes("UTF-8");
        byte[][] bytes = BytesUtils.splitBytes(utf8_bytes, 2);
        byte[] text_Hex_bytes = new byte[bytes.length];
        for (int i = 0;i<bytes.length;i++){
            String hexString = BytesUtils.toHexString(bytes[i]);
            text_Hex_bytes[i] =  TextDecoder.charEncoder_UTF_8.get(hexString);
        }

        byte[] lengthByte = new byte[]{(byte) (text_Hex_bytes.length & 0xFF), 0x00};
        command_header[INDEX_TOTAL_LENGTH] = (byte)((command_header[INDEX_TOTAL_LENGTH] + lengthByte[0] + 2) & 0xFF); // +2 for text length (L is two bytes) bytes (TLV)
        byte[] concat_1 = BytesUtils.add(command_header,0,command_header.length,lengthByte,0,lengthByte.length);
        byte[] concat_2 = BytesUtils.add(concat_1,0,concat_1.length,text_Hex_bytes,0,text_Hex_bytes.length);
        byte[] end = new byte[]{(byte) 0x64};
        byte[] command = BytesUtils.add(concat_2,0,concat_2.length,end,0,end.length);


        //System.out.println(BytesUtils.toHexString(command));
        return command;

    }

}
