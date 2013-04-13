package de.hotmail.gurkilein.bankcraft.database;



public interface AccountDatabaseInterface<X> {

	
	//Accountmethods
	public boolean hasAccount(String player);
	public boolean createAccount(String player);
	public X getBalance(String player);
	public boolean setBalance(String player, X amount);
	public boolean addToAccount(String player, X amount);
	public boolean removeFromAccount(String player, X amount);
	public String[] getAccounts();

}
