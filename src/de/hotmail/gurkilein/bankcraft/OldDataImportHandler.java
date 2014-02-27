package de.hotmail.gurkilein.bankcraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.hotmail.gurkilein.bankcraft.database.mysql.DatabaseManagerMysql;


public class OldDataImportHandler {

	private Bankcraft bankcraft;

	public OldDataImportHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}
	
	
	private Double migrateSpecificMoney_mysql (String player) {
		Connection conn = ((DatabaseManagerMysql)bankcraft.getDatabaseManagerInterface()).getConnection();
		try {
			 
	        String sql = "SELECT `balance` FROM `bc_accounts` WHERE `player_name` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player);
	        ResultSet result = preparedUpdateStatement.executeQuery();
	        
	        //No delete due to line sharing
	        while (result.next()) {
	        	return Double.parseDouble(result.getString("balance"));
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}
	
	private Integer migrateSpecificExp_mysql (String player) {
		Connection conn = ((DatabaseManagerMysql)bankcraft.getDatabaseManagerInterface()).getConnection();
		try {
			 
	        String sql = "SELECT `balance_xp` FROM `bc_accounts` WHERE `player_name` = ?";
	        
	        PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
	        preparedUpdateStatement.setString(1, player);
	        ResultSet result = preparedUpdateStatement.executeQuery();
	        
	 
	        String sql2 = "DELETE FROM `bc_accounts` WHERE `player_name` = ?";
	        
	        PreparedStatement preparedDeleteStatement = conn.prepareStatement(sql2);
	        preparedDeleteStatement.setString(1, player);
	        preparedDeleteStatement.executeQuery();
	        
	        while (result.next()) {
	        	return Integer.parseInt(result.getString("balance_xp"));
	        }
	      } catch (SQLException e) {
	        e.printStackTrace();
	      }
		return null;
	}
	
	
	private Double migrateSpecificMoney_flatfile (String player) {
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".data");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			Double balance = Double.parseDouble(br.readLine().split(":")[1]);
			br.close();
			fr.close();
			//No delete due to file sharing
			return balance;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not get Balance of "+player+"!");
		}
		return null;
	}
	
	private Integer migrateSpecificExp_flatfile (String player) {
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".data");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			Integer balance = Integer.parseInt(br.readLine().split(":")[0]);
			br.close();
			fr.close();
			accountFile.delete();
			return balance;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not get Balance of "+player+"!");
		}
		return null;
	}

	public boolean migratev2_3() {
		String database = bankcraft.getConfigurationHandler().getString("database.typeOfDatabase");
		bankcraft.getLogger().info("Migrating data from pre 2.3...");
		bankcraft.getLogger().info("Searching for old "+database+" entries...");
		
		{
			bankcraft.getLogger().info("Migrating money data...");
			String [] accounts = bankcraft.getMoneyDatabaseInterface().getAccounts();
			
			for (String account : accounts) {
				if (!account.equals(account.toLowerCase())) {
					try {
						bankcraft.getLogger().info("Found '"+account+"' migrating...");
						Double amount;
						if (database.toLowerCase().equals("mysql"))
							amount = migrateSpecificMoney_mysql(account);
						else
							amount = migrateSpecificMoney_flatfile(account);
						bankcraft.getMoneyDatabaseInterface().addToAccount(account.toLowerCase(), amount);
					} catch (Exception e) {
						e.printStackTrace();
					}
					}
			}
		}
		
		{
			bankcraft.getLogger().info("Migrating experience data...");
			String [] accounts = bankcraft.getMoneyDatabaseInterface().getAccounts();
			
			for (String account : accounts) {
				if (!account.equals(account.toLowerCase())) {
					try {
						Integer amount;
						if (database.toLowerCase().equals("mysql"))
							amount = migrateSpecificExp_mysql(account);
						else
							amount = migrateSpecificExp_flatfile(account);
						bankcraft.getExperienceDatabaseInterface().addToAccount(account.toLowerCase(), amount);
						} catch (Exception e) {
						e.printStackTrace();
					}
					}
			}
		}
		bankcraft.getLogger().info("Finished migrating of old data! Remember to set eliminateCaseSensitives to false in the config.yml!!!");
		return true;
	}
	
	
	//Migration for v1.X
	public boolean importOldData() {
		bankcraft.getLogger().info("Searching for flatfile data...");
		{
		//Import Money Data
		bankcraft.getLogger().info("Importing money data...");
		
		File accountsFolder = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts");
		double amount;
		String line;
		
		for (File playerFile: accountsFolder.listFiles()) {
			if (playerFile.getName().endsWith("db")) {
			try {
			FileReader fr = new FileReader(playerFile);
			BufferedReader br = new BufferedReader(fr);
			line = br.readLine();
			if (line.equals("0.00")) 
			amount = 0;	
			else
			amount = Double.parseDouble(br.readLine());
			br.close();
			fr.close();
			
			bankcraft.getMoneyDatabaseInterface().setBalance(playerFile.getName().split("\\.")[0], amount);
			} catch (Exception e) {
				bankcraft.getLogger().info("Could not import data from "+playerFile.getName());
				e.printStackTrace();
			}
			}
		}
		}
		
		
		{
		//Import Experience Data
		bankcraft.getLogger().info("Importing money data...");
		
		File xpAccountsFolder = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"XPAccounts");
		int amount;
		String line;
		
		for (File playerFile: xpAccountsFolder.listFiles()) {
			
			if (playerFile.getName().endsWith("db")) {
			try {
			FileReader fr = new FileReader(playerFile);
			BufferedReader br = new BufferedReader(fr);
			line = br.readLine();
			if (line.equals("0.00")) 
			amount = 0;	
			else
			amount = Integer.parseInt(line);
			br.close();
			fr.close();
			
			bankcraft.getExperienceDatabaseInterface().setBalance(playerFile.getName().split("\\.")[0], amount);
			} catch (Exception e) {
				bankcraft.getLogger().info("Could not import data from "+playerFile.getName());
				e.printStackTrace();
			}
			}
		}
		}
		
		{
		//Import Sign Data
		bankcraft.getLogger().info("Importing sign data...");
		
		File signFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"banks.db");
		String line;
		String [] lineArray;
		
		try{
			
		FileReader fr = new FileReader(signFile);
		BufferedReader br = new BufferedReader(fr);
		
		while ((line = br.readLine()) != null) {
			lineArray = line.split(":",6);
			bankcraft.getSignDatabaseInterface().createNewSign(Integer.parseInt(lineArray[0]), Integer.parseInt(lineArray[1]), Integer.parseInt(lineArray[2]), bankcraft.getServer().getWorld(lineArray[3]), Integer.parseInt(lineArray[4]), lineArray[5]);

		}
		
		
		br.close();
		fr.close();
		} catch (Exception e) {
			bankcraft.getLogger().info("Could not import sign data");
			e.printStackTrace();
		}

		
	}
		bankcraft.getLogger().info("Finished import of old data! Remember to set importOldData to false in the config.yml!!!");
		return true;
	}
}
