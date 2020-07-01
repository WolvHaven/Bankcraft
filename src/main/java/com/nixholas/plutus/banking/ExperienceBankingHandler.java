package com.nixholas.plutus.banking;

import com.nixholas.plutus.PlutusCore;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ExperienceBankingHandler implements BankingHandler<Integer> {

    private final PlutusCore plutusCore;

    public ExperienceBankingHandler(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;
    }

    @Override
    public synchronized boolean transferFromPocketToAccount(Player pocketOwner,
                                                            UUID accountOwner, Integer amount, Player observer) {
        if (amount < 0) return transferFromAccountToPocket(accountOwner, pocketOwner, -amount, observer);

        if (ExperienceBukkitHandler.getTotalExperience(pocketOwner) >= amount) {
            if (plutusCore.getExperienceDatabaseInterface().getBalance(accountOwner) <= Integer.parseInt(plutusCore.getConfigurationHandler().getString("general.maxBankLimitXp")) - amount) {
                ExperienceBukkitHandler.removeExperienceFromPocket(pocketOwner, amount);
                plutusCore.getExperienceDatabaseInterface().addToAccount(accountOwner, amount);
                plutusCore.getConfigurationHandler().printMessage(observer, "message.depositedSuccessfullyXp", amount + "", accountOwner);
                if (plutusCore.getServer().getPlayer(accountOwner) != null)
                    plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(accountOwner));
                return true;
            } else {
                plutusCore.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInAccount", amount + "", accountOwner);
            }
        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInPoket", amount + "", pocketOwner.getUniqueId(), pocketOwner.getName());
        }
        return false;
    }

    @Override
    public synchronized boolean transferFromAccountToPocket(UUID accountOwner,
                                                            Player pocketOwner, Integer amount, Player observer) {
        if (amount < 0) return transferFromPocketToAccount(pocketOwner, accountOwner, -amount, observer);

        if (plutusCore.getExperienceDatabaseInterface().getBalance(accountOwner) + plutusCore.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {
            if (ExperienceBukkitHandler.getTotalExperience(pocketOwner) <= Integer.parseInt(plutusCore.getConfigurationHandler().getString("general.maxPocketLimitXp")) - amount) {
                plutusCore.getExperienceDatabaseInterface().removeFromAccount(accountOwner, amount);
                ExperienceBukkitHandler.addExperienceToPocket(pocketOwner, amount);
                plutusCore.getConfigurationHandler().printMessage(observer, "message.withdrewSuccessfullyXp", amount + "", accountOwner);
                if (plutusCore.getServer().getPlayer(accountOwner) != null)
                    plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(accountOwner));
                return true;
            } else {
                plutusCore.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInPocket", amount + "", pocketOwner.getUniqueId(), pocketOwner.getName());
            }

        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInAccount", amount + "", accountOwner);
        }
        return false;
    }

    @Override
    public synchronized boolean transferFromAccountToAccount(UUID givingPlayer,
                                                             UUID gettingPlayer, Integer amount, Player observer) {
        if (amount < 0) return transferFromAccountToAccount(gettingPlayer, givingPlayer, -amount, observer);

        if (!plutusCore.getExperienceDatabaseInterface().hasAccount(gettingPlayer)) {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", gettingPlayer);
            return false;
        }

        if (plutusCore.getExperienceDatabaseInterface().getBalance(givingPlayer) + plutusCore.getConfigurationHandler().getLoanLimitForPlayer(givingPlayer, this) >= amount) {
            if (plutusCore.getExperienceDatabaseInterface().getBalance(gettingPlayer) <= Integer.parseInt(plutusCore.getConfigurationHandler().getString("general.maxBankLimitXp")) - amount) {
                plutusCore.getExperienceDatabaseInterface().removeFromAccount(givingPlayer, amount);
                plutusCore.getExperienceDatabaseInterface().addToAccount(gettingPlayer, amount);
                plutusCore.getConfigurationHandler().printMessage(observer, "message.transferedSuccessfullyXp", amount + "", gettingPlayer);
                if (plutusCore.getServer().getPlayer(givingPlayer) != null)
                    plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(givingPlayer));
                return true;
            } else {
                plutusCore.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInAccount", amount + "", gettingPlayer);
            }

        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInAccount", amount + "", givingPlayer);
        }
        return false;
    }

    @Override
    public synchronized boolean grantInterests(Player observer) {
        String messageKey;
        for (UUID accountName : plutusCore.getExperienceDatabaseInterface().getAccounts()) {

            double interest = plutusCore.getConfigurationHandler().getInterestForPlayer(accountName, this, plutusCore.getExperienceDatabaseInterface().getBalance(accountName) < 0);


            int amount = (int) (interest * plutusCore.getExperienceDatabaseInterface().getBalance(accountName));

            if (plutusCore.getExperienceDatabaseInterface().getBalance(accountName) <= Integer.parseInt(plutusCore.getConfigurationHandler().getString("general.maxBankLimitXp")) - amount) {
                plutusCore.getExperienceDatabaseInterface().addToAccount(accountName, amount);
                messageKey = "message.grantedInterestOnXp";
            } else {
                messageKey = "message.couldNotGrantInterestOnXp";
            }
            Player player;
            if ((player = plutusCore.getServer().getPlayer(accountName)) != null && (plutusCore.getConfigurationHandler().getString("interest.broadcastXp").equals("true") || PlutusCore.perms.has(player, "Centralbank.interest.broadcastmoney"))) {
                plutusCore.getConfigurationHandler().printMessage(player, messageKey, amount + "", player.getUniqueId(), player.getName());
            }
        }
        return true;
    }

    @Override
    public synchronized boolean depositToAccount(UUID accountOwner, Integer amount,
                                                 Player observer) {
        if (amount < 0) return withdrawFromAccount(accountOwner, -amount, observer);

        if (!plutusCore.getExperienceDatabaseInterface().hasAccount(accountOwner)) {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", accountOwner);
            return false;
        }

        if (plutusCore.getExperienceDatabaseInterface().getBalance(accountOwner) <= Integer.parseInt(plutusCore.getConfigurationHandler().getString("general.maxBankLimitXp")) - amount) {
            plutusCore.getExperienceDatabaseInterface().addToAccount(accountOwner, amount);
            if (plutusCore.getServer().getPlayer(accountOwner) != null)
                plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(accountOwner));
            return true;
        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.reachedMaximumXpInAccount", amount + "", accountOwner);
        }
        return false;
    }

    @Override
    public synchronized boolean withdrawFromAccount(UUID accountOwner, Integer amount,
                                                    Player observer) {
        if (amount < 0) return depositToAccount(accountOwner, -amount, observer);

        if (!plutusCore.getExperienceDatabaseInterface().hasAccount(accountOwner)) {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", accountOwner);
            return false;
        }

        if (plutusCore.getExperienceDatabaseInterface().getBalance(accountOwner) + plutusCore.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {

            plutusCore.getExperienceDatabaseInterface().removeFromAccount(accountOwner, amount);
            if (plutusCore.getServer().getPlayer(accountOwner) != null)
                plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(accountOwner));
            return true;
        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.notEnoughXpInAccount", amount + "", accountOwner);
        }

        return false;
    }


}
