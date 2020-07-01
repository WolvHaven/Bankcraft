package com.nixholas.centralbank;

import org.bukkit.Location;


public class Util {


    public static Boolean isInteger(String string) {

        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public static Boolean isIntegerBetween(String string, int low, int high) {
        if (high < low) {
            return isIntegerBetween(string, high, low);
        }

        return isInteger(string) && Integer.parseInt(string) >= low && Integer.parseInt(string) <= high;
    }

    public static Boolean isIp(String string) {
        System.out.println(string != null && string != "" && string.split(".") != null);
        return string != null && string != "" && string.split("\\.") != null && string.split("\\.").length == 4 && isIntegerBetween(string.split("\\.")[0], 0, 255) && isIntegerBetween(string.split("\\.")[1], 0, 255) && isIntegerBetween(string.split("\\.")[2], 0, 255) && isIntegerBetween(string.split("\\.")[3], 0, 255);
    }

    //Checks if string is a number and if it's a double.
    public static Boolean isDouble(String string) {

        try {
            Double.parseDouble(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int randomInt(int low, int high) {
        if (low > high) {
            return randomInt(high, low);
        }
        return (int) Math.random() * (high + 1 - low) + low;
    }

    public static double randomDouble(double low, double high) {
        if (low > high) {
            return randomDouble(high, low);
        }
        return Math.random() * (high - low) + low;
    }

    public static boolean isPositive(String input) {
        return (isDouble(input) && Double.parseDouble(input) >= 0);
    }

    public static boolean isInRange(Location searchObject, Location center, int radius) {
        int deltaX = Math.abs(searchObject.getBlockX() - center.getBlockX());
        int deltaY = Math.abs(searchObject.getBlockY() - center.getBlockY());
        int deltaZ = Math.abs(searchObject.getBlockZ() - center.getBlockZ());
        return (Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ))) <= radius;
    }
}
