package com.nixholas.centralbank.database.flatfile;

import com.nixholas.centralbank.CentralBank;
import com.nixholas.centralbank.database.AccountDatabaseInterface;

import java.io.*;
import java.util.UUID;

public class ExperienceFlatFileInterface implements
        AccountDatabaseInterface<Integer> {


    private final CentralBank centralBank;

    public ExperienceFlatFileInterface(CentralBank centralBank) {
        this.centralBank = centralBank;
        (new File("plugins" + System.getProperty("file.separator") + "Centralbank" + System.getProperty("file.separator") + "Accounts")).mkdir();
    }

    @Override
    public boolean hasAccount(UUID player) {
        return (new File("plugins" + System.getProperty("file.separator") + "Centralbank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player + ".data")).exists();
    }

    @Override
    public boolean createAccount(UUID player) {
        try {
            File accountFile = new File("plugins" + System.getProperty("file.separator") + "Centralbank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player + ".data");
            accountFile.createNewFile();

            FileWriter fw = new FileWriter(accountFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("0:0");
            bw.close();
            fw.close();
            return true;

        } catch (Exception e) {
            centralBank.getLogger().severe("Could not create Account " + player + "!");
        }
        return false;
    }

    @Override
    public Integer getBalance(UUID player) {

        if (!hasAccount(player)) {
            createAccount(player);
        }

        try {
            File accountFile = new File("plugins" + System.getProperty("file.separator") + "Centralbank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player + ".data");

            FileReader fr = new FileReader(accountFile);
            BufferedReader br = new BufferedReader(fr);
            Integer balance = Integer.parseInt(br.readLine().split(":")[0]);
            br.close();
            fr.close();
            return balance;

        } catch (Exception e) {
            centralBank.getLogger().severe("Could not get Balance of " + player + "!");
        }
        return null;
    }

    @Override
    public boolean setBalance(UUID player, Integer amount) {
        if (!hasAccount(player)) {
            createAccount(player);
        }

        try {
            File accountFile = new File("plugins" + System.getProperty("file.separator") + "Centralbank" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player + ".data");

            FileReader fr = new FileReader(accountFile);
            BufferedReader br = new BufferedReader(fr);
            String balances = br.readLine();
            br.close();
            fr.close();


            FileWriter fw = new FileWriter(accountFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(amount + ":" + balances.split(":")[1]);
            bw.close();
            fw.close();


            return true;

        } catch (Exception e) {
            centralBank.getLogger().severe("Could not set Balance of " + player + "!");
        }
        return false;
    }

    @Override
    public boolean addToAccount(UUID player, Integer amount) {

        if (amount < 0) {
            return removeFromAccount(player, -amount);
        }

        Integer currentBalance = getBalance(player);
        if (currentBalance <= Integer.MAX_VALUE - amount) {
            setBalance(player, currentBalance + amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeFromAccount(UUID player, Integer amount) {

        if (amount < 0) {
            return addToAccount(player, -amount);
        }

        Integer currentBalance = getBalance(player);
        if (currentBalance >= Integer.MIN_VALUE + amount) {
            setBalance(player, currentBalance - amount);
            return true;
        }
        return false;
    }

    @Override
    public UUID[] getAccounts() {
        String[] fileNames = ((new File("plugins" + System.getProperty("file.separator") + "Centralbank" + System.getProperty("file.separator") + "Accounts")).list());

        for (int i = 0; i < fileNames.length; i++) {
            fileNames[i] = fileNames[i].split("\\.")[0];
        }
        UUID[] uuids = new UUID[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            uuids[i] = UUID.fromString(fileNames[i]);
        }
        return uuids;
    }

}
