package com.example.dsawyer.maddscore.Utils;

import java.util.Random;

public class UIDGenerator {

    private static char[] charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static Random ran = new Random();

    public static String getUID(int length) {
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < 3; i++)
            sb.append(charSet[ran.nextInt(36)]);
        sb.append("-");

        for (int i = 0; i < length - 3; i++)
            sb.append(charSet[ran.nextInt(36)]);

        return sb.toString();
    }
}
