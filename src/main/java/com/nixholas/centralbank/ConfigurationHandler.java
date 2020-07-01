package com.nixholas.centralbank;


import com.nixholas.centralbank.banking.BankingHandler;
import com.nixholas.centralbank.banking.ExperienceBankingHandler;
import com.nixholas.centralbank.banking.MoneyBankingHandler;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;

public class ConfigurationHandler {

    private final CentralBank centralBank;

    public ConfigurationHandler(CentralBank centralBank) {
        this.centralBank = centralBank;
        if (!(new File("plugins" + System.getProperty("file.separator") + "Centralbank" + System.getProperty("file.separator") + "config.yml").exists())) {
            CentralBank.log.info("No config file found! Creating new one...");
            centralBank.saveDefaultConfig();
        }
        try {
            centralBank.getConfig().load(new File("plugins" + System.getProperty("file.separator") + "Centralbank" + System.getProperty("file.separator") + "config.yml"));
        } catch (Exception e) {
            CentralBank.log.info("Could not load config file!");
            e.printStackTrace();
        }
    }

    public void printMessage(Player player, String messageKey, String amount, UUID player2) {
        printMessage(player, messageKey, amount, player2, centralBank.getUUIDHandler().getName(player2));
    }

    public void printMessage(Player player, String messageKey, String amount, UUID player2, String player2Name) {
        if (centralBank.getConfig().contains(messageKey)) {
            List<String> message = new ArrayList<String>();
            message.add(centralBank.getConfig().getString(messageKey));

            if (player2 != null && !player2.equals("")) {
                message.set(0, message.get(0).replaceAll("%player2", player2Name));
            }

            DecimalFormat f = new DecimalFormat("#0.00");

            if (amount != null && !amount.equals("")) {
                message.set(0, message.get(0).replaceAll("%amount", f.format(Double.parseDouble(amount))));
            }

            message.set(0, message.get(0).replaceAll("%pocketXp", "" + player.getTotalExperience()));
            message.set(0, message.get(0).replaceAll("%pocket", "" + CentralBank.econ.getBalance(player)));

            if (centralBank.getExperienceDatabaseInterface().hasAccount(player2))
                message.set(0, message.get(0).replaceAll("%balanceXp", "" + centralBank.getExperienceDatabaseInterface().getBalance(player2)));
            else
                message.set(0, message.get(0).replaceAll("%balanceXp", "0"));

            if (centralBank.getMoneyDatabaseInterface().hasAccount(player2))
                message.set(0, message.get(0).replaceAll("%balance", "" + f.format(centralBank.getMoneyDatabaseInterface().getBalance(player2))));
            else
                message.set(0, message.get(0).replaceAll("%balance", "0.00"));

            message.set(0, message.get(0).replaceAll("%player", player.getName()));

            message.set(0, message.get(0).replaceAll("%interestTimeRemaining", centralBank.getInterestGrantingTask().getRemainingTime() + ""));
            message.set(0, message.get(0).replaceAll("%interestTimeTotal", centralBank.getInterestGrantingTask().getTotalTime() + ""));


            if (message.get(0).contains("%rankTableMoney")) {
                message.set(0, message.get(0).replaceAll("%rankTableMoney", ""));
                for (String line : getRichestPlayers()) {
                    message.add(line);
                }
            }
            if (message.get(0).contains("%rankTableExperience")) {
                message.set(0, message.get(0).replaceAll("%rankTableExperience", ""));
                for (String line : getExperiencedPlayers()) {
                    message.add(line);
                }
            }

            if (player != null) {
                player.sendMessage(getString("chat.color")
                        + getString("chat.prefix") + message.get(0));
                for (int i = 1; i < message.size(); i++) {
                    player.sendMessage(getString("chat.color") + message.get(i));
                }
            }

        } else {
            centralBank.getLogger().severe("Could not locate '" + messageKey + "' in the config.yml inside of the Centralbank folder!");
            player.sendMessage("Could not locate '" + messageKey + "' in the config.yml inside of the Centralbank folder!");
        }
    }


    public double getInterestForPlayer(UUID accountName,
                                       BankingHandler<?> bankingHandler, boolean inDebt) {
        //Default interest
        String interestString = "interest.interestOn";

        //Type specific interest
        if (bankingHandler instanceof MoneyBankingHandler) {
            interestString += "Money";
        } else if (bankingHandler instanceof ExperienceBankingHandler) {
            interestString += "Xp";
        }

        //Depts
        if (inDebt) {
            interestString += "debts";
        }

        //Online/Offline interests
        if (centralBank.getServer().getPlayer(accountName) != null)
            interestString += "IfOnline";
        else
            interestString += "IfOffline";

        //Player specific interest
        //TODO


        return Double.parseDouble(getString(interestString));
    }

    public String getString(String key) {
        if (!centralBank.getConfig().contains(key)) {
            centralBank.getLogger().severe("Could not locate '" + key + "' in the config.yml inside of the Centralbank folder! (Try generating a new one by deleting the current)");
            return "errorCouldNotLocateInConfigYml:" + key;
        } else {
            if (key.toLowerCase().contains("color")) {
                return "�" + centralBank.getConfig().getString(key);
            }
            return centralBank.getConfig().getString(key);
        }
    }

    private List<String> getRichestPlayers() {
        DecimalFormat f = new DecimalFormat("#0.00");
        List<String> result = new ArrayList<String>();
        HashMap<UUID, Double> accounts = new HashMap<UUID, Double>();

        for (UUID uuid : centralBank.getMoneyDatabaseInterface().getAccounts()) {
            accounts.put(uuid, centralBank.getMoneyDatabaseInterface().getBalance(uuid));
        }

        @SuppressWarnings("unchecked")
        List<Map.Entry<UUID, Double>> sortedAccounts = sortByComparator(accounts);

        for (int i = Math.min(Integer.parseInt(getString("chat.rankTableLength")), sortedAccounts.size()) - 1; i >= 0; i--) {
            result.add(centralBank.getUUIDHandler().getName(sortedAccounts.get(i).getKey()) + " " + f.format(sortedAccounts.get(i).getValue()));
        }


        return result;
    }


    private List<String> getExperiencedPlayers() {

        List<String> result = new ArrayList<String>();
        HashMap<UUID, Integer> accounts = new HashMap<UUID, Integer>();

        for (UUID uuid : centralBank.getExperienceDatabaseInterface().getAccounts()) {
            accounts.put(uuid, centralBank.getExperienceDatabaseInterface().getBalance(uuid));
        }

        @SuppressWarnings("unchecked")
        List<Map.Entry<UUID, Integer>> sortedAccounts = sortByComparator(accounts);

        for (int i = Math.min(Integer.parseInt(getString("chat.rankTableLength")), sortedAccounts.size()) - 1; i >= 0; i--) {
            result.add(centralBank.getUUIDHandler().getName(sortedAccounts.get(i).getKey()) + " " + sortedAccounts.get(i).getValue());
        }


        return result;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List sortByComparator(Map unsortMap) {

        List list = new LinkedList(unsortMap.entrySet());

        // sort list based on comparator
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        return list;
    }


    public double getLoanLimitForPlayer(UUID accountOwner, BankingHandler<?> bankingHandler) {
        Double loanLimit = 0D;

        //Default loans
        String interestString = "general.maxLoanLimit";

        //Type specific loans
        if (bankingHandler instanceof MoneyBankingHandler) {
            interestString += "Money";
        } else if (bankingHandler instanceof ExperienceBankingHandler) {
            interestString += "Xp";
        }

        loanLimit = Double.parseDouble(getString(interestString));

        //Player specific loans
        Player player = centralBank.getServer().getPlayer(accountOwner);
        if (player != null) { //If Online

            //TODO


        }


        return loanLimit;
    }

}
