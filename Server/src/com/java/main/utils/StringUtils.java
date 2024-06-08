package com.java.main.utils;

public class StringUtils {

    public static boolean isValidUsername(String username) {
        // Only allow plain characters and underscores
        return username != null && username.matches("^[a-zA-Z0-9_]*$");
    }

}
