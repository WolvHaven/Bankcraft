package de.hotmail.gurkilein.bankcraft.tasks;

import java.util.TimerTask;

import org.bukkit.block.Sign;

import de.hotmail.gurkilein.bankcraft.Bankcraft;
import de.hotmail.gurkilein.bankcraft.banking.BankingHandler;

public class InterestGrantingTask extends TimerTask{
	
	private Bankcraft bankcraft;
	private final int amountOfTimeUntilInterestInMinutes;
	private int currentTimeUntilInterestInMinutes;

	public InterestGrantingTask (Bankcraft bankcraft, int amountOfTimeUntilInterestInMinutes) {
		this.bankcraft = bankcraft;
		this.amountOfTimeUntilInterestInMinutes = amountOfTimeUntilInterestInMinutes;
		this.currentTimeUntilInterestInMinutes = this.amountOfTimeUntilInterestInMinutes;
	}

	@Override
	public void run() {
		
		//Decrease counter
		currentTimeUntilInterestInMinutes--;
		
		//Update signs
		for (Sign sign: bankcraft.getSignHandler().getSigns(null, 16)) {
			sign.setLine(2, currentTimeUntilInterestInMinutes+"");
			sign.update(true);
		}
		
		
		if (currentTimeUntilInterestInMinutes<= 0) {
			//Grant interests
			for (BankingHandler<?> bankingHandler: bankcraft.getBankingHandlers()) {
				bankingHandler.grantInterests(null);
			}
			
			if (Boolean.parseBoolean(bankcraft.getConfigurationHandler().getString("interest.broadcastToConsole"))) {
				bankcraft.getLogger().info("Granted interest to all players.");
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
