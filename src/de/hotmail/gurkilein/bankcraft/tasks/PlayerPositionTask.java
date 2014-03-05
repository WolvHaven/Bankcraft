package de.hotmail.gurkilein.bankcraft.tasks;

import java.util.TimerTask;

import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import de.hotmail.gurkilein.bankcraft.Bankcraft;
import de.hotmail.gurkilein.bankcraft.Util;

public class PlayerPositionTask  extends TimerTask{

	private Bankcraft bankcraft;

	public PlayerPositionTask (Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}
	
	@Override
	public void run() {
		
		int maxRadius = Integer.parseInt( bankcraft.getConfigurationHandler().getString("general.maximumRange"));
		for (World w: bankcraft.getServer().getWorlds()) {
			Sign[] signs = bankcraft.getSignHandler().getSigns(w, 17);
			
			playerLoop:
			for (Player p: bankcraft.getServer().getOnlinePlayers()) {
				if (p.getWorld().equals(w)) {
					for (Sign s: signs) {
						if (Util.isInRange(p.getLocation(), s.getLocation(), maxRadius)) {
							continue playerLoop;
						}
					}
					bankcraft.getInteractionHandler().stopChatInteract(p);
				}
			}
		}
	}

}
