package com.nixholas.centralbank.banking;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.nixholas.centralbank.Bankcraft;

public class MoneyBankingHandler implements BankingHandler<Double>{
	
	private Bankcraft bankcraft;

	public MoneyBankingHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}

	@Override
	public synchronized boolean transferFromPocketToAccount(Player pocketOwner,
			UUID accountOwner, Double amount, Player observer) {
		if (amount <0) return transferFromAccountToPocket(accountOwner, pocketOwner, -amount, observer);
		
		if (Bankcraft.econ.getBalance(pocketOwner) >= amount && bankcraft.getMoneyDatabaseInterface().getBalance(accountOwner)<= Double.MAX_VALUE-amount) {
			if (bankcraft.getMoneyDatabaseInterface().getBalance(accountOwner)<= Double.parseDouble(bankcraft.getConfigurationHandler().getString("general.maxBankLimitMoney"))-amount) {
				Bankcraft.econ.withdrawPlayer(pocketOwner, amount);
				bankcraft.getMoneyDatabaseInterface().addToAccount(accountOwner, amount);
				bankcraft.getConfigurationHandler().printMessage(observer, "message.depositedSuccessfully", amount+"", accountOwner);
				if (bankcraft.getServer().getPlayer(accountOwner) != null) bankcraft.getDebitorHandler().updateDebitorStatus(bankcraft.getServer().getPlayer(accountOwner));
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount+"", accountOwner);
			}
		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInPoket", amount+"", pocketOwner.getUniqueId(), pocketOwner.getName());
		}
		return false;
	}

	@Override
	public synchronized boolean transferFromAccountToPocket(UUID accountOwner,
			Player pocketOwner, Double amount, Player observer) {
		if (amount <0) return transferFromPocketToAccount(pocketOwner, accountOwner, -amount, observer);
		
		if (bankcraft.getMoneyDatabaseInterface().getBalance(accountOwner)+bankcraft.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {
			if (Bankcraft.econ.getBalance(pocketOwner)<= Double.parseDouble(bankcraft.getConfigurationHandler().getString("general.maxPocketLimitMoney"))-amount) {
				bankcraft.getMoneyDatabaseInterface().removeFromAccount(accountOwner, amount);
				Bankcraft.econ.depositPlayer(pocketOwner, amount);
				bankcraft.getConfigurationHandler().printMessage(observer, "message.withdrewSuccessfully", amount+"", accountOwner);
				if (bankcraft.getServer().getPlayer(accountOwner) != null) bankcraft.getDebitorHandler().updateDebitorStatus(bankcraft.getServer().getPlayer(accountOwner));
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInPocket", amount+"", pocketOwner.getUniqueId(), pocketOwner.getName());
			}
		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount+"", accountOwner);
		}
		return false;
	}

	@Override
	public synchronized boolean transferFromAccountToAccount(UUID givingPlayer,
			UUID gettingPlayer, Double amount, Player observer) {
		if (amount <0) return transferFromAccountToAccount(gettingPlayer, givingPlayer, -amount, observer);
		
		if (!bankcraft.getMoneyDatabaseInterface().hasAccount(gettingPlayer)) {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount+"", gettingPlayer);
			return false;
		}
		if (bankcraft.getMoneyDatabaseInterface().getBalance(givingPlayer)+bankcraft.getConfigurationHandler().getLoanLimitForPlayer(givingPlayer, this) >= amount) {
			if (bankcraft.getMoneyDatabaseInterface().getBalance(gettingPlayer)<= Double.parseDouble(bankcraft.getConfigurationHandler().getString("general.maxBankLimitMoney"))-amount) {
				bankcraft.getMoneyDatabaseInterface().removeFromAccount(givingPlayer, amount);
				bankcraft.getMoneyDatabaseInterface().addToAccount(gettingPlayer, amount);
				bankcraft.getConfigurationHandler().printMessage(observer, "message.transferedSuccessfully", amount+"", gettingPlayer);
				if (bankcraft.getServer().getPlayer(givingPlayer) != null) bankcraft.getDebitorHandler().updateDebitorStatus(bankcraft.getServer().getPlayer(givingPlayer));
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount+"", gettingPlayer);
			}

		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount+"", givingPlayer);
		}
		return false;
	}

	@Override
	public synchronized boolean grantInterests(Player observer) {
		String messageKey;
		for (UUID accountName: bankcraft.getMoneyDatabaseInterface().getAccounts()) {
			
			double interest = bankcraft.getConfigurationHandler().getInterestForPlayer(accountName, this, bankcraft.getMoneyDatabaseInterface().getBalance(accountName)<0);
			double amount = interest*bankcraft.getMoneyDatabaseInterface().getBalance(accountName);
			
			if (bankcraft.getMoneyDatabaseInterface().getBalance(accountName)<= Double.parseDouble(bankcraft.getConfigurationHandler().getString("general.maxBankLimitMoney"))-amount) {
				bankcraft.getMoneyDatabaseInterface().addToAccount(accountName, amount);
				messageKey = "message.grantedInterestOnMoney";
			} else {
				messageKey = "message.couldNotGrantInterestOnMoney";
			}
			Player player;
			if ((player =bankcraft.getServer().getPlayer(accountName)) != null && (bankcraft.getConfigurationHandler().getString("interest.broadcastMoney").equals("true") || Bankcraft.perms.has(player, "bankcraft.interest.broadcastmoney"))) {
				bankcraft.getConfigurationHandler().printMessage(player, messageKey, amount+"", player.getUniqueId(), player.getName());
			}
		}
		return true;
	}

	@Override
	public synchronized boolean depositToAccount(UUID accountOwner, Double amount,
			Player observer) {
		if (amount <0) return withdrawFromAccount(accountOwner, -amount, observer);
		
		if (!bankcraft.getMoneyDatabaseInterface().hasAccount(accountOwner)) {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount+"", accountOwner);
			return false;
		}
		
			if (bankcraft.getMoneyDatabaseInterface().getBalance(accountOwner)<= Double.parseDouble(bankcraft.getConfigurationHandler().getString("general.maxBankLimitMoney"))-amount) {
				bankcraft.getMoneyDatabaseInterface().addToAccount(accountOwner, amount);
				if (bankcraft.getServer().getPlayer(accountOwner) != null) bankcraft.getDebitorHandler().updateDebitorStatus(bankcraft.getServer().getPlayer(accountOwner));
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount+"", accountOwner);
			}
		return false;
	}

	@Override
	public synchronized boolean withdrawFromAccount(UUID accountOwner, Double amount,
			Player observer) {
		if (amount <0) return depositToAccount(accountOwner, -amount, observer);
		
		if (!bankcraft.getMoneyDatabaseInterface().hasAccount(accountOwner)) {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount+"", accountOwner);
			return false;
		}
	
	if (bankcraft.getMoneyDatabaseInterface().getBalance(accountOwner)+bankcraft.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {
		
		    bankcraft.getMoneyDatabaseInterface().removeFromAccount(accountOwner, amount);
		    if (bankcraft.getServer().getPlayer(accountOwner) != null) bankcraft.getDebitorHandler().updateDebitorStatus(bankcraft.getServer().getPlayer(accountOwner));
		    return true;
	} else {
		bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount+"", accountOwner);
	}
	
	return false;
	}


}
