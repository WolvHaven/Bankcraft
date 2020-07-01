package com.nixholas.plutus.banking;

import com.nixholas.plutus.PlutusCore;
import org.bukkit.entity.Player;

import java.util.UUID;


public class DebitorHandler {

    private final PlutusCore plutusCore;

    public DebitorHandler(PlutusCore bc) {
        this.plutusCore = bc;
    }

    private boolean shouldBeDebitor(UUID player) {
        return (plutusCore.getMoneyDatabaseInterface().getBalance(player) < 0
                || plutusCore.getExperienceDatabaseInterface().getBalance(player) < 0);
    }

    public boolean isCurrentlyDebitor(Player p) {
        return PlutusCore.perms.playerInGroup(p, plutusCore.getConfigurationHandler().getString("general.loanGroup"));
    }

    private boolean editPermissions(Player p, boolean isNowDebitor) {
        String loanGroup = plutusCore.getConfigurationHandler().getString("general.loanGroup");
        boolean isCurrentlyDebitor = isCurrentlyDebitor(p);

        if (isNowDebitor == isCurrentlyDebitor)
            return false;

        if (isNowDebitor) {
            PlutusCore.perms.playerAddGroup(p, loanGroup);
        } else {
            PlutusCore.perms.playerRemoveGroup(p, loanGroup);
        }
        return true;
    }

    public void updateDebitorStatus(Player p) {
        if (!Boolean.getBoolean(plutusCore.getConfigurationHandler().getString("general.useLoanGroup"))) return;
        //Check if player is in debt and update permission group accordingly
        editPermissions(p, shouldBeDebitor(p.getUniqueId()));
    }

}
