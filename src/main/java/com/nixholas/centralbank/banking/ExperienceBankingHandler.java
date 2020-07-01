package com.nixholas.centralbank.banking;

import com.nixholas.centralbank.CentralBank;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ExperienceBankingHandler implements BankingHandler<Integer> {

    private final CentralBank centralBank;

    public ExperienceBankingHandler(CentralBank centralBank) {
        this.centralBank = centralBank;
    }

    @Override
    public synchronized boolean transferFromPocketToAccount(Player pocketOwner,
                                                            UUID accountOwner, Integer amount, Player observer) {
        if (amount < 0) return transferFromAccountToPocket(accountOwner, pocketOwner, -amount, observer);

        if (ExperienceBukkitHandler.getTotalExperience(pocketOwner) >= amount) {
            if (centralBank.getExperienceDatabaseInterface().getBalance(accountOwner) <= Integer.parseInt(centralBank.getConfigurationHandler().getString("general.maxBankLimitXp")) - amount) {
                ExperienceBukkitHandler.removeExperienceFromPocket(pocketOwner, amount);
                centralBank.getExperienceDatabaseInterface().addToAccount(accountOwner, amount);
                centralBank.getConfigurationHandler().printMessage(observer, "message.depositedSuccessfullyXp", amount + "", accountOwner);
                if (centralBank.getServer().getPlayer(accountOwner) != null)
                    centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(accountOwner));
                return true;
            } else {
                centralBank.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInAccount", amount + "", accountOwner);
            }
        } else {
            centralBank.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInPoket", amount + "", pocketOwner.getUniqueId(), pocketOwner.getName());
        }
        return false;
    }

    @Override
    public synchronized boolean transferFromAccountToPocket(UUID accountOwner,
                                                            Player pocketOwner, Integer amount, Player observer) {
        if (amount < 0) return transferFromPocketToAccount(pocketOwner, accountOwner, -amount, observer);

        if (centralBank.getExperienceDatabaseInterface().getBalance(accountOwner) + centralBank.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {
            if (ExperienceBukkitHandler.getTotalExperience(pocketOwner) <= Integer.parseInt(centralBank.getConfigurationHandler().getString("general.maxPocketLimitXp")) - amount) {
                centralBank.getExperienceDatabaseInterface().removeFromAccount(accountOwner, amount);
                ExperienceBukkitHandler.addExperienceToPocket(pocketOwner, amount);
                centralBank.getConfigurationHandler().printMessage(observer, "message.withdrewSuccessfullyXp", amount + "", accountOwner);
                if (centralBank.getServer().getPlayer(accountOwner) != null)
                    centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(accountOwner));
                return true;
            } else {
                centralBank.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInPocket", amount + "", pocketOwner.getUniqueId(), pocketOwner.getName());
            }

        } else {
            centralBank.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInAccount", amount + "", accountOwner);
        }
        return false;
    }

    @Override
    public synchronized boolean transferFromAccountToAccount(UUID givingPlayer,
                                                             UUID gettingPlayer, Integer amount, Player observer) {
        if (amount < 0) return transferFromAccountToAccount(gettingPlayer, givingPlayer, -amount, observer);

        if (!centralBank.getExperienceDatabaseInterface().hasAccount(gettingPlayer)) {
            centralBank.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", gettingPlayer);
            return false;
        }

        if (centralBank.getExperienceDatabaseInterface().getBalance(givingPlayer) + centralBank.getConfigurationHandler().getLoanLimitForPlayer(givingPlayer, this) >= amount) {
            if (centralBank.getExperienceDatabaseInterface().getBalance(gettingPlayer) <= Integer.parseInt(centralBank.getConfigurationHandler().getString("general.maxBankLimitXp")) - amount) {
                centralBank.getExperienceDatabaseInterface().removeFromAccount(givingPlayer, amount);
                centralBank.getExperienceDatabaseInterface().addToAccount(gettingPlayer, amount);
                centralBank.getConfigurationHandler().printMessage(observer, "message.transferedSuccessfullyXp", amount + "", gettingPlayer);
                if (centralBank.getServer().getPlayer(givingPlayer) != null)
                    centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(givingPlayer));
                return true;
            } else {
                centralBank.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInAccount", amount + "", gettingPlayer);
            }

        } else {
            centralBank.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInAccount", amount + "", givingPlayer);
        }
        return false;
    }

    @Override
    public synchronized boolean grantInterests(Player observer) {
        String messageKey;
        for (UUID accountName : centralBank.getExperienceDatabaseInterface().getAccounts()) {

            double interest = centralBank.getConfigurationHandler().getInterestForPlayer(accountName, this, centralBank.getExperienceDatabaseInterface().getBalance(accountName) < 0);


            int amount = (int) (interest * centralBank.getExperienceDatabaseInterface().getBalance(accountName));

            if (centralBank.getExperienceDatabaseInterface().getBalance(accountName) <= Integer.parseInt(centralBank.getConfigurationHandler().getString("general.maxBankLimitXp")) - amount) {
                centralBank.getExperienceDatabaseInterface().addToAccount(accountName, amount);
                messageKey = "message.grantedInterestOnXp";
            } else {
                messageKey = "message.couldNotGrantInterestOnXp";
            }
            Player player;
            if ((player = centralBank.getServer().getPlayer(accountName)) != null && (centralBank.getConfigurationHandler().getString("interest.broadcastXp").equals("true") || CentralBank.perms.has(player, "bankcraft.interest.broadcastmoney"))) {
                centralBank.getConfigurationHandler().printMessage(player, messageKey, amount + "", player.getUniqueId(), player.getName());
            }
        }
        return true;
    }

    @Override
    public synchronized boolean depositToAccount(UUID accountOwner, Integer amount,
                                                 Player observer) {
        if (amount < 0) return withdrawFromAccount(accountOwner, -amount, observer);

        if (!centralBank.getExperienceDatabaseInterface().hasAccount(accountOwner)) {
            centralBank.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", accountOwner);
            return false;
        }

        if (centralBank.getExperienceDatabaseInterface().getBalance(accountOwner) <= Integer.parseInt(centralBank.getConfigurationHandler().getString("general.maxBankLimitXp")) - amount) {
            centralBank.getExperienceDatabaseInterface().addToAccount(accountOwner, amount);
            if (centralBank.getServer().getPlayer(accountOwner) != null)
                centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(accountOwner));
            return true;
        } else {
            centralBank.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInAccount", amount + "", accountOwner);
        }
        return false;
    }

    @Override
    public synchronized boolean withdrawFromAccount(UUID accountOwner, Integer amount,
                                                    Player observer) {
        if (amount < 0) return depositToAccount(accountOwner, -amount, observer);

        if (!centralBank.getExperienceDatabaseInterface().hasAccount(accountOwner)) {
            centralBank.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", accountOwner);
            return false;
        }

        if (centralBank.getExperienceDatabaseInterface().getBalance(accountOwner) + centralBank.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {

            centralBank.getExperienceDatabaseInterface().removeFromAccount(accountOwner, amount);
            if (centralBank.getServer().getPlayer(accountOwner) != null)
                centralBank.getDebitorHandler().updateDebitorStatus(centralBank.getServer().getPlayer(accountOwner));
            return true;
        } else {
            centralBank.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInAccount", amount + "", accountOwner);
        }

        return false;
    }


}
