package com.nixholas.centralbank;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.nixholas.centralbank.UUID.UUIDHandler;
import com.nixholas.centralbank.database.AccountDatabaseInterface;
import com.nixholas.centralbank.database.DatabaseManagerInterface;
import com.nixholas.centralbank.database.SignDatabaseInterface;
import com.nixholas.centralbank.database.flatfile.DatabaseManagerFlatFile;
import com.nixholas.centralbank.database.flatfile.ExperienceFlatFileInterface;
import com.nixholas.centralbank.database.flatfile.MoneyFlatFileInterface;
import com.nixholas.centralbank.database.flatfile.SignFlatFileInterface;
import com.nixholas.centralbank.database.mysql.DatabaseManagerMysql;
import com.nixholas.centralbank.database.mysql.ExperienceMysqlInterface;
import com.nixholas.centralbank.database.mysql.MoneyMysqlInterface;
import com.nixholas.centralbank.database.mysql.SignMysqlInterface;
import com.nixholas.centralbank.tasks.InterestGrantingTask;
import com.nixholas.centralbank.tasks.PlayerPositionTask;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.nixholas.centralbank.banking.BankingHandler;
import com.nixholas.centralbank.banking.DebitorHandler;
import com.nixholas.centralbank.banking.ExperienceBankingHandler;
import com.nixholas.centralbank.banking.InteractionHandler;
import com.nixholas.centralbank.banking.MoneyBankingHandler;

public final class CentralBank extends JavaPlugin{
	public static Logger log;
	public static Economy econ = null;
    public static Permission perms = null;
    public static ExecutorService execService = null;
    
    private AccountDatabaseInterface<Double> moneyDatabaseInterface;
    private AccountDatabaseInterface<Integer> experienceDatabaseInterface;
    private SignDatabaseInterface signDatabaseInterface;
    private ConfigurationHandler configurationHandler;
    private SignHandler signHandler;
    private DebitorHandler debitorHandler;
    private DatabaseManagerInterface databaseManager;
	private BankingHandler<?>[] bankingHandlers;
	private InterestGrantingTask interestGrantingTask;
	private PlayerPositionTask playerPositionTask;
	private InteractionHandler interactionHandler;
	private UUIDHandler uuidHandler;
	private static int taskIdInterest = -1;
	private static int taskIdPlayerPos = -1;

	
    @Override
    public void onEnable(){
    	log = getLogger();
    	log.info("Loading Bankcraft "+getDescription().getVersion()+"... ");
    	
    	//Create Bankcraft folder
    	(new File("plugins"+System.getProperty("file.separator")+"Bankcraft")).mkdir();
    	
    	
    	//Setup Vault for economy and permissions
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!(Is Vault installed?)", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        
        //Load Configuration
        configurationHandler = new ConfigurationHandler(this);

        //Initiate Threadpool
        execService = Executors.newFixedThreadPool(Integer.parseInt(configurationHandler.getString("database.maximumThreads")));
        
        //Setup Database
        if (configurationHandler.getString("database.typeOfDatabase").equalsIgnoreCase("mysql")) {
        	log.info("Using MYSQL as Datasource...");
        	databaseManager = new DatabaseManagerMysql(this);
        	moneyDatabaseInterface = new MoneyMysqlInterface(this);
        	experienceDatabaseInterface = new ExperienceMysqlInterface(this);
        	signDatabaseInterface = new SignMysqlInterface(this);
        } else {
        	//Go for FlatFile
        	log.info("Using FlatFile as Datasource...");
        	databaseManager = new DatabaseManagerFlatFile(this);
        	moneyDatabaseInterface = new MoneyFlatFileInterface(this);
        	experienceDatabaseInterface = new ExperienceFlatFileInterface(this);
        	signDatabaseInterface = new SignFlatFileInterface(this);
        }
        
        //Setup DebitorHandler
        debitorHandler = new DebitorHandler(this);
        
        //Setup BankingHandlers
        bankingHandlers = new BankingHandler[2];
        bankingHandlers[0] = new MoneyBankingHandler(this);
        bankingHandlers[1] = new ExperienceBankingHandler(this);
        
        

        //Setup UUIDHandler
        uuidHandler = new UUIDHandler(this);
        
    	OldDataImportHandler odih = new OldDataImportHandler(this);
    	
        //Fix UUID
        if (configurationHandler.getString("database.updateToUUID").equalsIgnoreCase("true")) {
        	log.info("Changing database to use UUID...");
        	odih.migratev2_4();
        }
        
        //Setup SignHandler
        signHandler = new SignHandler(this);
        interactionHandler = new InteractionHandler(this);
        
        
    	//Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new MinecraftPlayerListener(this), this);
    	pm.registerEvents(new MinecraftBlockListener(this), this);
    	pm.registerEvents(new MinecraftChatListener(this), this);
    	MinecraftCommandListener bcl = new MinecraftCommandListener(this);
    	getCommand("bank").setExecutor(bcl);
    	getCommand("bankadmin").setExecutor(bcl);
    	getCommand("bc").setExecutor(bcl);
    	getCommand("bcadmin").setExecutor(bcl);
        
        
    	//Start Tasks
        toggleTimerTasks();
    	
    	log.info("Bankcraft has been successfully loaded!");
    }
 
	private void toggleTimerTasks() {
    	Integer timerInterest = Integer.parseInt(configurationHandler.getString("interest.timeBetweenInterestsInMinutes"));
    if (taskIdInterest != -1) {
    	getServer().getScheduler().cancelTask(taskIdInterest);
    	taskIdInterest = -1;
    	} else {
    	interestGrantingTask = new InterestGrantingTask(this, timerInterest);
    	taskIdInterest = getServer().getScheduler().scheduleSyncRepeatingTask(this, interestGrantingTask, 1200 ,1200);
    	}
    
	double timerPlayer = Double.parseDouble(configurationHandler.getString("general.secondsBetweenPlayerPositionChecks"));
	if (taskIdPlayerPos != -1) {
		getServer().getScheduler().cancelTask(taskIdPlayerPos);
		taskIdPlayerPos = -1;
		} else {
			if (Boolean.parseBoolean(configurationHandler.getString("general.enableAutoExitSigns"))) {
				playerPositionTask = new PlayerPositionTask(this);
				taskIdPlayerPos = getServer().getScheduler().scheduleSyncRepeatingTask(this, playerPositionTask, (long)timerPlayer*20 , (long)timerPlayer*20);
			}
		}
	}

	@Override
    public void onDisable() {
    	log.info("Bankcraft has been disabled");
    }	
    
    
    
    //Methods for setting up Vault
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
    
    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
    
    
    //Getter for Database Interfaces
    public AccountDatabaseInterface<Double> getMoneyDatabaseInterface() {
    	return moneyDatabaseInterface;
    }
    
    public SignDatabaseInterface getSignDatabaseInterface() {
    	return signDatabaseInterface;
    }
    
    public AccountDatabaseInterface<Integer> getExperienceDatabaseInterface() {
    	return experienceDatabaseInterface;
    }

	public BankingHandler<?>[] getBankingHandlers() {
		return bankingHandlers;
	}

	public ConfigurationHandler getConfigurationHandler() {
		return configurationHandler;
	}

	public SignHandler getSignHandler() {
		return signHandler;
	}
	
	public DebitorHandler getDebitorHandler() {
		return debitorHandler;
	}
	
	public InterestGrantingTask getInterestGrantingTask() {
		return interestGrantingTask;
	}
	
	public DatabaseManagerInterface getDatabaseManagerInterface() {
		return databaseManager;
	}
	
	public InteractionHandler getInteractionHandler() {
		return interactionHandler;
	}
	
	public UUIDHandler getUUIDHandler() {
		return uuidHandler;
	}
	
}
