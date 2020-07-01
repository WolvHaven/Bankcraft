package com.nixholas.centralbank.banking;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.nixholas.centralbank.Bankcraft;


public class DebitorHandler {
	
	private Bankcraft bankcraft;

	public DebitorHandler (Bankcraft bc) {
		this.bankcraft = bc;
	}
	
	private boolean shouldBeDebitor(UUID player) {
		return (bankcraft.getMoneyDatabaseInterface().getBalance(player) < 0
			|| bankcraft.getExperienceDatabaseInterface().getBalance(player) < 0);
	}
	
	public boolean isCurrentlyDebitor(Player p) {
		return Bankcraft.perms.playerInGroup(p, bankcraft.getConfigurationHandler().getString("general.loanGroup"));
	}

	private boolean editPermissions(Player p, boolean isNowDebitor) {
		String loanGroup = bankcraft.getConfigurationHandler().getString("general.loanGroup");
		boolean isCurrentlyDebitor = isCurrentlyDebitor (p);
		
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
		if (!Boolean.getBoolean(bankcraft.getConfigurationHandler().getString("general.useLoanGroup"))) return;
		//Check if player is in debt and update permission group accordingly
		if (shouldBeDebitor(p.getUniqueId())) {
			editPermissions(p,true);
		} else {
			editPermissions(p,false);
		}
	}
	
}
