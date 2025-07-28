package org.project.utils;

public class RandomUtils {
    public static String generateStringFromEnableCharacter(String characters, int length) {
        // Password contains only alphanumeric characters
        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < length; i++) { // Length
            int index = (int) (Math.random() * characters.length());
            newPassword.append(characters.charAt(index));
        }
        return newPassword.toString();
    }
}
