package de.hotmail.gurkilein.bankcraft.database.flatfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import de.hotmail.gurkilein.bankcraft.Bankcraft;
import de.hotmail.gurkilein.bankcraft.database.AccountDatabaseInterface;

public class ExperienceFlatFileInterface implements
		AccountDatabaseInterface<Integer> {

	
	private Bankcraft bankcraft;

	public ExperienceFlatFileInterface(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
		System.out.println("blub");
    	(new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts")).mkdir();
	}
	
	@Override
	public boolean hasAccount(String player) {
		return (new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".data")).exists();
	}

	@Override
	public boolean createAccount(String player) {
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".data");
			accountFile.createNewFile();
			
			FileWriter fw = new FileWriter(accountFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("0:0");
			bw.close();
			fw.close();
			return true;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not create Account "+player+"!");
		}
		return false;
	}

	@Override
	public Integer getBalance(String player) {
		
		if (!hasAccount(player)) {
			createAccount(player);
		}
		
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".data");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			Integer balance = Integer.parseInt(br.readLine().split(":")[0]);
			br.close();
			fr.close();
			return balance;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not get Balance of "+player+"!");
		}
		return null;
	}

	@Override
	public boolean setBalance(String player, Integer amount) {
		if (!hasAccount(player)) {
			createAccount(player);
		}
		
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player+".data");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			String balances = br.readLine();
			br.close();
			fr.close();
			
			
			FileWriter fw = new FileWriter(accountFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(amount+":"+balances.split(":")[1]);
			bw.close();
			fw.close();
			
			
			return true;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not set Balance of "+player+"!");
		}
		return false;
	}

	@Override
	public boolean addToAccount(String player, Integer amount) {
		
		if (amount < 0) {
			return removeFromAccount(player, -amount);
		}
		
		Integer currentBalance = getBalance(player);
		if (currentBalance <= Integer.MAX_VALUE-amount) {
			setBalance(player, currentBalance+amount);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeFromAccount(String player, Integer amount) {
		
		if (amount < 0) {
			return addToAccount(player, -amount);
		}
		
		Integer currentBalance = getBalance(player);
		if (currentBalance >= Integer.MIN_VALUE+amount) {
			setBalance(player, currentBalance-amount);
			return true;
		}
		return false;
	}

	@Override
	public String[] getAccounts() {
		String [] fileNames =  (new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts")).list();
		for (int i = 0; i< fileNames.length; i++) {
			fileNames[i] = fileNames[i].split("\\.")[0];
		}
		return fileNames;
	}

}
