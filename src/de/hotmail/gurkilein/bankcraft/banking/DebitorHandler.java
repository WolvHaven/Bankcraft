package de.hotmail.gurkilein.bankcraft.banking;

import org.bukkit.entity.Player;

import de.hotmail.gurkilein.bankcraft.Bankcraft;


public class DebitorHandler {
	
	private Bankcraft bankcraft;

	public DebitorHandler (Bankcraft bc) {
		this.bankcraft = bc;
	}
	
	private boolean shouldBeDebitor(String player) {
		return (bankcraft.getMoneyDatabaseInterface().getBalance(player) < 0
			|| bankcraft.getExperienceDatabaseInterface().getBalance(player) < 0);
	}

	private boolean editPermissions(Player p, boolean isNowDebitor) {
		String loanGroup = bankcraft.getConfigurationHandler().getString("general.loanGroup");
		boolean isCurrentlyDebitor = Bankcraft.perms.playerInGroup(p, loanGroup);
		
		if (isNowDebitor == isCurrentlyDebitor) 
			return false;
		
		if (isNowDebitor) {
			Bankcraft.perms.playerAddGroup(p, loanGroup);
		} else {
			Bankcraft.perms.playerRemoveGroup(p, loanGroup);
		}
		return true;
	}
	
	public void updateDebitorStatus(Player p) {
		//Check if player is in debt and update permission group accordingly
		if (shouldBeDebitor(p.getName().toLowerCase())) {
			if (Boolean.getBoolean(bankcraft.getConfigurationHandler().getString("general.remindDebitorOnLogin"))) {
				bankcraft.getConfigurationHandler().printMessage(p, "message.debitor", "0", p.getName());
			}
			editPermissions(p,true);
		} else {
			editPermissions(p,false);
		}
	}
	
}
