package com.nixholas.centralbank.database.flatfile;

import com.nixholas.centralbank.Bankcraft;
import com.nixholas.centralbank.database.DatabaseManagerInterface;

public class DatabaseManagerFlatFile implements DatabaseManagerInterface{

	@SuppressWarnings("unused")
	private Bankcraft bankcraft;

	public DatabaseManagerFlatFile(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
		
		setupDatabase();
	}

	@Override
	public boolean setupDatabase() {
		return true;
	}

	@Override
	public boolean closeDatabase() {
		return true;
	}

}
