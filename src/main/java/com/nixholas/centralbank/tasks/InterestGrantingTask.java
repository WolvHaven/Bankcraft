package com.nixholas.centralbank.tasks;

import com.nixholas.centralbank.CentralBank;
import com.nixholas.centralbank.banking.BankingHandler;
import org.bukkit.block.Sign;

import java.util.TimerTask;

public class InterestGrantingTask extends TimerTask {

    private final CentralBank centralBank;
    private final int amountOfTimeUntilInterestInMinutes;
    private int currentTimeUntilInterestInMinutes;

    public InterestGrantingTask(CentralBank centralBank, int amountOfTimeUntilInterestInMinutes) {
        this.centralBank = centralBank;
        this.amountOfTimeUntilInterestInMinutes = amountOfTimeUntilInterestInMinutes;
        this.currentTimeUntilInterestInMinutes = this.amountOfTimeUntilInterestInMinutes;
    }

    @Override
    public void run() {

        //Decrease counter
        currentTimeUntilInterestInMinutes--;

        //Update signs
        for (Sign sign : centralBank.getSignHandler().getSigns(null, 16)) {
            sign.setLine(2, currentTimeUntilInterestInMinutes + "");
            sign.update(true);
        }


        if (currentTimeUntilInterestInMinutes <= 0) {
            //Grant interests
            for (BankingHandler<?> bankingHandler : centralBank.getBankingHandlers()) {
                bankingHandler.grantInterests(null);
            }

            if (Boolean.parseBoolean(centralBank.getConfigurationHandler().getString("interest.broadcastToConsole"))) {
                centralBank.getLogger().info("Granted interest to all players.");
            }


            //Reset counter
            currentTimeUntilInterestInMinutes = amountOfTimeUntilInterestInMinutes;
        }


    }

    public int getRemainingTime() {
        return this.currentTimeUntilInterestInMinutes;
    }

    public int getTotalTime() {
        return this.amountOfTimeUntilInterestInMinutes;
    }

}
