package com.nixholas.centralbank.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nixholas.centralbank.Bankcraft;
import com.nixholas.centralbank.database.AccountDatabaseInterface;

public class MoneyMysqlInterface implements AccountDatabaseInterface <Double>{

	@SuppressWarnings("unused")
	private Bankcraft bankcraft;
	private Connection conn;

	public MoneyMysqlInterface(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
		this.conn = ((DatabaseManagerMysql)bankcraft.getDatabaseManagerInterface()).getConnection();
	}

	@Override
	public boolean hasAccount(UUID player) {
		      try {
		 
		        String sql = "SELECT `player_name` FROM `bc_accounts` WHERE `player_name` = ?";
		        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
		        preparedUpdateStatement.setString(1, player.toString());
		        
		        
		        ResultSet result = preparedUpdateStatement.executeQuery();
		 
		        while (result.next()) {
		        	return true;
		        }
		      } catch (SQLException e) {
		        e.printStackTrace();
		      }
		      return false;
	}

	@Override
	public boolean createAccount(UUID player) {
		try {
			 
	        String sql = "INSERT INTO `bc_accounts`(`player_name`, `balance`, `balance_xp`) " +
	                     "VALUES(?, ?, ?)";
	        PreparedStatement preparedStatement = conn.prepareStatement(sql);
	        
	        preparedStatement.setString(1, player.toString());
	        preparedStatement.setString(2, "0");
	        preparedStatement.setString(3, "0");
	        
	        preparedStatement.executeUpdate();
	        return true;
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return false;
	}

	@Override
	public Double getBalance(UUID player) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		
	      try {
	 
	        String sql = "SELECT `balance` FROM `bc_accounts` WHERE `player_name` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player.toString());
	        ResultSet result = preparedUpdateStatement.executeQuery();
	 
	        while (result.next()) {
	        	return Double.parseDouble(result.getString("balance"));
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}

	@Override
	public boolean setBalance(UUID player, Double amount) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		
        try {
			String updateSql = "UPDATE `bc_accounts` " +
			        "SET `balance` = ?" +
			        "WHERE `player_name` = ?";
			PreparedStatement preparedUpdateStatement = conn.prepareStatement(updateSql);
			preparedUpdateStatement.setString(1, amount+"");
			preparedUpdateStatement.setString(2, player.toString());
			
			preparedUpdateStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return false;
	}
	
	@Override
	public boolean addToAccount(UUID player, Double amount) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		
		if (amount < 0) {
			return removeFromAccount(player, -amount);
		}
		
		Double currentBalance = getBalance(player);
		if (currentBalance <= Double.MAX_VALUE-amount) {
			setBalance(player, currentBalance+amount);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeFromAccount(UUID player, Double amount) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		
		if (amount < 0) {
			return addToAccount(player, -amount);
		}
		
		Double currentBalance = getBalance(player);
		if (currentBalance >= -Double.MAX_VALUE+amount) {
			setBalance(player, currentBalance-amount);
			return true;
		}
		return false;
	}

	@Override
	public UUID[] getAccounts() {
		
	      Statement query;
	      try {
	        query = conn.createStatement();
	 
	        String sql = "SELECT `player_name` FROM `bc_accounts`";
	        ResultSet result = query.executeQuery(sql);
	 
	        List <UUID> loadingList= new ArrayList <UUID>();
	        while (result.next()) {
	        	loadingList.add(UUID.fromString(result.getString("player_name")));
	        }
	        return loadingList.toArray(new UUID [0]);
	        
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}

}
