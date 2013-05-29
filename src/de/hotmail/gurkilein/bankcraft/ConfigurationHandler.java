package de.hotmail.gurkilein.bankcraft;


import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import de.hotmail.gurkilein.bankcraft.banking.BankingHandler;
import de.hotmail.gurkilein.bankcraft.banking.ExperienceBankingHandler;
import de.hotmail.gurkilein.bankcraft.banking.MoneyBankingHandler;

public class ConfigurationHandler {

	private Bankcraft bankcraft;

	public ConfigurationHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
		
		bankcraft.saveDefaultConfig();
	}

	
	public void printMessage(Player player, String messageKey, String amount, String player2) {
		if (bankcraft.getConfig().contains(messageKey)) {
			String message = bankcraft.getConfig().getString(messageKey);

			if (player2 != null && !player2.equals("")) {
				message = message.replaceAll("%player2", player2);
			}
			
			DecimalFormat f = new DecimalFormat("#0.00");
			
			if (amount != null && !amount.equals("")) {
				message = message.replaceAll("%amount", f.format(Double.parseDouble(amount)));
			}

			message = message.replaceAll("%pocketXp", ""+player.getTotalExperience());
			message = message.replaceAll("%pocket", ""+Bankcraft.econ.getBalance(player.getName()));
			
			
			message = message.replaceAll("%balanceXp", ""+bankcraft.getExperienceDatabaseInterface().getBalance(player2));
			message = message.replaceAll("%balance", ""+f.format(bankcraft.getMoneyDatabaseInterface().getBalance(player2)));
			
			message = message.replaceAll("%player", player.getName());
			
			message = message.replaceAll("%interestTimeRemaining", bankcraft.getInterestGrantingTask().getRemainingTime()+"");
			message = message.replaceAll("%interestTimeTotal", bankcraft.getInterestGrantingTask().getTotalTime()+"");
			
			message = message.replaceAll("%rankTableMoney", getRichestPlayers());
			message = message.replaceAll("%rankTableExperience", getExperiencedPlayers());
			
			if (player != null)
			player.sendMessage(getString("chat.color")+getString("chat.prefix")+message);
			
		} else {
			bankcraft.getLogger().severe("Could not locate '"+messageKey+"' in the config.yml inside of the Bankcraft folder!");
			player.sendMessage("Could not locate '"+messageKey+"' in the config.yml inside of the Bankcraft folder!");
		}
	}



	public double getInterestForPlayer(String accountName,
			BankingHandler<?> bankingHandler) {
		//Default interest
		double interest = 0;
		
		//Type specific interest
		if (bankingHandler instanceof MoneyBankingHandler) {
			
			
			//Online/Offline interests
			if (bankcraft.getServer().getPlayer(accountName) != null)
			interest = Double.parseDouble(getString("interest.interestOnMoneyIfOnline"));
			else
			interest = Double.parseDouble(getString("interest.interestOnMoneyIfOffline"));	
			
		} else
		if (bankingHandler instanceof ExperienceBankingHandler) {

			//Online/Offline interests
			if (bankcraft.getServer().getPlayer(accountName) != null)
			interest = Double.parseDouble(getString("interest.interestOnXpIfOnline"));
			else
			interest = Double.parseDouble(getString("interest.interestOnXpIfOffline"));	
			
		}
		
		//Player specific interest
		//TODO
		
		
		return interest;
	}
	
	public String getString(String key) {
		if (!bankcraft.getConfig().contains(key)) {
			bankcraft.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the Bankcraft folder!");
			return "errorCouldNotLocateInConfigYml:"+key;
		} else {
			if (key.toLowerCase().contains("color")) {
				return "§"+bankcraft.getConfig().getString(key);
			}
			return bankcraft.getConfig().getString(key);
		}
	}
	
	private String getRichestPlayers() {
		String result = "";
		HashMap <String,Double> accounts = new HashMap<String,Double>();
		
		for (String name: bankcraft.getMoneyDatabaseInterface().getAccounts()) {
			accounts.put(name, bankcraft.getMoneyDatabaseInterface().getBalance(name));
		}
		
		@SuppressWarnings("unchecked")
		List <Map.Entry<String,Double>> sortedAccounts = sortByComparator(accounts);
		
		for (int i = 0; i<Integer.parseInt(getString("chat.rankTableLength")); i++) {
			result += sortedAccounts.get(i).getKey()+" "+sortedAccounts.get(i).getValue()+System.getProperty("line.separator");
		}
		
		
		return result;
	}
	

	private String getExperiencedPlayers() {
		String result = "";
		HashMap <String,Integer> accounts = new HashMap<String,Integer>();
		
		for (String name: bankcraft.getExperienceDatabaseInterface().getAccounts()) {
			accounts.put(name, bankcraft.getExperienceDatabaseInterface().getBalance(name));
		}
		
		@SuppressWarnings("unchecked")
		List <Map.Entry<String,Integer>> sortedAccounts = sortByComparator(accounts);
		
		for (int i = 0; i<Integer.parseInt(getString("chat.rankTableLength")); i++) {
			result += sortedAccounts.get(i).getKey()+" "+sortedAccounts.get(i).getValue()+System.getProperty("line.separator");
		}
		
		
		return result;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static List sortByComparator(Map unsortMap) {
		 
		List list = new LinkedList(unsortMap.entrySet());
 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
                                       .compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		return list;
	}
}
