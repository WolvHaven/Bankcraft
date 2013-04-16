package de.hotmail.gurkilein.bankcraft.banking;

import org.bukkit.entity.Player;

public interface  BankingHandler <X>{

	public boolean depositToAccount(String accountOwner, X amount, Player observer);
	public boolean withdrawFromAccount(String accountOwner, X amount, Player observer);
	public boolean transferFromPocketToAccount (Player pocketOwner, String accountOwner, X amount, Player observer);
	public boolean transferFromAccountToPocket (String accountOwner, Player pocketOwner, X amount, Player observer); 
	public boolean transferFromAccountToAccount(String givingPlayer, String gettingPlayer, X amount, Player observer);
	public boolean grantInterests(Player observer);
	
}
