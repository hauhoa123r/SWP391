package org.project.utils;


public final class StringUtils {
    public static boolean check(String data) {
        if(data != null && !data.equals(""))return true;
        else return false;
    }
    public static boolean isNumber(Object value) {
        try {
            Long number = Long.parseLong(value.toString());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
