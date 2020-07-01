package com.nixholas.centralbank.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.nixholas.centralbank.Bankcraft;
import com.nixholas.centralbank.database.SignDatabaseInterface;

public class SignMysqlInterface implements SignDatabaseInterface {

	private Bankcraft bankcraft;
	private Connection conn;

	public SignMysqlInterface(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
		this.conn = ((DatabaseManagerMysql)bankcraft.getDatabaseManagerInterface()).getConnection();
	}

	@Override
	public int getType(int x, int y, int z, World world) {
		
	      try {
	 
	        String sql = "SELECT `type` FROM `bc_signs` WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?";
	        
	        PreparedStatement preparedStatement = conn.prepareStatement(sql);
	        
	        preparedStatement.setString(1, x+"");
	        preparedStatement.setString(2, y+"");
	        preparedStatement.setString(3, z+"");
	        preparedStatement.setString(4, world.getName());
	        ResultSet result = preparedStatement.executeQuery();

	        while (result.next()) {
	        	return Integer.parseInt(result.getString("type"));
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		
		return -1;
	}

	@Override
	public boolean createNewSign(int x, int y, int z, World world, int type,
			String amount) {
		try {
			 
	        String sql = "INSERT INTO `bc_signs`(`x`, `y`, `z`, `world`, `type`, `amount`) " +
	                     "VALUES(?, ?, ?, ?, ?, ?)";
	        PreparedStatement preparedStatement = conn.prepareStatement(sql);
	        
	        preparedStatement.setString(1, x+"");
	        preparedStatement.setString(2, y+"");
	        preparedStatement.setString(3, z+"");
	        preparedStatement.setString(4, world.getName());
	        preparedStatement.setString(5, type+"");
	        preparedStatement.setString(6, amount);
	        
	        preparedStatement.executeUpdate();
	        return true;
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return false;
	}

	@Override
	public boolean addAmount(int x, int y, int z, World world, String amount) {
		try {
			String[] oldAmounts = getAmounts(x,y,z,world);
			String newAmount = "";
			for (String oldAmount: oldAmounts) {
				newAmount += oldAmount+":";
			}
			
			newAmount += amount;
			
			String updateSql = "UPDATE `bc_signs` " +
			        "SET `amount` = ?" +
			        "WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?";
			PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSql);
			
			preparedUpdateStatement.setString(1, newAmount);
			preparedUpdateStatement.setString(2, x+"");
			preparedUpdateStatement.setString(3, y+"");
			preparedUpdateStatement.setString(4, z+"");
			preparedUpdateStatement.setString(5, world.getName());
	        
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean removeSign(int x, int y, int z, World world) {
	      try {
	 
	        String sql = "DELETE FROM `bc_signs` WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?";
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
			
			preparedUpdateStatement.setString(1, x+"");
			preparedUpdateStatement.setString(2, y+"");
			preparedUpdateStatement.setString(3, z+"");
			preparedUpdateStatement.setString(4, world.getName());
	        
			preparedUpdateStatement.executeUpdate();
	 
	        return true;
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		
		return false;
	}

	@Override
	public boolean changeType(int x, int y, int z, Integer type, World world) {
		try {
			String updateSql = "UPDATE `bc_signs` " +
			        "SET `type` = ?" +
			        "WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?";
			PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSql);
			preparedUpdateStatement.setString(1, type+"");
			preparedUpdateStatement.setString(2, x+"");
			preparedUpdateStatement.setString(3, y+"");
			preparedUpdateStatement.setString(4, z+"");
			preparedUpdateStatement.setString(5, world.getName());
			
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String[] getAmounts(int x, int y, int z, World world) {
	      try {
	 
	        String sql = "SELECT `amount` FROM `bc_signs` WHERE `x` = ? AND `y` = ? AND `z` = ? AND `world` = ?";
	        
			PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
			preparedUpdateStatement.setString(1, x+"");
			preparedUpdateStatement.setString(2, y+"");
			preparedUpdateStatement.setString(3, z+"");
			preparedUpdateStatement.setString(4, world.getName());
	        
	        ResultSet result = preparedUpdateStatement.executeQuery();

	        while (result.next()) {
	        	return (result.getString("amount")).split(":");
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
	      return null;
	}

	@Override
	public Location[] getLocations(int type, World world) {
	      try {
	 
	        String sql = "SELECT `x`,`y`,`z`,`world` FROM `bc_signs`";
	        
	        PreparedStatement preparedUpdateStatement = null;
	        if (type != -1 && world != null) {
	        	sql += " WHERE `type` = ? AND `world` = ?";
	        	preparedUpdateStatement = conn.prepareStatement(sql);
				preparedUpdateStatement.setString(1, type+"");
				preparedUpdateStatement.setString(2, world.getName());
	        } else 
	        if (type != -1) {
	        	sql += " WHERE `type` = ?";
	        	preparedUpdateStatement = conn.prepareStatement(sql);
				preparedUpdateStatement.setString(1, type+"");
	        } else
	        if (world != null) {
	        	sql += " WHERE `world` = ?";
	        	preparedUpdateStatement = conn.prepareStatement(sql);
				preparedUpdateStatement.setString(1, world.getName());
	        } else {
	        	preparedUpdateStatement = conn.prepareStatement(sql);
	        }

	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        List <Location> loadingList= new ArrayList <Location>();
	        while (result.next()) {
	        	loadingList.add(new Location(bankcraft.getServer().getWorld(result.getString("world")),Integer.parseInt(result.getString("x")),Integer.parseInt(result.getString("y")),Integer.parseInt(result.getString("z"))));
	        }
	        return loadingList.toArray(new Location [0]);
	        
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}

}
