package com.nixholas.plutus.banking;

import com.nixholas.plutus.PlutusCore;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoneyBankingHandler implements BankingHandler<Double> {

    private final PlutusCore plutusCore;

    public MoneyBankingHandler(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;
    }

    @Override
    public synchronized boolean transferFromPocketToAccount(Player pocketOwner,
                                                            UUID accountOwner, Double amount, Player observer) {
        if (amount < 0) return transferFromAccountToPocket(accountOwner, pocketOwner, -amount, observer);

        if (PlutusCore.econ.getBalance(pocketOwner) >= amount && plutusCore.getMoneyDatabaseInterface().getBalance(accountOwner) <= Double.MAX_VALUE - amount) {
            if (plutusCore.getMoneyDatabaseInterface().getBalance(accountOwner) <= Double.parseDouble(plutusCore.getConfigurationHandler().getString("general.maxBankLimitMoney")) - amount) {
                PlutusCore.econ.withdrawPlayer(pocketOwner, amount);
                plutusCore.getMoneyDatabaseInterface().addToAccount(accountOwner, amount);
                plutusCore.getConfigurationHandler().printMessage(observer, "message.depositedSuccessfully", amount + "", accountOwner);
                if (plutusCore.getServer().getPlayer(accountOwner) != null)
                    plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(accountOwner));
                return true;
            } else {
                plutusCore.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount + "", accountOwner);
            }
        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInPoket", amount + "", pocketOwner.getUniqueId(), pocketOwner.getName());
        }
        return false;
    }

    @Override
    public synchronized boolean transferFromAccountToPocket(UUID accountOwner,
                                                            Player pocketOwner, Double amount, Player observer) {
        if (amount < 0) return transferFromPocketToAccount(pocketOwner, accountOwner, -amount, observer);

        if (plutusCore.getMoneyDatabaseInterface().getBalance(accountOwner) + plutusCore.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {
            if (PlutusCore.econ.getBalance(pocketOwner) <= Double.parseDouble(plutusCore.getConfigurationHandler().getString("general.maxPocketLimitMoney")) - amount) {
                plutusCore.getMoneyDatabaseInterface().removeFromAccount(accountOwner, amount);
                PlutusCore.econ.depositPlayer(pocketOwner, amount);
                plutusCore.getConfigurationHandler().printMessage(observer, "message.withdrewSuccessfully", amount + "", accountOwner);
                if (plutusCore.getServer().getPlayer(accountOwner) != null)
                    plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(accountOwner));
                return true;
            } else {
                plutusCore.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInPocket", amount + "", pocketOwner.getUniqueId(), pocketOwner.getName());
            }
        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount + "", accountOwner);
        }
        return false;
    }

    @Override
    public synchronized boolean transferFromAccountToAccount(UUID givingPlayer,
                                                             UUID gettingPlayer, Double amount, Player observer) {
        if (amount < 0) return transferFromAccountToAccount(gettingPlayer, givingPlayer, -amount, observer);

        if (!plutusCore.getMoneyDatabaseInterface().hasAccount(gettingPlayer)) {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", gettingPlayer);
            return false;
        }
        if (plutusCore.getMoneyDatabaseInterface().getBalance(givingPlayer) + plutusCore.getConfigurationHandler().getLoanLimitForPlayer(givingPlayer, this) >= amount) {
            if (plutusCore.getMoneyDatabaseInterface().getBalance(gettingPlayer) <= Double.parseDouble(plutusCore.getConfigurationHandler().getString("general.maxBankLimitMoney")) - amount) {
                plutusCore.getMoneyDatabaseInterface().removeFromAccount(givingPlayer, amount);
                plutusCore.getMoneyDatabaseInterface().addToAccount(gettingPlayer, amount);
                plutusCore.getConfigurationHandler().printMessage(observer, "message.transferedSuccessfully", amount + "", gettingPlayer);
                if (plutusCore.getServer().getPlayer(givingPlayer) != null)
                    plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(givingPlayer));
                return true;
            } else {
                plutusCore.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount + "", gettingPlayer);
            }

        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount + "", givingPlayer);
        }
        return false;
    }

    @Override
    public synchronized boolean grantInterests(Player observer) {
        String messageKey;
        for (UUID accountName : plutusCore.getMoneyDatabaseInterface().getAccounts()) {

            double interest = plutusCore.getConfigurationHandler().getInterestForPlayer(accountName, this, plutusCore.getMoneyDatabaseInterface().getBalance(accountName) < 0);
            double amount = interest * plutusCore.getMoneyDatabaseInterface().getBalance(accountName);

            if (plutusCore.getMoneyDatabaseInterface().getBalance(accountName) <= Double.parseDouble(plutusCore.getConfigurationHandler().getString("general.maxBankLimitMoney")) - amount) {
                plutusCore.getMoneyDatabaseInterface().addToAccount(accountName, amount);
                messageKey = "message.grantedInterestOnMoney";
            } else {
                messageKey = "message.couldNotGrantInterestOnMoney";
            }
            Player player;
            if ((player = plutusCore.getServer().getPlayer(accountName)) != null && (plutusCore.getConfigurationHandler().getString("interest.broadcastMoney").equals("true") || PlutusCore.perms.has(player, "Centralbank.interest.broadcastmoney"))) {
                plutusCore.getConfigurationHandler().printMessage(player, messageKey, amount + "", player.getUniqueId(), player.getName());
            }
        }
        return true;
    }

    @Override
    public synchronized boolean depositToAccount(UUID accountOwner, Double amount,
                                                 Player observer) {
        if (amount < 0) return withdrawFromAccount(accountOwner, -amount, observer);

        if (!plutusCore.getMoneyDatabaseInterface().hasAccount(accountOwner)) {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", accountOwner);
            return false;
        }

        if (plutusCore.getMoneyDatabaseInterface().getBalance(accountOwner) <= Double.parseDouble(plutusCore.getConfigurationHandler().getString("general.maxBankLimitMoney")) - amount) {
            plutusCore.getMoneyDatabaseInterface().addToAccount(accountOwner, amount);
            if (plutusCore.getServer().getPlayer(accountOwner) != null)
                plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(accountOwner));
            return true;
        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.reachedMaximumMoneyInAccount", amount + "", accountOwner);
        }
        return false;
    }

    @Override
    public synchronized boolean withdrawFromAccount(UUID accountOwner, Double amount,
                                                    Player observer) {
        if (amount < 0) return depositToAccount(accountOwner, -amount, observer);

        if (!plutusCore.getMoneyDatabaseInterface().hasAccount(accountOwner)) {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.accountDoesNotExist", amount + "", accountOwner);
            return false;
        }

        if (plutusCore.getMoneyDatabaseInterface().getBalance(accountOwner) + plutusCore.getConfigurationHandler().getLoanLimitForPlayer(accountOwner, this) >= amount) {

            plutusCore.getMoneyDatabaseInterface().removeFromAccount(accountOwner, amount);
            if (plutusCore.getServer().getPlayer(accountOwner) != null)
                plutusCore.getDebitorHandler().updateDebitorStatus(plutusCore.getServer().getPlayer(accountOwner));
            return true;
        } else {
            plutusCore.getConfigurationHandler().printMessage(observer, "message.notEnoughMoneyInAccount", amount + "", accountOwner);
        }

        return false;
    }


}
