package de.hotmail.gurkilein.bankcraft;

import java.util.TimerTask;

import de.hotmail.gurkilein.bankcraft.banking.BankingHandler;

public class InterestGrantingTask extends TimerTask{
	
	private Bankcraft bankcraft;

	public InterestGrantingTask (Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}

	@Override
	public void run() {
		//Grant interests
		for (BankingHandler<?> bankingHandler: bankcraft.getBankingHandlers()) {
			bankingHandler.grantInterests(null);
		}
		bankcraft.getLogger().info("[Bankcraft] Granted interest to all players.");
		
		
	}

}
