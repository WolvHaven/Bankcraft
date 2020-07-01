package com.nixholas.plutus.database.flatfile;

import com.nixholas.plutus.PlutusCore;
import com.nixholas.plutus.database.AccountDatabaseInterface;

import java.io.*;
import java.util.UUID;

public class MoneyFlatFileInterface implements AccountDatabaseInterface<Double> {

    private final PlutusCore plutusCore;

    public MoneyFlatFileInterface(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;
    }

    @Override
    public boolean hasAccount(UUID player) {
        return (new File("plugins" + System.getProperty("file.separator") + "Plutus" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player + ".data")).exists();
    }

    @Override
    public boolean createAccount(UUID player) {
        try {
            File accountFile = new File("plugins" + System.getProperty("file.separator") + "Plutus" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player + ".data");
            accountFile.createNewFile();

            FileWriter fw = new FileWriter(accountFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("0:0");
            bw.close();
            fw.close();
            return true;

        } catch (Exception e) {
            plutusCore.getLogger().severe("Could not create Account " + player + "!");
        }
        return false;
    }

    @Override
    public Double getBalance(UUID player) {
        if (!hasAccount(player)) {
            createAccount(player);
        }

        try {
            File accountFile = new File("plugins" + System.getProperty("file.separator") + "Plutus" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player + ".data");

            FileReader fr = new FileReader(accountFile);
            BufferedReader br = new BufferedReader(fr);
            Double balance = Double.parseDouble(br.readLine().split(":")[1]);
            br.close();
            fr.close();
            return balance;

        } catch (Exception e) {
            plutusCore.getLogger().severe("Could not get Balance of " + player + "!");
        }
        return null;
    }

    @Override
    public boolean setBalance(UUID player, Double amount) {
        if (!hasAccount(player)) {
            createAccount(player);
        }

        try {
            File accountFile = new File("plugins" + System.getProperty("file.separator") + "Plutus" + System.getProperty("file.separator") + "Accounts" + System.getProperty("file.separator") + player + ".data");

            FileReader fr = new FileReader(accountFile);
            BufferedReader br = new BufferedReader(fr);
            String balances = br.readLine();
            br.close();
            fr.close();


            FileWriter fw = new FileWriter(accountFile, false);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(balances.split(":")[0] + ":" + amount);
            bw.close();
            fw.close();


            return true;

        } catch (Exception e) {
            plutusCore.getLogger().severe("Could not set Balance of " + player + "!");
        }
        return false;
    }

    @Override
    public boolean addToAccount(UUID player, Double amount) {
        if (amount < 0) {
            return removeFromAccount(player, -amount);
        }

        Double currentBalance = getBalance(player);
        if (currentBalance <= Double.MAX_VALUE - amount) {
            setBalance(player, currentBalance + amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeFromAccount(UUID player, Double amount) {

        if (amount < 0) {
            return addToAccount(player, -amount);
        }

        Double currentBalance = getBalance(player);
        if (currentBalance - amount >= -Double.MAX_VALUE) {
            setBalance(player, currentBalance - amount);
            return true;
        }
        return false;
    }

    @Override
    public UUID[] getAccounts() {
        String[] fileNames = (new File("plugins" + System.getProperty("file.separator") + "Plutus" + System.getProperty("file.separator") + "Accounts")).list();
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