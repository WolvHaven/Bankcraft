package de.hotmail.gurkilein.bankcraft.database.flatfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import de.hotmail.gurkilein.bankcraft.Bankcraft;
import de.hotmail.gurkilein.bankcraft.database.SignDatabaseInterface;

public class SignFlatFileInterface implements SignDatabaseInterface {
	
	private Bankcraft bankcraft;
	private File signFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"SignDatabase"+System.getProperty("file.separator")+"signs.data");

	public SignFlatFileInterface(Bankcraft bankcraft) {
		
		this.bankcraft = bankcraft;
		
		try {
			(new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"SignDatabase")).mkdir();
			signFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getType(int x, int y, int z, World world) {
		try {
		FileReader fr = new FileReader(signFile);
		BufferedReader br = new BufferedReader(fr);
		
		String line;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(x+":"+y+":"+z+":"+world.getName())) {
				br.close();
				fr.close();
				return Integer.parseInt(line.split(":")[4]);
			}
		}
		
		br.close();
		fr.close();
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not get Type of Sign in Database!");
		}
		return -1;
	}

	@Override
	public boolean createNewSign(int x, int y, int z, World world, int type,
			String amount) {
		try {
			
			
			FileWriter fw = new FileWriter(signFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(x+":"+y+":"+z+":"+world.getName()+":"+type+":"+amount+System.getProperty("line.separator"));
			bw.close();
			fw.close();
			return true;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not create Sign in Database!");
		}
		return false;
	}

	@Override
	public boolean addAmount(int x, int y, int z, World world, String amount) {
		try {
		FileReader fr = new FileReader(signFile);
		BufferedReader br = new BufferedReader(fr);
		
		String newFile = "";
		String line;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(x+":"+y+":"+z+":"+world.getName())) {
				newFile += line+":"+amount+System.getProperty("line.separator");
			} else {
				newFile += line+System.getProperty("line.separator");
			}
		}
		
		br.close();
		fr.close();
		
		FileWriter fw = new FileWriter(signFile, false);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(newFile);
		bw.close();
		fw.close();
		return true;
		
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not remove Sign in Database!");
		}
		return false;
	}

	@Override
	public String[] getAmounts(int x, int y, int z, World world) {
		try {
		FileReader fr = new FileReader(signFile);
		BufferedReader br = new BufferedReader(fr);
		
		String line;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(x+":"+y+":"+z+":"+world.getName())) {
				br.close();
				fr.close();
				
				String [] stringArray = line.split(":", 6)[5].split(":");
				
				return stringArray;
			}
		}
		
		br.close();
		fr.close();
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not get Amounts of Sign in Database!");
		}
		return null;
	}

	@Override
	public boolean removeSign(int x, int y, int z, World world) {
		try {
		FileReader fr = new FileReader(signFile);
		BufferedReader br = new BufferedReader(fr);
		
		String newFile = "";
		String line;
		while ((line = br.readLine()) != null) {
			if (line.startsWith(x+":"+y+":"+z+":"+world.getName())) {
				
			} else {
				newFile += line+System.getProperty("line.separator");
			}
		}
		
		br.close();
		fr.close();
		
		FileWriter fw = new FileWriter(signFile, false);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(newFile);
		bw.close();
		fw.close();
		return true;
		
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not remove Sign in Database!");
		}
		return false;
	}

	@Override
	public boolean changeType(int x, int y, int z, Integer type, World world) {
		try {
			FileReader fr = new FileReader(signFile);
			BufferedReader br = new BufferedReader(fr);
			
			String newFile = "";
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(x+":"+y+":"+z+":"+world.getName())) {
					int lengthOfStartString = (x+":"+y+":"+z+":"+world.getName()).length();
					newFile += line.substring(0, lengthOfStartString)+":"+type+line.substring(lengthOfStartString+type.toString().length()+1);
				} else {
					newFile += line+System.getProperty("line.separator");
				}
			}
			
			br.close();
			fr.close();
			
			FileWriter fw = new FileWriter(signFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(newFile);
			bw.close();
			fw.close();
			return true;
			
			} catch (Exception e) {
				bankcraft.getLogger().severe("Could not remove Sign in Database!");
			}
			return false;
	}

	@Override
	public Location[] getLocations(int type, World world) {
		try {
			FileReader fr = new FileReader(signFile);
			BufferedReader br = new BufferedReader(fr);
			
			List <Location> loadingLocationList = new ArrayList <Location> ();
			String line;
			
			while ((line = br.readLine()) != null) {
				String[] lineSplit = line.split(":");
				if (world == null || lineSplit[3].equalsIgnoreCase(world.getName())) {
					if (type == -1 || Integer.parseInt(lineSplit[4]) == type) {
						loadingLocationList.add(new Location(bankcraft.getServer().getWorld(lineSplit[3]), Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]), Integer.parseInt(lineSplit[2])));
					}
				}
			}
			
			br.close();
			fr.close();
			
			if (loadingLocationList.isEmpty()) {
				return new Location[0];
			}
			
			return loadingLocationList.toArray(new Location[0]);
			} catch (Exception e) {
				bankcraft.getLogger().severe("Could not get Locations of Signs in Database!");
			}
			return null;
	}

}
