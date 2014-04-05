package de.hotmail.gurkilein.bankcraft.banking;

import java.util.UUID;

import org.bukkit.entity.Player;

public interface  BankingHandler <X>{

	public boolean depositToAccount(UUID accountOwner, X amount, Player observer);
	public boolean withdrawFromAccount(UUID accountOwner, X amount, Player observer);
	public boolean transferFromPocketToAccount (Player pocketOwner, UUID accountOwner, X amount, Player observer);
	public boolean transferFromAccountToPocket (UUID accountOwner, Player pocketOwner, X amount, Player observer); 
	public boolean transferFromAccountToAccount(UUID givingPlayer, UUID gettingPlayer, X amount, Player observer);
	public boolean grantInterests(Player observer);
	
}
