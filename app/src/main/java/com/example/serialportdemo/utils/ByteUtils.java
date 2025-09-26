package com.example.serialportdemo.utils;

public class ByteUtils {

    ///
    /// Convertir arreglo de bytes a Hex.
    ///
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < bytes.length; i++) {
            sb.append(String.format("0x%02X", bytes[i]));
            if (i < bytes.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
