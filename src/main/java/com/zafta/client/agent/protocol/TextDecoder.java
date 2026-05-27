package com.zafta.client.agent.protocol;

import java.util.Map;

import static java.util.Map.entry;

public class TextDecoder {
    public static Map<String,Byte> charEncoder_UTF_8 = Map.ofEntries(

            entry("D8A2", (byte)0xC2),
            entry("D8A7", (byte)0xC7),

            entry("D8A8", (byte)0xC8),
            entry("D9BE", (byte)0x81),

            entry("D8AA", (byte)0xCA),
            entry("D8AB", (byte)0xCB),

            entry("D8AC", (byte)0xCC),
            entry("DA86", (byte)0x8D),

            entry("D8AD", (byte)0xCD),
            entry("D8AE", (byte)0xCE),

            entry("D8AF", (byte)0xCF),
            entry("D8B0", (byte)0xD0),

            entry("D8B1", (byte)0xD1),

            entry("D8B2", (byte)0xD2),
            entry("DA98", (byte)0x8E),

            entry("D8B3", (byte)0xD3),
            entry("D8B4", (byte)0xD4),

            ///

            entry("D8B5", (byte)0xD5),
            entry("D8B6", (byte)0xD6),

            entry("D8B7", (byte)0xD8),
            entry("D8B8", (byte)0xD9),

            entry("D8B9", (byte)0xDA),
            entry("D8BA", (byte)0xDB),

            entry("D981", (byte)0xDD),
            entry("D982", (byte)0xDE),

            entry("DAA9", (byte)0x98),
            entry("DAAF", (byte)0x90),

            entry("D984", (byte)0xE1),
            entry("D985", (byte)0xE3),

            entry("D986", (byte)0xE4),

            entry("D988", (byte)0xE6),
            entry("D987", (byte)0xE5),

            entry("DB8C", (byte)0xED),

            //entry("ئ", (byte)0xD4),

            //
            entry("3030", (byte)0x30),
            entry("3131", (byte)0x31),

            entry("3232", (byte)0x32),
            entry("3333", (byte)0x33),

            entry("3434", (byte)0x34),
            entry("3535", (byte)0x35),

            entry("3636", (byte)0x36),

            entry("3737", (byte)0x37),
            entry("3838", (byte)0x38),

            entry("3939", (byte)0x39),

            entry("2020", (byte)0x20)

    );
}
