package com.java.main.utils;

public class StringUtils {

    public static boolean isValidUsername(String username) {
        // Only allow plain characters and underscores
        return username != null && username.matches("^[a-zA-Z0-9_]*$");
    }

    public static int getInt(String inputStr){
        int outputInt;
        try{
            outputInt = Integer.parseInt(inputStr);
        }   catch (NumberFormatException e) {
            return -1;
        }
        return outputInt;
    }

}
