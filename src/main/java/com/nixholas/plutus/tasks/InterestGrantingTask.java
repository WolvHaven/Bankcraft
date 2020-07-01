package com.nixholas.plutus.tasks;

import com.nixholas.plutus.PlutusCore;
import com.nixholas.plutus.banking.BankingHandler;
import org.bukkit.block.Sign;

import java.util.TimerTask;

public class InterestGrantingTask extends TimerTask {

    private final PlutusCore plutusCore;
    private final int amountOfTimeUntilInterestInMinutes;
    private int currentTimeUntilInterestInMinutes;

    public InterestGrantingTask(PlutusCore plutusCore, int amountOfTimeUntilInterestInMinutes) {
        this.plutusCore = plutusCore;
        this.amountOfTimeUntilInterestInMinutes = amountOfTimeUntilInterestInMinutes;
        this.currentTimeUntilInterestInMinutes = this.amountOfTimeUntilInterestInMinutes;
    }

    @Override
    public void run() {

        //Decrease counter
        currentTimeUntilInterestInMinutes--;

        //Update signs
        for (Sign sign : plutusCore.getSignHandler().getSigns(null, 16)) {
            sign.setLine(2, currentTimeUntilInterestInMinutes + "");
            sign.update(true);
        }


        if (currentTimeUntilInterestInMinutes <= 0) {
            //Grant interests
            for (BankingHandler<?> bankingHandler : plutusCore.getBankingHandlers()) {
                bankingHandler.grantInterests(null);
            }

            if (Boolean.parseBoolean(plutusCore.getConfigurationHandler().getString("interest.broadcastToConsole"))) {
                plutusCore.getLogger().info("Granted interest to all players.");
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
