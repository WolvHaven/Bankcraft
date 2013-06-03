package de.hotmail.gurkilein.bankcraft;


import java.text.DecimalFormat;
import java.util.ArrayList;
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
			List<String> message = new ArrayList<String>();
			message.add(bankcraft.getConfig().getString(messageKey));

			if (player2 != null && !player2.equals("")) {
				message.set(0, message.get(0).replaceAll("%player2", player2));
			}
			
			DecimalFormat f = new DecimalFormat("#0.00");
			
			if (amount != null && !amount.equals("")) {
				message.set(0, message.get(0).replaceAll("%amount", f.format(Double.parseDouble(amount))));
			}

			message.set(0, message.get(0).replaceAll("%pocketXp", ""+player.getTotalExperience()));
			message.set(0, message.get(0).replaceAll("%pocket", ""+Bankcraft.econ.getBalance(player.getName())));
			
			
			message.set(0, message.get(0).replaceAll("%balanceXp", ""+bankcraft.getExperienceDatabaseInterface().getBalance(player2)));
			message.set(0, message.get(0).replaceAll("%balance", ""+f.format(bankcraft.getMoneyDatabaseInterface().getBalance(player2))));
			
			message.set(0, message.get(0).replaceAll("%player", player.getName()));
			
			message.set(0, message.get(0).replaceAll("%interestTimeRemaining", bankcraft.getInterestGrantingTask().getRemainingTime()+""));
			message.set(0, message.get(0).replaceAll("%interestTimeTotal", bankcraft.getInterestGrantingTask().getTotalTime()+""));
			
			
			if (message.get(0).contains("%rankTableMoney")) {
				message.set(0, message.get(0).replaceAll("%rankTableMoney", ""));
				for (String line: getRichestPlayers()) {
					message.add(line);
				}
			}
			if (message.get(0).contains("%rankTableExperience")) {
				message.set(0, message.get(0).replaceAll("%rankTableExperience", ""));
				for (String line: getExperiencedPlayers()) {
					message.add(line);
				}
			}
			
			if (player != null) {
				player.sendMessage(getString("chat.color")
						+ getString("chat.prefix") + message.get(0));
				for (int i = 1; i < message.size(); i++) {
					player.sendMessage(getString("chat.color")+message.get(i));
				}
			}
			
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
	
	private List<String> getRichestPlayers() {
		List<String> result = new ArrayList<String>();
		HashMap <String,Double> accounts = new HashMap<String,Double>();
		
		for (String name: bankcraft.getMoneyDatabaseInterface().getAccounts()) {
			accounts.put(name, bankcraft.getMoneyDatabaseInterface().getBalance(name));
		}
		
		@SuppressWarnings("unchecked")
		List <Map.Entry<String,Double>> sortedAccounts = sortByComparator(accounts);
		
		for (int i = Math.min(Integer.parseInt(getString("chat.rankTableLength")),sortedAccounts.size())-1; i>=0 ; i--) {
			result.add(sortedAccounts.get(i).getKey()+" "+sortedAccounts.get(i).getValue());
		}
		
		
		return result;
	}
	

	private List<String> getExperiencedPlayers() {
		List<String> result = new ArrayList<String>();
		HashMap <String,Integer> accounts = new HashMap<String,Integer>();
		
		for (String name: bankcraft.getExperienceDatabaseInterface().getAccounts()) {
			accounts.put(name, bankcraft.getExperienceDatabaseInterface().getBalance(name));
		}
		
		@SuppressWarnings("unchecked")
		List <Map.Entry<String,Integer>> sortedAccounts = sortByComparator(accounts);
		
		for (int i = Math.min(Integer.parseInt(getString("chat.rankTableLength")),sortedAccounts.size())-1; i >=0; i--) {
			result.add(sortedAccounts.get(i).getKey()+" "+sortedAccounts.get(i).getValue());
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
