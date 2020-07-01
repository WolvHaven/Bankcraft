package com.nixholas.centralbank.banking;

import com.nixholas.centralbank.CentralBank;
import org.bukkit.entity.Player;

import java.util.UUID;


public class DebitorHandler {

    private final CentralBank centralBank;

    public DebitorHandler(CentralBank bc) {
        this.centralBank = bc;
    }

    private boolean shouldBeDebitor(UUID player) {
        return (centralBank.getMoneyDatabaseInterface().getBalance(player) < 0
                || centralBank.getExperienceDatabaseInterface().getBalance(player) < 0);
    }

    public boolean isCurrentlyDebitor(Player p) {
        return CentralBank.perms.playerInGroup(p, centralBank.getConfigurationHandler().getString("general.loanGroup"));
    }

    private boolean editPermissions(Player p, boolean isNowDebitor) {
        String loanGroup = centralBank.getConfigurationHandler().getString("general.loanGroup");
        boolean isCurrentlyDebitor = isCurrentlyDebitor(p);

        if (isNowDebitor == isCurrentlyDebitor)
            return false;

        if (isNowDebitor) {
            CentralBank.perms.playerAddGroup(p, loanGroup);
        } else {
            CentralBank.perms.playerRemoveGroup(p, loanGroup);
        }
        return true;
    }

    public void updateDebitorStatus(Player p) {
        if (!Boolean.getBoolean(centralBank.getConfigurationHandler().getString("general.useLoanGroup"))) return;
        //Check if player is in debt and update permission group accordingly
        editPermissions(p, shouldBeDebitor(p.getUniqueId()));
    }

}
