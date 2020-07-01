package com.nixholas.plutus.database.flatfile;

import com.nixholas.plutus.PlutusCore;
import com.nixholas.plutus.database.DatabaseManagerInterface;

public class DatabaseManagerFlatFile implements DatabaseManagerInterface {

    @SuppressWarnings("unused")
    private final PlutusCore plutusCore;

    public DatabaseManagerFlatFile(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;

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
