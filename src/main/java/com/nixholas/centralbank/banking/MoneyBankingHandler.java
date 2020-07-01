package com.nixholas.centralbank.banking;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.nixholas.centralbank.CentralBank;

public class MoneyBankingHandler implements BankingHandler<Double>{
	
	private CentralBank centralBank;

	public MoneyBankingHandler(CentralBank centralBank) {
		this.centralBank = centralBank;
	}

	@Override
	public synchronized boolean transferFromPocketToAccount(Player pocketOwner,
			UUID accountOwner, Double amount, Player observer) {
		if (amount <0) return transferFromAccountToPocket(accountOwner, pocketOwner, -amount, observer);
		
		if (CentralBank.econ.getBalance(pocketOwner) >= amount && centralBank.getMoneyDatabaseInterface().getBalance(accountOwner)<= Double.MAX_VALUE-amount) {
			if (centralBank.getMoneyDatabaseInterface().getBalance(accountOwner)<= Double.parseDouble(centralBank.getConfigurationHandler().getString("general.maxBankLimitMoney"))-amount) {
				CentralBank.econ.withdrawPlayer(pocketOwner, amount);
				centralBank.getMoneyDatabaseInterface().addToAccount(accountOwner, amount);
				centralBank.getConfigurationHandler().printMessage(observer, "message.depositedSuccessfully", amount+"", accountOwner);
				if (centralBank.getServer().getPlayer(accountOwner) != null) centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(accountOwner));
				return true;
			} else {
				centralBank.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount+"", accountOwner);
			}
		} else {
			centralBank.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInPoket", amount+"", pocketOwner.getUniqueId(), pocketOwner.getName());
		}
		return false;
	}

	@Override
	public synchronized boolean transferFromAccountToPocket(UUID accountOwner,
			Player pocketOwner, Double amount, Player observer) {
		if (amount <0) return transferFromPocketToAccount(pocketOwner, accountOwner, -amount, observer);
		
		if (centralBank.getMoneyDatabaseInterface().getBalance(accountOwner)+ centralBank.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {
			if (CentralBank.econ.getBalance(pocketOwner)<= Double.parseDouble(centralBank.getConfigurationHandler().getString("general.maxPocketLimitMoney"))-amount) {
				centralBank.getMoneyDatabaseInterface().removeFromAccount(accountOwner, amount);
				CentralBank.econ.depositPlayer(pocketOwner, amount);
				centralBank.getConfigurationHandler().printMessage(observer, "message.withdrewSuccessfully", amount+"", accountOwner);
				if (centralBank.getServer().getPlayer(accountOwner) != null) centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(accountOwner));
				return true;
			} else {
				centralBank.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInPocket", amount+"", pocketOwner.getUniqueId(), pocketOwner.getName());
			}
		} else {
			centralBank.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount+"", accountOwner);
		}
		return false;
	}

	@Override
	public synchronized boolean transferFromAccountToAccount(UUID givingPlayer,
			UUID gettingPlayer, Double amount, Player observer) {
		if (amount <0) return transferFromAccountToAccount(gettingPlayer, givingPlayer, -amount, observer);
		
		if (!centralBank.getMoneyDatabaseInterface().hasAccount(gettingPlayer)) {
			centralBank.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount+"", gettingPlayer);
			return false;
		}
		if (centralBank.getMoneyDatabaseInterface().getBalance(givingPlayer)+ centralBank.getConfigurationHandler().getLoanLimitForPlayer(givingPlayer, this) >= amount) {
			if (centralBank.getMoneyDatabaseInterface().getBalance(gettingPlayer)<= Double.parseDouble(centralBank.getConfigurationHandler().getString("general.maxBankLimitMoney"))-amount) {
				centralBank.getMoneyDatabaseInterface().removeFromAccount(givingPlayer, amount);
				centralBank.getMoneyDatabaseInterface().addToAccount(gettingPlayer, amount);
				centralBank.getConfigurationHandler().printMessage(observer, "message.transferedSuccessfully", amount+"", gettingPlayer);
				if (centralBank.getServer().getPlayer(givingPlayer) != null) centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(givingPlayer));
				return true;
			} else {
				centralBank.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount+"", gettingPlayer);
			}

		} else {
			centralBank.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount+"", givingPlayer);
		}
		return false;
	}

	@Override
	public synchronized boolean grantInterests(Player observer) {
		String messageKey;
		for (UUID accountName: centralBank.getMoneyDatabaseInterface().getAccounts()) {
			
			double interest = centralBank.getConfigurationHandler().getInterestForPlayer(accountName, this, centralBank.getMoneyDatabaseInterface().getBalance(accountName)<0);
			double amount = interest* centralBank.getMoneyDatabaseInterface().getBalance(accountName);
			
			if (centralBank.getMoneyDatabaseInterface().getBalance(accountName)<= Double.parseDouble(centralBank.getConfigurationHandler().getString("general.maxBankLimitMoney"))-amount) {
				centralBank.getMoneyDatabaseInterface().addToAccount(accountName, amount);
				messageKey = "message.grantedInterestOnMoney";
			} else {
				messageKey = "message.couldNotGrantInterestOnMoney";
			}
			Player player;
			if ((player = centralBank.getServer().getPlayer(accountName)) != null && (centralBank.getConfigurationHandler().getString("interest.broadcastMoney").equals("true") || CentralBank.perms.has(player, "bankcraft.interest.broadcastmoney"))) {
				centralBank.getConfigurationHandler().printMessage(player, messageKey, amount+"", player.getUniqueId(), player.getName());
			}
		}
		return true;
	}

	@Override
	public synchronized boolean depositToAccount(UUID accountOwner, Double amount,
			Player observer) {
		if (amount <0) return withdrawFromAccount(accountOwner, -amount, observer);
		
		if (!centralBank.getMoneyDatabaseInterface().hasAccount(accountOwner)) {
			centralBank.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount+"", accountOwner);
			return false;
		}
		
			if (centralBank.getMoneyDatabaseInterface().getBalance(accountOwner)<= Double.parseDouble(centralBank.getConfigurationHandler().getString("general.maxBankLimitMoney"))-amount) {
				centralBank.getMoneyDatabaseInterface().addToAccount(accountOwner, amount);
				if (centralBank.getServer().getPlayer(accountOwner) != null) centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(accountOwner));
				return true;
			} else {
				centralBank.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount+"", accountOwner);
			}
		return false;
	}

	@Override
	public synchronized boolean withdrawFromAccount(UUID accountOwner, Double amount,
			Player observer) {
		if (amount <0) return depositToAccount(accountOwner, -amount, observer);
		
		if (!centralBank.getMoneyDatabaseInterface().hasAccount(accountOwner)) {
			centralBank.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount+"", accountOwner);
			return false;
		}
	
	if (centralBank.getMoneyDatabaseInterface().getBalance(accountOwner)+ centralBank.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {
		
		    centralBank.getMoneyDatabaseInterface().removeFromAccount(accountOwner, amount);
		    if (centralBank.getServer().getPlayer(accountOwner) != null) centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(accountOwner));
		    return true;
	} else {
		centralBank.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount+"", accountOwner);
	}
	
	return false;
	}


}
