package com.talearnt.util.common;

public class VerificationUtil {

    public static String makeRandomVerificationNumber(){
        return Integer.toString((int)(Math.random() * (9999 - 1000 + 1)) + 1000);
    }
}
