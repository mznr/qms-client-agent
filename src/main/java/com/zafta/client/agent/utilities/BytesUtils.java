package com.zafta.client.agent.utilities;

import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public class BytesUtils {
    private static final byte HEX_UPPER_CHAR[] = "0123456789ABCDEF".getBytes();

    private static final byte HEX_LOWER_CHAR[] = "0123456789abcdef".getBytes();

    private static final byte HEX_DECODE_CHAR[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4,
            5, 6, 7, 8, 9, 0, 0, 0, 0, 0, 0, 0, 10, 11, 12, 13, 14, 15, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 10, 11, 12, 13, 14, 15, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0 };

    public static byte[] left(byte b[], int len) {
        return sub(b, 0, len);
    }

    public static byte[] right(byte b[], int len) {
        return sub(b, b.length - len, len);
    }


    public static byte[][] splitBytes(final byte[] data, final int chunkSize)
    {
        final int length = data.length;
        final byte[][] dest = new byte[(length + chunkSize - 1)/chunkSize][];
        int destIndex = 0;
        int stopIndex = 0;

        for (int startIndex = 0; startIndex + chunkSize <= length; startIndex += chunkSize)
        {
            stopIndex += chunkSize;
            dest[destIndex++] = Arrays.copyOfRange(data, startIndex, stopIndex);
        }

        if (stopIndex < length)
            dest[destIndex] = Arrays.copyOfRange(data, stopIndex, length);

        return dest;
    }

    public static byte[] sub(byte b[], int off) {
        return sub(b, off, b.length - off);
    }

    public static byte[] sub(byte b[], int off, int len) {
        byte result[] = new byte[len];
        System.arraycopy(b, off, result, 0, len);
        return result;
    }

    public static String toHexUpper(byte b[]) {
        return toHexUpper(b, 0, b.length);
    }

    public static String toHexUpper(byte b[], int off, int len) {
        byte hex[] = new byte[len * 2];
        for (int i = 0; i < len; i++) {
            hex[i * 2] = HEX_UPPER_CHAR[(b[off + i] & 0xf0) >>> 4];
            hex[i * 2 + 1] = HEX_UPPER_CHAR[b[off + i] & 0xf];
        }

        return new String(hex);
    }

    public static String convertStringToUTF8EncodedHexString(String str) {
        // display in uppercase
        char[] chars = Hex.encodeHex(str.getBytes(StandardCharsets.UTF_8), false);
        // display in lowercase, default
        //char[] chars = Hex.encodeHex(str.getBytes(StandardCharsets.UTF_8));

        return String.valueOf(chars);
    }

    public static void adjust(byte key[]) {
        for (int i = 0; i < key.length; i++) {
            int akku = key[i] & 0xff | 1;
            for (int c = 7; c > 0; c--) {
                akku = akku & 1 ^ akku >> 1;
            }

            key[i] = (byte) (key[i] & 0xfe | akku);
        }

    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    public static String toHexString(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] toByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] fromHex(String hex) {
        byte bHex[] = hex.getBytes();
        byte b[] = new byte[bHex.length / 2];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) (HEX_DECODE_CHAR[bHex[i * 2]] * 16 + HEX_DECODE_CHAR[bHex[i * 2 + 1]]);
        }

        return b;
    }

    public static String nnbvcbjjdajfh(byte data[]) {
        String result = "";
        for (int i = 0; i < data.length; i++) {
            char c = (char) data[i];
            result = (new StringBuilder())
                    .append(result)
                    .append(
                            String
                                    .valueOf(" .,:;'`\"<>()[]{}?/\\!@#$%^&*_-=+|~0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                            .indexOf(c) < 0 ? '.' : c))
                    .toString();
        }

        return result;
    }

    public static byte[] add(byte b1[], int off1, int len1, byte b2[],
                             int off2, int len2) {
        byte b[] = new byte[len1 + len2];
        System.arraycopy(b1, off1, b, 0, len1);
        System.arraycopy(b2, off2, b, len1, len2);
        return b;
    }

    public static byte[] add(byte b[][]) {
        int nLen = 0;
        for (int i = 0; i < b.length; i++) {
            nLen += b[i].length;
        }

        byte lp[] = new byte[nLen];
        nLen = 0;
        for (int i = 0; i < b.length; i++) {
            byte lpi[] = b[i];
            System.arraycopy(lpi, 0, lp, nLen, lpi.length);
            nLen += lpi.length;
        }

        return lp;
    }

    public static byte[] fromInt(int n) {
        byte b[] = fromInt(n, false);;
        if (n < 65535) {
            return Arrays.copyOfRange(b,
                    2, 4);
        } else {
            return Arrays.copyOfRange(b,
                    0, 4);
        }

        //return fromInt(n, false);
    }

    public static byte[] fromInt(int n, boolean bLittleEndian) {
        byte b[] = new byte[4];
        setInt(n, b, 0, bLittleEndian);
        return b;
    }

    public static void setInt(int n, byte b[], int off) {
        setInt(n, b, off, false);
    }

    public static void setInt(int n, byte b[], int off, boolean bLittleEndian) {
        if (bLittleEndian) {
            b[off] = (byte) n;
            b[off + 1] = (byte) (n >>> 8);
            b[off + 2] = (byte) (n >>> 16);
            b[off + 3] = (byte) (n >>> 24);
        } else {
            b[off] = (byte) (n >>> 24);
            b[off + 1] = (byte) (n >>> 16);
            b[off + 2] = (byte) (n >>> 8);
            b[off + 3] = (byte) n;
        }
    }



}

