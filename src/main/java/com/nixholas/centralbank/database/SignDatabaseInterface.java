package com.nixholas.centralbank.database;


import org.bukkit.Location;
import org.bukkit.World;

public interface SignDatabaseInterface {
	//Signdatabasemethods
	public int getType(int x, int y, int z, World world);
	public boolean createNewSign(int x, int y, int z, World world, int type, String amount);
	public boolean addAmount(int x, int y, int z, World world, String amount);
	public boolean removeSign(int x, int y, int z, World world);
	public boolean changeType(int x, int y, int z, Integer type, World world);
	public String[] getAmounts(int x, int y, int z, World world);
	public Location[] getLocations(int type, World world);
}
