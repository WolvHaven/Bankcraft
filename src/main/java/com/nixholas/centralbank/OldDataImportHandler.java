package com.nixholas.centralbank;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.nixholas.centralbank.database.mysql.DatabaseManagerMysql;

public class OldDataImportHandler {

	private Bankcraft bankcraft;

	public OldDataImportHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}
	

	public void migratev2_4() {
		String database = bankcraft.getConfigurationHandler().getString("database.typeOfDatabase");
		bankcraft.getLogger().info("Searching for old "+database+" entries...");

		if (database.equalsIgnoreCase("flatfile")) {
			fixFlat();
		} else {
			fixMysql();
		}
		bankcraft.getLogger().info("Finished migrating of old data! Remember to set updateToUUID to false in the config.yml!!!");
	}


	private void fixMysql() {
		String [] currentAccounts = getOldSqlEntries();
		
		for (String account: currentAccounts) {
			updateName(account);
		}
	}


	private void updateName(String account) {
		Connection conn = ((DatabaseManagerMysql)bankcraft.getDatabaseManagerInterface()).getConnection();
	try {

		String sql = "UPDATE `bc_accounts` SET `player_name` = ? WHERE `player_name` = ?";

		PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
		preparedUpdateStatement.setString(1, bankcraft.getUUIDHandler().getUUID(account).toString());
		preparedUpdateStatement.setString(2, account);
		preparedUpdateStatement.executeQuery();

	} catch (SQLException e) {
		e.printStackTrace();
	}
	}


	private String[] getOldSqlEntries() {Connection conn = ((DatabaseManagerMysql)bankcraft.getDatabaseManagerInterface()).getConnection();
	try {

		String sql = "SELECT `player_name` FROM `bc_accounts`";

		PreparedStatement preparedUpdateStatement = conn.prepareStatement(sql);
		ResultSet result = preparedUpdateStatement.executeQuery();

		List <String> resultList = new ArrayList <String> ();

		while (result.next()) {
			try {
				bankcraft.getUUIDHandler().getUUIDwE(result.getString("player_name")).toString();
				resultList.add(result.getString("player_name"));
			} catch (Exception e) {
				try {
					UUID.fromString(result.getString("player_name"));
				} catch (Exception e1) {
					System.out.println("Could not locate "+result.getString("player_name")+" in MC-Database! This check is case sensitive!");
				}
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return null;
	}


	private void fixFlat() {
		File [] files = (new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts")).listFiles();
		String newName = "";
		for (File file: files) {
			try {
				newName = bankcraft.getUUIDHandler().getUUIDwE(file.getName().split("\\.")[0]).toString();
				System.out.println("Renamed "+file.getName()+" to "+newName);
				file.renameTo(new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+newName+".data"));

			} catch (Exception e) {
				try {
					UUID.fromString(file.getName().split("\\.")[0]);
				} catch (Exception e1) {
					System.out.println("Could not locate "+file.getName().split("\\.")[0]+" in MC-Database! This check is case sensitive!");
				}
			}
		}
	}
}
