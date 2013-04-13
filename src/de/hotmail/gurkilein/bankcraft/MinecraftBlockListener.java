package de.hotmail.gurkilein.bankcraft;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;


public class MinecraftBlockListener implements Listener{
	
	private Bankcraft bankcraft;
	private ConfigurationHandler coHa;

	public MinecraftBlockListener(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
		this.coHa = bankcraft.getConfigurationHandler();
	}
	
	private static final BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent event) throws Exception {
		Player p = event.getPlayer();
		Block testblock = event.getBlock();
		Material type = testblock.getType();
		//Check blocks around destroyed block for signs
		for (BlockFace face : faces) {
			type = testblock.getRelative(face).getType();
			Block nachbarblock = testblock.getRelative(face);

			//if sign found
			if (type == Material.WALL_SIGN) {
				if ((nachbarblock.getData() == 3 & face.equals(BlockFace.WEST)) | (nachbarblock.getData() == 2 & face.equals(BlockFace.EAST)) | (nachbarblock.getData() == 4 & face.equals(BlockFace.NORTH)) | (nachbarblock.getData() == 5 & face.equals(BlockFace.SOUTH))) {

					//check the found sign for the players name.
					Sign sign = (Sign) testblock.getRelative(face).getState();
					if (sign.getLine(0).contains("[Bank]")) {
						if (!Bankcraft.perms.has(p, "bankcraft.admin")) {
							coHa.printMessage(p, "message.notAllowed", "0", p.getName());
							event.setCancelled(true);
							return;
						} else {
							bankcraft.getSignDatabaseInterface().removeSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
							coHa.printMessage(p, "message.removedSignSuccessfully", "0", p.getName());
						}
					}
				}
			}
		}
		String block = event.getBlock().getType().toString();
		if ("WALL_SIGN".equals(block)) {
			Sign sign = ((Sign) event.getBlock().getState());
			if (sign.getLine(0).contains("[Bank]")) {
				if (p.getGameMode().equals(GameMode.CREATIVE) && !p.isSneaking()) {
					event.setCancelled(true);
					return;
				}
				if (Bankcraft.perms.has(p, "bankcraft.admin")) {
					bankcraft.getSignDatabaseInterface().removeSign(sign.getX(), sign.getY(), sign.getZ(), sign.getWorld());
				} else {
					event.setCancelled(true);
					coHa.printMessage(p, "message.notAllowed", "0", p.getName());
				}
			}
		}
	}

	@EventHandler
	public void onSignPlace(SignChangeEvent event) {
		Player p = event.getPlayer();
		String firstRow = event.getLine(0);
		if (firstRow.equalsIgnoreCase("[Bank]")) {
			if (Bankcraft.perms.has(p, "bankcraft.admin")) {
				if (((event.getLine(1).equals(coHa.getString("sign.deposit")) | event.getLine(1).equals(coHa.getString("sign.exchange")) | event.getLine(1).equals(coHa.getString("sign.exchangexp")) | event.getLine(1).equals(coHa.getString("sign.withdraw")) | event.getLine(1).equals(coHa.getString("sign.withdrawxp")) | event.getLine(1).equals(coHa.getString("sign.depositxp"))) && (Util.isPositive(event.getLine(2))) || event.getLine(2).equalsIgnoreCase("all")) == true) {
					//ERSTELLEN DER BANK(setting up a sign)
					event.setLine(0, coHa.getString("sign.color") + "[Bank]");
					double amount = 0;
					String typeRow = event.getLine(1);
					int signX = event.getBlock().getX();
					int signY = event.getBlock().getY();
					int signZ = event.getBlock().getZ();
					Integer typ = -1;
					if (typeRow.equals(coHa.getString("sign.deposit"))) {
						if (event.getLine(2).equalsIgnoreCase("all")) {
							event.setLine(2, "All");
							amount = -1;
						} else {
							amount = new Double(event.getLine(2));
						}
						typ = 1;
					}
					if (typeRow.equals(coHa.getString("sign.withdraw"))) {
						if (event.getLine(2).equalsIgnoreCase("all")) {
							event.setLine(2, "All");
							amount = -1;
						} else {
							amount = new Double(event.getLine(2));
						}
						typ = 2;
					}

					if (typeRow.equals(coHa.getString("sign.depositxp"))) {
						if (event.getLine(2).equalsIgnoreCase("all")) {
							event.setLine(2, "All");
							amount = -1;
						} else {
							amount = new Double(event.getLine(2));
						}
						typ = 6;
					}
					if (typeRow.equals(coHa.getString("sign.withdrawxp"))) {
						if (event.getLine(2).equalsIgnoreCase("all")) {
							event.setLine(2, "All");
							amount = -1;
						} else {
							amount = new Double(event.getLine(2));
						}
						typ = 7;
					}
					if (typeRow.equals(coHa.getString("sign.exchange"))) {
						if (event.getLine(2).equalsIgnoreCase("all")) {
							event.setLine(2, "All");
							amount = -1;
						} else {
							amount = new Double(event.getLine(2));
						}
						typ = 12;
					}
					if (typeRow.equals(coHa.getString("sign.exchangexp"))) {
						if (event.getLine(2).equalsIgnoreCase("all")) {
							event.setLine(2, "All");
							amount = -1;
						} else {
							amount = new Double(event.getLine(2));
						}
						typ = 13;
					}
					bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, event.getBlock().getWorld(), typ, amount + "");
					coHa.printMessage(p, "message.createdSignSuccessfully", "", p.getName());

				} else {
					if (event.getLine(1).equals(coHa.getString("sign.balance")) | (event.getLine(1).equals(coHa.getString("sign.balancexp")))) {
						event.setLine(0, coHa.getString("sign.color") + "[Bank]");
						int signX = event.getBlock().getX();
						int signY = event.getBlock().getY();
						int signZ = event.getBlock().getZ();
						if (event.getLine(1).equals(coHa.getString("sign.balancexp"))) {
							if (event.getLine(2).isEmpty()) {
								bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, event.getBlock().getWorld(), 5, "0");
								
							} else {
								bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, event.getBlock().getWorld(), 11, "0");
								
							}
						} else {
							if (event.getLine(2).isEmpty()) {
								bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, event.getBlock().getWorld(), 0, "0");
								
							} else {
								bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, event.getBlock().getWorld(), 10, "0");
								
							}
						}
						coHa.printMessage(p, "message.createdSignSuccessfully", "0", p.getName());
					} else {
						coHa.printMessage(p, "message.errorWhileCreatingSign", "0", p.getName());
						event.setLine(0, "");
						event.setLine(1, "");
						event.setLine(2, "");
						event.setLine(3, "");
					}
				}
			} else {
				coHa.printMessage(p, "message.notAllowed", "0", p.getName());
				event.setLine(0, "");
				event.setLine(1, "");
				event.setLine(2, "");
				event.setLine(3, "");
			}
		}

	}
}
