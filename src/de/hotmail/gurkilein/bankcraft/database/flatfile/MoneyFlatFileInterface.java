package de.hotmail.gurkilein.bankcraft.database.flatfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import de.hotmail.gurkilein.bankcraft.Bankcraft;
import de.hotmail.gurkilein.bankcraft.database.AccountDatabaseInterface;

public class MoneyFlatFileInterface implements AccountDatabaseInterface<Double>{

	private Bankcraft bankcraft;

	public MoneyFlatFileInterface(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}
	
	@Override
	public boolean hasAccount(String player) {
		return (new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player.toLowerCase()+".data")).exists();
	}

	@Override
	public boolean createAccount(String player) {
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player.toLowerCase()+".data");
			accountFile.createNewFile();
			
			FileWriter fw = new FileWriter(accountFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("0:0");
			bw.close();
			fw.close();
			return true;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not create Account "+player.toLowerCase()+"!");
		}
		return false;
	}

	@Override
	public Double getBalance(String player) {
		if (!hasAccount(player.toLowerCase())) {
			createAccount(player.toLowerCase());
		}
		
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player.toLowerCase()+".data");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			Double balance = Double.parseDouble(br.readLine().split(":")[1]);
			br.close();
			fr.close();
			return balance;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not get Balance of "+player.toLowerCase()+"!");
		}
		return null;
	}

	@Override
	public boolean setBalance(String player, Double amount) {
		if (!hasAccount(player.toLowerCase())) {
			createAccount(player.toLowerCase());
		}
		
		try {
			File accountFile = new File("plugins"+System.getProperty("file.separator")+"Bankcraft"+System.getProperty("file.separator")+"Accounts"+System.getProperty("file.separator")+player.toLowerCase()+".data");
			
			FileReader fr = new FileReader(accountFile);
			BufferedReader br = new BufferedReader(fr);
			String balances = br.readLine();
			br.close();
			fr.close();
			
			
			FileWriter fw = new FileWriter(accountFile, false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(balances.split(":")[0]+":"+amount);
			bw.close();
			fw.close();
			
			
			return true;
			
		} catch (Exception e) {
			bankcraft.getLogger().severe("Could not set Balance of "+player.toLowerCase()+"!");
		}
		return false;
	}

	@Override
	public boolean addToAccount(String player, Double amount) {
		if (amount < 0) {
			return removeFromAccount(player.toLowerCase(), -amount);
		}
		
		Double currentBalance = getBalance(player.toLowerCase());
		if (currentBalance <= Double.MAX_VALUE-amount) {
			setBalance(player.toLowerCase(), currentBalance+amount);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeFromAccount(String player, Double amount) {

		if (amount < 0) {
			return addToAccount(player.toLowerCase(), -amount);
		}
		
		Double currentBalance = getBalance(player.toLowerCase());
		if (currentBalance-amount >= -Double.MAX_VALUE) {
			setBalance(player.toLowerCase(), currentBalance-amount);
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
