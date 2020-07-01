package com.nixholas.centralbank.database.flatfile;

import com.nixholas.centralbank.CentralBank;
import com.nixholas.centralbank.database.DatabaseManagerInterface;

public class DatabaseManagerFlatFile implements DatabaseManagerInterface{

	@SuppressWarnings("unused")
	private CentralBank centralBank;

	public DatabaseManagerFlatFile(CentralBank centralBank) {
		this.centralBank = centralBank;
		
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
