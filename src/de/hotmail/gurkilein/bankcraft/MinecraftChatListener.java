package de.hotmail.gurkilein.bankcraft;
//All of the imports.
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MinecraftChatListener implements Listener{
    private Bankcraft bankcraft;

	public MinecraftChatListener(Bankcraft bankcraft) {
		this.bankcraft = bankcraft; 
	}

	@EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (!((Integer)bankcraft.getSignHandler().getChatSignMap().get(event.getPlayer()) == 0)) {
		//1 = deposit, xpdeposit, withdraw etc.
			if ((Integer)bankcraft.getSignHandler().getChatSignMap().get(event.getPlayer()) == 1) {
			
		}else
			//Waiting for player to input a value.
			if ((Integer)bankcraft.getSignHandler().getChatSignMap().get(event.getPlayer()) == 2) {
				if (Util.isInteger(event.getMessage())) {
					
				}
			}
		}
	}
}
