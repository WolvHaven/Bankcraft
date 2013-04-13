package de.hotmail.gurkilein.bankcraft;


public class Util {
	

	
	
	public static Boolean isInteger(String string) {
		
		try {
			Integer.parseInt(string);
			return true;
		} catch (Exception e){
			return false;
		}
		
	}
	
	public static Boolean isIntegerBetween(String string, int low, int high) {
		if (high<low) {
			return isIntegerBetween(string, high, low);
		}
		
		if (isInteger(string) && Integer.parseInt(string) >= low && Integer.parseInt(string) <= high) {
			return true;
		}
		return false;
	}
	
	public static Boolean isIp(String string) {
		System.out.println(string != null && string != "" && string.split(".") != null);
		if (string != null && string != "" && string.split("\\.") != null && string.split("\\.").length == 4 && isIntegerBetween(string.split("\\.")[0],0,255) && isIntegerBetween(string.split("\\.")[1],0,255) && isIntegerBetween(string.split("\\.")[2],0,255) && isIntegerBetween(string.split("\\.")[3],0,255)) {

			return true;
		}
		return false;
	}
	
	public static Boolean isDouble(String string) {
		
		try {
			Double.parseDouble(string);
			return true;
		} catch (Exception e){
			return false;
		}
	}
	
	public static int randomInt(int low, int high) {
		if (low>high) {
			return randomInt(high,low);
		}
		return (int)Math.random()*(high+1-low)+low;
	}
	
	public static double randomDouble(double low, double high) {
		if (low>high) {
			return randomDouble(high,low);
		}
		return Math.random()*(high-low)+low;
	}
	
	public static boolean isPositive(String input) {
		return (isDouble(input) && Double.parseDouble(input) >= 0);
	}
}
