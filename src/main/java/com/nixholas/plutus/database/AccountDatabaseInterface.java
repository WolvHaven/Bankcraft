package com.nixholas.plutus.database;

import java.util.UUID;


public interface AccountDatabaseInterface<X> {


    //Accountmethods
    boolean hasAccount(UUID player);

    boolean createAccount(UUID player);

    X getBalance(UUID player);

    boolean setBalance(UUID player, X amount);

    boolean addToAccount(UUID player, X amount);

    boolean removeFromAccount(UUID player, X amount);

    UUID[] getAccounts();

}
