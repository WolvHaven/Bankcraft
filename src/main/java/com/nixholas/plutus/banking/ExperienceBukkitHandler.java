package com.nixholas.plutus.banking;

import org.bukkit.entity.Player;

public class ExperienceBukkitHandler {


    public static void removeExperienceFromPocket(Player player, int amount) {

        int currentExp = getTotalExperience(player);

        player.setLevel(0);
        player.setExp(0);
        player.setTotalExperience(0);

        if (currentExp - amount >= 0)
            player.giveExp(currentExp - amount);


    }

    public static void addExperienceToPocket(Player player, int amount) {
        player.giveExp(amount);
    }


    //Used to calculate the REAL total experience of a player
    public static int getTotalExperience(Player player) {
        int currentLevel = player.getLevel();
        float experienceTowardsNextLevel = player.getExp();
        int experienceFromLevels = 0;
        int experienceNeededFromCurrentLevelToNext = 0;


        //calculate experience from levels see http://www.minecraftwiki.net/wiki/Experience
        if (currentLevel <= 15) {
            experienceFromLevels = currentLevel * 17;
        } else if (currentLevel <= 30) {
            experienceFromLevels = (int) ((1.5 * currentLevel * currentLevel) - (29.5 * currentLevel) + 360);
        } else {
            experienceFromLevels = (int) ((3.5 * currentLevel * currentLevel) - (151.5 * currentLevel) + 2220);
        }


        //calculate experience needed to level up
        if (currentLevel <= 14) {
            experienceNeededFromCurrentLevelToNext = 17;
        } else if (currentLevel <= 29) {
            experienceNeededFromCurrentLevelToNext = (3 * currentLevel) - 28;
        } else {
            experienceNeededFromCurrentLevelToNext = (7 * currentLevel) - 148;
        }

        //calculate total xp
        return experienceFromLevels + (int) (experienceTowardsNextLevel * experienceNeededFromCurrentLevelToNext);
    }
}
