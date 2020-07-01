package com.nixholas.centralbank.database.mysql;

import com.nixholas.centralbank.CentralBank;
import com.nixholas.centralbank.database.DatabaseManagerInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManagerMysql implements DatabaseManagerInterface {

    private Connection conn = null;

    // Hostname
    private String dbHost;

    // Port -- Standard: 3306
    private String dbPort;

    // Databankname
    private String database;

    // Databank username
    private String dbUser;

    // Databank password
    private String dbPassword;

    private final CentralBank centralBank;

    public DatabaseManagerMysql(CentralBank centralBank) {
        this.centralBank = centralBank;

        setupDatabase();
    }

    @Override
    public boolean setupDatabase() {
        try {
            //Load Drivers
            Class.forName("com.mysql.jdbc.Driver");

            dbHost = centralBank.getConfigurationHandler().getString("database.mysql.host");
            dbPort = centralBank.getConfigurationHandler().getString("database.mysql.port");
            database = centralBank.getConfigurationHandler().getString("database.mysql.databaseName");
            dbUser = centralBank.getConfigurationHandler().getString("database.mysql.user");
            dbPassword = centralBank.getConfigurationHandler().getString("database.mysql.password");

            //Connect to database
            conn = DriverManager.getConnection("jdbc:mysql://" + dbHost + ":"
                    + dbPort + "/" + database + "?" + "user=" + dbUser + "&"
                    + "password=" + dbPassword);

        } catch (ClassNotFoundException e) {
            System.out.println("Could not locate drivers");
            return false;
        } catch (SQLException e) {
            System.out.println("Could not connect");
            return false;
        }

        //Create tables if needed
        Statement query;
        try {
            query = conn.createStatement();

            String accounts = "CREATE TABLE IF NOT EXISTS `bc_accounts` (id int(10) AUTO_INCREMENT, player_name varchar(50) NOT NULL UNIQUE, balance DOUBLE(30,2) NOT NULL, balance_xp DOUBLE(30,2) NOT NULL, PRIMARY KEY(id));";
            String signs = "CREATE TABLE IF NOT EXISTS `bc_signs` (id int(10) AUTO_INCREMENT, x int(10) NOT NULL, y int(10) NOT NULL, z int(10) NOT NULL, world varchar(100) NOT NULL, type int(10) NOT NULL, amount varchar(250) NOT NULL, PRIMARY KEY(id));";
            query.executeUpdate(accounts);
            query.executeUpdate(signs);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        CentralBank.log.info("Mysql has been set up!");
        return true;
    }

    public Connection getConnection() {
        return conn;
    }

    @Override
    public boolean closeDatabase() {
        try {
            conn.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
