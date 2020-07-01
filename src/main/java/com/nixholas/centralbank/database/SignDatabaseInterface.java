package com.nixholas.centralbank.database;


import org.bukkit.Location;
import org.bukkit.World;

public interface SignDatabaseInterface {
    //Signdatabasemethods
    int getType(int x, int y, int z, World world);

    boolean createNewSign(int x, int y, int z, World world, int type, String amount);

    boolean addAmount(int x, int y, int z, World world, String amount);

    boolean removeSign(int x, int y, int z, World world);

    boolean changeType(int x, int y, int z, Integer type, World world);

    String[] getAmounts(int x, int y, int z, World world);

    Location[] getLocations(int type, World world);
}
