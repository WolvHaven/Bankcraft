package com.nixholas.centralbank.tasks;

import java.util.TimerTask;

import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.nixholas.centralbank.CentralBank;
import com.nixholas.centralbank.Util;

public class PlayerPositionTask  extends TimerTask{

	private CentralBank centralBank;

	public PlayerPositionTask (CentralBank centralBank) {
		this.centralBank = centralBank;
	}
	
	@Override
	public void run() {
		
		int maxRadius = Integer.parseInt( centralBank.getConfigurationHandler().getString("general.maximumRange"));
		for (World w: centralBank.getServer().getWorlds()) {
			Sign[] signs = centralBank.getSignHandler().getSigns(w, 17);
			
			playerLoop:
			for (Player p: centralBank.getServer().getOnlinePlayers()) {
				if (p.getWorld().equals(w)) {
					for (Sign s: signs) {
						if (Util.isInRange(p.getLocation(), s.getLocation(), maxRadius)) {
							continue playerLoop;
						}
					}
					centralBank.getInteractionHandler().stopChatInteract(p);
				}
			}
		}
	}

}
