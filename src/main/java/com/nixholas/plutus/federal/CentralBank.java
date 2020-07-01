package com.nixholas.plutus.federal;

import com.nixholas.plutus.PlutusCore;

import java.util.UUID;

public class CentralBank {
    private final PlutusCore plutusCore;

    // Central bank will always have 00000000-0000-0000-0000-000000000000
    public static final UUID uuid = new UUID(0,0);

    public CentralBank(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;
    }
    
    public boolean IsEnabled() {
        return plutusCore.getConfigurationHandler().getString("general.centralBank")
                .equalsIgnoreCase("true");
    }
}
