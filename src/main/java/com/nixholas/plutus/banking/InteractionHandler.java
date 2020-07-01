package com.nixholas.plutus.banking;

import com.nixholas.plutus.PlutusCore;
import com.nixholas.plutus.Util;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class InteractionHandler {

    private final PlutusCore plutusCore;

    //0 = not listening, 1 = waiting for method, 2 = Amount
    private final HashMap<Player, Integer> chatSignMap = new HashMap<Player, Integer>();

    //Matches interactions like deposit or withdraw to their typeId
    private final HashMap<String, Integer> typeMap = new HashMap<String, Integer>();

    //-1 = no account related, 1 = pocket money, 2 = account money, 3= pocket xp, 4= account xp
    private final HashMap<Integer, Integer> currencyMap = new HashMap<Integer, Integer>();

    private final HashMap<Player, Long> lastInteractMap = new HashMap<Player, Long>();

    public InteractionHandler(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;


        //Fill currencyMap
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.balance").toLowerCase(), 0);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.deposit").toLowerCase(), 1);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.withdraw").toLowerCase(), 2);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.balancexp").toLowerCase(), 5);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.depositxp").toLowerCase(), 6);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.withdrawxp").toLowerCase(), 7);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.exchange").toLowerCase(), 12);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.exchangexp").toLowerCase(), 13);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.interesttimer").toLowerCase(), 16);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.chatinteract").toLowerCase(), 17);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.rankstats").toLowerCase(), 18);
        typeMap.put(plutusCore.getConfigurationHandler().getString("signAndCommand.rankstatsxp").toLowerCase(), 19);

        //Fill typeMap
        currencyMap.put(0, -1);
        currencyMap.put(1, 1);
        currencyMap.put(2, 2);
        currencyMap.put(3, 1);
        currencyMap.put(4, 2);
        currencyMap.put(5, -1);
        currencyMap.put(6, 3);
        currencyMap.put(7, 4);
        currencyMap.put(8, 3);
        currencyMap.put(9, 4);
        currencyMap.put(10, -1);
        currencyMap.put(11, -1);
        currencyMap.put(12, 2);
        currencyMap.put(13, 4);
        currencyMap.put(14, 2);
        currencyMap.put(15, 4);
        currencyMap.put(16, -1);
        currencyMap.put(17, -1);
        currencyMap.put(18, -1);
        currencyMap.put(19, -1);
    }


    public boolean interact(int type, String amountAsString, Player interactingPlayer, UUID targetPlayer) {

        //Check for interaction interval
        if (lastInteractMap.containsKey(interactingPlayer) && System.currentTimeMillis() - lastInteractMap.get(interactingPlayer) <= Integer.parseInt(plutusCore.getConfigurationHandler().getString("general.timeBetweenTwoInteractions"))) {
            plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.tooFastInteraction", "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
            return false;
        }
        lastInteractMap.put(interactingPlayer, System.currentTimeMillis());


        if (amountAsString == null || amountAsString.equalsIgnoreCase("") || (!amountAsString.equalsIgnoreCase("all") && !Util.isDouble(amountAsString))) {
            return interact(type, -1, interactingPlayer, targetPlayer);
        }

        if (amountAsString.equalsIgnoreCase("all")) {
            return interact(type, getMaxAmountForAction(currencyMap.get(type), interactingPlayer), interactingPlayer, targetPlayer);
        }


        return interact(type, Double.parseDouble(amountAsString), interactingPlayer, targetPlayer);
    }


    //Returns current balance of the related account
    private double getMaxAmountForAction(int currencyType, Player pocketOwner) {


        if (currencyType == 1) {
            return PlutusCore.econ.getBalance(pocketOwner);
        } else if (currencyType == 2) {
            return plutusCore.getMoneyDatabaseInterface().getBalance(pocketOwner.getUniqueId());
        } else if (currencyType == 3) {
            return ExperienceBukkitHandler.getTotalExperience(pocketOwner);
        } else if (currencyType == 4) {
            return plutusCore.getExperienceDatabaseInterface().getBalance(pocketOwner.getUniqueId());
        }
        return -1;
    }


    //Main method
    private boolean interact(int type, double amount, Player interactingPlayer, UUID targetPlayer) {

//		System.out.println(type+" "+amount+" "+interactingPlayer.getName()+" "+targetPlayer);


        //BALANCE signs
        if (type == 0 || type == 10) {
            if (targetPlayer != null)
                plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.balance", "", targetPlayer);
            else
                plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.balance", "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
            return true;
        }
        if (type == 5 || type == 11) {
            if (targetPlayer != null)
                plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.balancexp", "", targetPlayer);
            else
                plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.balancexp", "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
            return true;
        }

        if (type == 1 | type == 3) {
            //Deposit Money
            return ((MoneyBankingHandler) plutusCore.getBankingHandlers()[0]).transferFromPocketToAccount(interactingPlayer, interactingPlayer.getUniqueId(), amount, interactingPlayer);
        }
        if (type == 6 | type == 8) {
            //Deposit XP
            return ((ExperienceBankingHandler) plutusCore.getBankingHandlers()[1]).transferFromPocketToAccount(interactingPlayer, interactingPlayer.getUniqueId(), (int) amount, interactingPlayer);
        }

        if (type == 2 | type == 4) {
            //Withdraw Money
            return ((MoneyBankingHandler) plutusCore.getBankingHandlers()[0]).transferFromAccountToPocket(interactingPlayer.getUniqueId(), interactingPlayer, amount, interactingPlayer);
        }
        if (type == 7 | type == 9) {
            //Withdraw XP
            return ((ExperienceBankingHandler) plutusCore.getBankingHandlers()[1]).transferFromAccountToPocket(interactingPlayer.getUniqueId(), interactingPlayer, (int) amount, interactingPlayer);
        }
        if (type == 12 | type == 14) {
            //exchange Money
            if (((MoneyBankingHandler) plutusCore.getBankingHandlers()[0]).withdrawFromAccount(interactingPlayer.getUniqueId(), (double) (int) amount, interactingPlayer)) {
                if (((ExperienceBankingHandler) plutusCore.getBankingHandlers()[1]).depositToAccount(interactingPlayer.getUniqueId(), (int) ((int) amount * Double.parseDouble(plutusCore.getConfigurationHandler().getString("general.exchangerateFromMoneyToXp"))), interactingPlayer)) {
                    plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.exchangedMoneySuccessfully", amount + "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
                    return true;
                } else {
                    ((MoneyBankingHandler) plutusCore.getBankingHandlers()[0]).depositToAccount(interactingPlayer.getUniqueId(), (double) (int) amount, interactingPlayer);
                    return false;
                }

            }
        }
        if (type == 13 | type == 15) {
            //exchange xp
            if (((ExperienceBankingHandler) plutusCore.getBankingHandlers()[1]).withdrawFromAccount(interactingPlayer.getUniqueId(), (int) amount, interactingPlayer)) {
                if (((MoneyBankingHandler) plutusCore.getBankingHandlers()[0]).depositToAccount(interactingPlayer.getUniqueId(), (((int) amount) * Double.parseDouble(plutusCore.getConfigurationHandler().getString("general.exchangerateFromXpToMoney"))), interactingPlayer)) {
                    plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.exchangedXpSuccessfully", amount + "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
                    return true;
                } else {
                    ((ExperienceBankingHandler) plutusCore.getBankingHandlers()[1]).depositToAccount(interactingPlayer.getUniqueId(), (int) amount, interactingPlayer);
                    return false;
                }
            }
        }

        if (type == 16) {
            //interestCounter
            plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.interestTimer", amount + "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
            return true;
        }

        if (type == 17) {
            //Starts interaction with chatSigns (everything else is handled in the MinecraftChatListener)
            chatSignMap.put(interactingPlayer, 1);
            plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.specifyAnInteraction", "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
            return true;
        }

        if (type == 18) {
            //rankStatsMoney
            plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.rankStatsMoney", "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
            return true;
        }

        if (type == 19) {
            //rankStatsExperience
            plutusCore.getConfigurationHandler().printMessage(interactingPlayer, "message.rankStatsExperience", "", interactingPlayer.getUniqueId(), interactingPlayer.getName());
            return true;
        }

        return false;
    }

    public void stopChatInteract(Player p) {
        if (!chatSignMap.containsKey(p) || chatSignMap.get(p).equals(0)) return;
        chatSignMap.put(p, 0);
        plutusCore.getConfigurationHandler().printMessage(p, "message.youHaveQuit", "", p.getUniqueId(), p.getName());
    }

    public boolean interact(String type, String amountAsString, Player pocketOwner, UUID accountOwner) {
        return interact(typeMap.get(type.toLowerCase()), amountAsString, pocketOwner, accountOwner);
    }

    public HashMap<Player, Integer> getChatSignMap() {
        return chatSignMap;
    }

    public HashMap<String, Integer> getTypeMap() {
        return typeMap;
    }
}
