package com.theagilemonkeys.meets.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by kloster on 30/09/13.
 */
public class StringUtils {
    public static String toUpperCaseFirst(String s){
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }

    public static String toLowerCaseFirst(String s) {
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Does the same as String.valueOf but returns an empty string if param is null, instead of
     * raise a NullPointerException.
     * @param o Object to cast to string.
     * @return String
     */
    public static String safeValueOf(Object o){
        return o != null ? String.valueOf(o) : "";
    }

    public static String MD5Hash(String s) {
        String hash = null;

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            hash = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}
