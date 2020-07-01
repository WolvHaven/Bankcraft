package com.nixholas.centralbank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignHandler {
	
	private Bankcraft bankcraft;
	private Map<Block, Integer> signPosition = new HashMap<Block, Integer>();
	
	public SignHandler(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
	}

	



	public String updateSign(Block clickedBlock, int steps) {
		String[] scrollingSignArray = bankcraft.getSignDatabaseInterface().getAmounts(clickedBlock.getX(), clickedBlock.getY(), clickedBlock.getZ(), clickedBlock.getWorld());
		Sign sign = (Sign) clickedBlock.getState();
		if (signPosition.containsKey(clickedBlock)) {
			if (signPosition.get(clickedBlock) < (scrollingSignArray.length - steps)) {
				signPosition.put(clickedBlock, signPosition.get(clickedBlock) + steps);
				sign.setLine(2, "> " + scrollingSignArray[signPosition.get(clickedBlock)] + "");
				if (signPosition.get(clickedBlock).equals(scrollingSignArray.length - 1)) {
					sign.setLine(3, scrollingSignArray[0] + "");
				} else {
					sign.setLine(3, (scrollingSignArray[signPosition.get(clickedBlock) + 1]) + "");
				}
			} else {
				signPosition.put(clickedBlock, 0);
				sign.setLine(2, "> " + scrollingSignArray[0] + "");
				sign.setLine(3, scrollingSignArray[1] + "");
			}
		} else {
			signPosition.put(clickedBlock, 0);
			sign.setLine(2, "> " + scrollingSignArray[0] + "");
			sign.setLine(3, scrollingSignArray[1] + "");
		}
		sign.update(true);
		return (String) scrollingSignArray[signPosition.get(clickedBlock)];
	}

	
	public Sign[] getSigns(World world, int type) {
		
		Location[] locations = bankcraft.getSignDatabaseInterface().getLocations(type, world);
		List <Sign> signsList = new ArrayList<Sign>();
		Sign sign;
		
		for (int i = 0; i<locations.length; i++) {
			try {
			sign = ((Sign)locations[i].getBlock().getState());
			signsList.add(sign);
			} catch (ClassCastException e) {
				Bankcraft.log.warning("Found broken Sign... Removing... ("+locations[i].getBlockX()+":"+locations[i].getBlockY()+":"+locations[i].getBlockZ()+")");
				bankcraft.getSignDatabaseInterface().removeSign(locations[i].getBlockX(), locations[i].getBlockY(), locations[i].getBlockZ(), locations[i].getWorld());
			}
		}
		
		return signsList.toArray(new Sign[0]);
	}




	
}
