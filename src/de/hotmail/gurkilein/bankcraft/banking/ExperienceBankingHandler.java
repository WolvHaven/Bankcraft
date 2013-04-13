package de.hotmail.gurkilein.bankcraft.banking;

import org.bukkit.entity.Player;

import de.hotmail.gurkilein.bankcraft.Bankcraft;
import de.hotmail.gurkilein.bankcraft.Util;

public class ExperienceBankingHandler implements BankingHandler<Integer>{

	private Bankcraft bankcraft;

	public ExperienceBankingHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}
	
	@Override
	public boolean transferFromPocketToAccount(Player pocketOwner,
			String accountOwner, Integer amount, Player observer) {
		if (Util.getT >= amount) {
			if (bankcraft.getExperienceDatabaseInterface().getBalance(accountOwner)<= Integer.parseInt(bankcraft.getConfigurationHandler().getString("general.maxBankLimit"))-amount) {
				Integer currentExp = pocketOwner.getTotalExperience();
				pocketOwner.setLevel(0);
				pocketOwner.setExp(0);
				pocketOwner.giveExp(currentExp-amount);
				bankcraft.getExperienceDatabaseInterface().addToAccount(accountOwner, amount);
				bankcraft.getConfigurationHandler().printMessage(observer, "message.depositedSuccessfullyXp", amount+"", accountOwner);
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInAccount", amount+"", accountOwner);
			}
		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInPoket", amount+"", pocketOwner.getName());
		}
		return false;
	}

	@Override
	public boolean transferFromAccountToPocket(String accountOwner,
			   Player pocketOwner, Integer amount, Player observer) {
			  if (bankcraft.getExperienceDatabaseInterface().getBalance(accountOwner) >= amount) {
			   if (Util.getTotalExperience(pocketOwner)<= Integer.parseInt(bankcraft.getConfigurationHandler().getString("general.maxBankLimit"))-amount) {
			    bankcraft.getExperienceDatabaseInterface().removeFromAccount(accountOwner, amount);
			    pocketOwner.giveExp(amount);
			    bankcraft.getConfigurationHandler().printMessage(observer, "message.withdrewSuccessfullyXp", amount+"", accountOwner);
			    return true;
			   } else {
			    bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInPocket", amount+"", pocketOwner.getName());
			   }

		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInAccount", amount+"", accountOwner);
		}
		return false;
	}

	@Override
	public boolean transferFromAccountToAccount(String givingPlayer,
			String gettingPlayer, Integer amount, Player observer) {
		
		if (!bankcraft.getExperienceDatabaseInterface().hasAccount(gettingPlayer)) {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount+"", gettingPlayer);
			return false;
		}
		
		if (bankcraft.getExperienceDatabaseInterface().getBalance(givingPlayer) >= amount) {
			if (bankcraft.getExperienceDatabaseInterface().getBalance(gettingPlayer)<= Integer.parseInt(bankcraft.getConfigurationHandler().getString("general.maxBankLimit"))-amount) {
				bankcraft.getExperienceDatabaseInterface().removeFromAccount(givingPlayer, amount);
				bankcraft.getExperienceDatabaseInterface().addToAccount(gettingPlayer, amount);
				bankcraft.getConfigurationHandler().printMessage(observer, "message.transferedSuccessfullyXp", amount+"", gettingPlayer);
				return true;
			} else {
				bankcraft.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInAccount", amount+"", gettingPlayer);
			}

		} else {
			bankcraft.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInAccount", amount+"", givingPlayer);
		}
		return false;
	}

	@Override
	public boolean grantInterests(Player observer) {
		String messageKey;
		for (String accountName: bankcraft.getExperienceDatabaseInterface().getAccounts()) {
			
			double interest = bankcraft.getConfigurationHandler().getInterestForPlayer(accountName, this);
			int amount = (int)(interest*bankcraft.getExperienceDatabaseInterface().getBalance(accountName));
			
			if (bankcraft.getExperienceDatabaseInterface().getBalance(accountName)<= Integer.parseInt(bankcraft.getConfigurationHandler().getString("general.maxBankLimit"))-amount) {
				bankcraft.getExperienceDatabaseInterface().addToAccount(accountName, amount);
				messageKey = "message.grantedInterestOnXp";
			} else {
				messageKey = "message.couldNotGrantInterestOnXp";
			}
			Player player;
			if (bankcraft.getConfigurationHandler().getString("interest.interestOnXp").equals(true) && (player =bankcraft.getServer().getPlayer(accountName)) != null) {
				bankcraft.getConfigurationHandler().printMessage(player, messageKey, amount+"", player.getName());
			}
		}
		return true;
	}


}
