package com.nixholas.centralbank.banking;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface BankingHandler<X> {

    boolean depositToAccount(UUID accountOwner, X amount, Player observer);

    boolean withdrawFromAccount(UUID accountOwner, X amount, Player observer);

    boolean transferFromPocketToAccount(Player pocketOwner, UUID accountOwner, X amount, Player observer);

    boolean transferFromAccountToPocket(UUID accountOwner, Player pocketOwner, X amount, Player observer);

    boolean transferFromAccountToAccount(UUID givingPlayer, UUID gettingPlayer, X amount, Player observer);

    boolean grantInterests(Player observer);

}
