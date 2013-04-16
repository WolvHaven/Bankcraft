package de.hotmail.gurkilein.bankcraft.database.flatfile;

import de.hotmail.gurkilein.bankcraft.Bankcraft;
import de.hotmail.gurkilein.bankcraft.database.DatabaseManagerInterface;

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
