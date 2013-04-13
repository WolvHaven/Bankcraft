package de.hotmail.gurkilein.bankcraft;

import java.io.File;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.hotmail.gurkilein.bankcraft.banking.BankingHandler;
import de.hotmail.gurkilein.bankcraft.banking.ExperienceBankingHandler;
import de.hotmail.gurkilein.bankcraft.banking.MoneyBankingHandler;
import de.hotmail.gurkilein.bankcraft.database.AccountDatabaseInterface;
import de.hotmail.gurkilein.bankcraft.database.SignDatabaseInterface;
import de.hotmail.gurkilein.bankcraft.database.flatfile.ExperienceFlatFileInterface;
import de.hotmail.gurkilein.bankcraft.database.flatfile.MoneyFlatFileInterface;
import de.hotmail.gurkilein.bankcraft.database.flatfile.SignFlatFileInterface;

public final class Bankcraft extends JavaPlugin{
	
	public static Logger log;
	public static Economy econ = null;
    public static Permission perms = null;
    
    private AccountDatabaseInterface<Double> moneyDatabaseInterface;
    private AccountDatabaseInterface<Integer> experienceDatabaseInterface;
    private SignDatabaseInterface signDatabaseInterface;
    private ConfigurationHandler configurationHandler;
    private SignHandler signHandler;
	private BankingHandler<?>[] bankingHandlers;
	private static int taskId = -1;

    @Override
    public void onEnable(){
    	log = getLogger();
    	log.info("[Bankcraft] Loading Bankcraft "+getDescription().getVersion()+"... ");
    	
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
        
        //Setup Database
        if (configurationHandler.getString("database.typeOfDatabase").equalsIgnoreCase("flatfile")) {
        	moneyDatabaseInterface = new MoneyFlatFileInterface(this);
        	experienceDatabaseInterface = new ExperienceFlatFileInterface(this);
        	signDatabaseInterface = new SignFlatFileInterface(this);
        }
        
        //Setup BankingHandlers
        bankingHandlers = new BankingHandler[2];
        bankingHandlers[0] = new MoneyBankingHandler(this);
        bankingHandlers[1] = new ExperienceBankingHandler(this);
        
        
        //Setup SignHandler
        signHandler = new SignHandler(this);
        
        
    	//Register Listeners
    	PluginManager pm = getServer().getPluginManager();
    	pm.registerEvents(new MinecraftPlayerListener(this), this);
    	pm.registerEvents(new MinecraftBlockListener(this), this);
    	MinecraftCommandListener bcl = new MinecraftCommandListener(this);
    	getCommand("bank").setExecutor(bcl);
    	getCommand("bankadmin").setExecutor(bcl);
    	getCommand("bc").setExecutor(bcl);
    	getCommand("bcadmin").setExecutor(bcl);
        
        
    	//Start InterestTimerTask
        toggleTimerTask();
    	
    	log.info("Bankcraft has been successfully loaded! ;D");
    }
 
    @SuppressWarnings("deprecation")
	private void toggleTimerTask() {
    	Integer timer = Integer.parseInt(configurationHandler.getString("interest.timeBetweenInterestsInMinutes"));
    if (taskId != -1) {
    	getServer().getScheduler().cancelTask(taskId);
    	taskId = -1;
    	} else {
    	taskId = getServer().getScheduler().scheduleAsyncRepeatingTask(
    	this, new InterestGrantingTask(this), 1200 * timer,
    	1200 * timer);
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
	
}
