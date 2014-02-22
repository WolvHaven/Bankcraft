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
			System.out.println("C:"+p.getName()+"D:"+isNowDebitor);
		} else {
			Bankcraft.perms.playerRemoveGroup(p, loanGroup);
			System.out.println("C:"+p.getName()+"D:"+isNowDebitor);
		}
		return true;
	}
	
	public void updateDebitorStatus(Player p) {
		System.out.println("U:"+p.getName()+"S:"+shouldBeDebitor(p.getName().toLowerCase()));
		//Check if player is in debt and update permission group accordingly
		if (shouldBeDebitor(p.getName().toLowerCase())) {
			editPermissions(p,true);
		} else {
			editPermissions(p,false);
		}
	}
	
}
