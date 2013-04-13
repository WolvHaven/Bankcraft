package de.hotmail.gurkilein.bankcraft;


import java.text.DecimalFormat;

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

			if (player2 != null) {
				message = message.replaceAll("%player2", player2);
			}
			
			if (amount != null) {
				message = message.replaceAll("%amount", amount);
			}

			message = message.replaceAll("%pocketXp", ""+player.getTotalExperience());
			message = message.replaceAll("%pocket", ""+Bankcraft.econ.getBalance(player.getName()));
			
			
			DecimalFormat f = new DecimalFormat("#0.00");
			
			message = message.replaceAll("%balanceXp", ""+bankcraft.getExperienceDatabaseInterface().getBalance(player2));
			message = message.replaceAll("%balance", ""+f.format(bankcraft.getMoneyDatabaseInterface().getBalance(player2)));
			
			message = message.replaceAll("%player", player.getName());
			
			
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
			interest = Double.parseDouble(getString("interest.interestOnMoney"));
		} else
		if (bankingHandler instanceof ExperienceBankingHandler) {
			interest = Double.parseDouble(getString("interest.interestOnXp"));
		}
		
		//Player specific interest
		//TODO
		
		
		return interest;
	}
	
	public String getString(String key) {
		if (!bankcraft.getConfig().contains(key)) {
			bankcraft.getLogger().severe("Could not locate '"+key+"' in the config.yml inside of the Bankcraft folder!");
			return "errorCouldNotLocateInConfigYml";
		} else {
			if (key.contains("color")) {
				return "§"+bankcraft.getConfig().getString(key);
			}
			return bankcraft.getConfig().getString(key);
		}
	}
}
