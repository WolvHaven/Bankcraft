package de.hotmail.gurkilein.bankcraft;

import org.bukkit.entity.Player;

public class Util {
	
	//Used to calculate the REAL total experience of a player
	public static int getTotalExperience(Player player) {
		int currentLevel = player.getLevel();
		float experienceTowardsNextLevel = player.getExp();
		int experienceFromLevels = 0;
		int experienceNeededFromCurrentLevelToNext = 0;
		
		
		//calculate experience from levels see http://www.minecraftwiki.net/wiki/Experience
		if (currentLevel <= 15) {
			experienceFromLevels = currentLevel*17;
		} else
		if (currentLevel <= 30) {
			experienceFromLevels = (int)((1.5*currentLevel*currentLevel)-(29.5*currentLevel)+360);
		} else {
			experienceFromLevels = (int)((3.5*currentLevel*currentLevel)-(151.5*currentLevel)+2220);
		}
			
		
		//calculate experience needed to level up
		if (currentLevel <= 14) {
			experienceNeededFromCurrentLevelToNext = 17;
		} else
		if (currentLevel <= 29) {
			experienceNeededFromCurrentLevelToNext = (int)((3*currentLevel)-28);
		} else {
			experienceNeededFromCurrentLevelToNext = (int)((7*currentLevel)-148);
		}
			
		//calculate total xp	
		return experienceFromLevels+(int)(experienceTowardsNextLevel*experienceNeededFromCurrentLevelToNext);
	}
	
	
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
