package de.hotmail.gurkilein.bankcraft;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class MinecraftPlayerListener implements Listener{

	
	private Bankcraft bankcraft;
	private ConfigurationHandler coHa;

	public MinecraftPlayerListener(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
		this.coHa = bankcraft.getConfigurationHandler();
	}

	@EventHandler
	public void onklick(PlayerInteractEvent event) throws Exception {
		Player p = event.getPlayer();
		if (event.getClickedBlock() != null) {
			String block = event.getClickedBlock().getType().toString();
			if (block == "WALL_SIGN") {
				if (((Sign) event.getClickedBlock().getState()).getLine(0).contains("[Bank]")) {
					if (!p.isSneaking()) {
						Integer typ = bankcraft.getSignDatabaseInterface().getType(event.getClickedBlock().getX(), event.getClickedBlock().getY(), event.getClickedBlock().getZ(), event.getClickedBlock().getWorld());
						if (typ == -1) {
							if (Bankcraft.perms.has(p, "bankcraft.admin")) {
								p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Reinitializing Bankcraftsign...");
								Sign sign = (Sign) event.getClickedBlock().getState();
								if (((sign.getLine(1).contains(coHa.getString("sign.deposit")) | sign.getLine(1).contains(coHa.getString("sign.exchange")) | sign.getLine(1).contains(coHa.getString("sign.exchangexp")) | sign.getLine(1).contains(coHa.getString("sign.debit")) | sign.getLine(1).contains(coHa.getString("sign.debitxp")) | sign.getLine(1).contains(coHa.getString("sign.depositxp"))) && (Util.isPositive(sign.getLine(2))) || sign.getLine(2).equalsIgnoreCase("all")) == true) {
									//ERSTELLEN DER BANK
									sign.setLine(0, coHa.getString("sign.color") + "[Bank]");
									double betrag = 0;
									String typreihe = sign.getLine(1);
									int signX = sign.getBlock().getX();
									int signY = sign.getBlock().getY();
									int signZ = sign.getBlock().getZ();
									
									if (typreihe.equals(coHa.getString("sign.deposit"))) {
										if (sign.getLine(2).equalsIgnoreCase("all")) {
											sign.setLine(2, "All");
											betrag = -1;
										} else {
											betrag = new Double(sign.getLine(2));
										}
										typ = 1;
									}
									if (typreihe.equals(coHa.getString("sign.debit"))) {
										if (sign.getLine(2).equalsIgnoreCase("all")) {
											sign.setLine(2, "All");
											betrag = -1;
										} else {
											betrag = new Double(sign.getLine(2));
										}
										typ = 2;
									}

									if (typreihe.equals(coHa.getString("sign.depositxp"))) {
										if (sign.getLine(2).equalsIgnoreCase("all")) {
											sign.setLine(2, "All");
											betrag = -1;
										} else {
											betrag = new Double(sign.getLine(2));
										}
										typ = 6;
									}
									if (typreihe.equals(coHa.getString("sign.debitxp"))) {
										if (sign.getLine(2).equalsIgnoreCase("all")) {
											sign.setLine(2, "All");
											betrag = -1;
										} else {
											betrag = new Double(sign.getLine(2));
										}
										typ = 7;
									}
									if (typreihe.equals(coHa.getString("sign.exchange"))) {
										if (sign.getLine(2).equalsIgnoreCase("all")) {
											sign.setLine(2, "All");
											betrag = -1;
										} else {
											betrag = new Double(sign.getLine(2));
										}
										typ = 12;
									}
									if (typreihe.equals(coHa.getString("sign.exchangexp"))) {
										if (sign.getLine(2).equalsIgnoreCase("all")) {
											sign.setLine(2, "All");
											betrag = -1;
										} else {
											betrag = new Double(sign.getLine(2));
										}
										typ = 13;
									}
									bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, sign.getBlock().getWorld(), typ, betrag+"");
									
									coHa.printMessage(p, "message.createdSignSuccessfully", "0", p.getName());

								} else {
									if (sign.getLine(1).equals(coHa.getString("sign.balance")) | (sign.getLine(1).equals(coHa.getString("sign.balancexp")))) {
										sign.setLine(0, coHa.getString("sign.color") + "[Bank]");
										int signX = sign.getBlock().getX();
										int signY = sign.getBlock().getY();
										int signZ = sign.getBlock().getZ();
										if (sign.getLine(1).equals(coHa.getString("sign.balancexp"))) {
											if (sign.getLine(2).isEmpty()) {
												bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, sign.getBlock().getWorld(), 5, "0");
											} else {
												bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, sign.getBlock().getWorld(), 11, "0");
											}
										} else {
											if (sign.getLine(2).isEmpty()) {
												bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, sign.getBlock().getWorld(), 0, "0");
											} else {
												bankcraft.getSignDatabaseInterface().createNewSign(signX, signY, signZ, sign.getBlock().getWorld(), 10, "0");
											}
										}
										coHa.printMessage(p, "message.createdSignSuccessfully", "0", p.getName());
									} else {
										p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Could not reinitialize the sign! Removing it now!");
										event.getClickedBlock().setType(Material.AIR);
									}
								}
							} else {
								p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Sign not in Database! Please contact an admin!");
							}
							return;
						}
						if (Bankcraft.perms.has(p, "bankcraft.use") | (Bankcraft.perms.has(p, "bankcraft.use.money") && (typ == 0 | typ == 1 | typ == 2 | typ == 3 | typ == 4)) | (Bankcraft.perms.has(p, "bankcraft.use.exp") && (typ == 5 | typ == 6 | typ == 7 | typ == 8 | typ == 9))) {
							if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
								if (((Sign) event.getClickedBlock().getState()).getLine(1).contains(coHa.getString("message.balance")) | ((Sign) event.getClickedBlock().getState()).getLine(1).contains(coHa.getString("message.balancexp"))) {
									
									if (typ == 0) {
										coHa.printMessage(p, "message.balance", "0", p.getName());
									} else {
										coHa.printMessage(p, "message.balancexp", "0", p.getName());
									}
									
								} else {
									if (typ == 3 | typ == 4 | typ == 8 | typ == 9 | typ == 14 | typ == 15) {
										bankcraft.getSignHandler().updateSign(event.getClickedBlock(), 1);
									}
								}
							}
							if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
								bankcraft.getSignHandler().leftClickSign(p, ((Sign) event.getClickedBlock().getState()).getLine(2), typ, event.getClickedBlock(), ((Sign) event.getClickedBlock().getState()).getLine(3));
							}
						}
					} else {
						coHa.printMessage(p, "message.notAllowed", "0", p.getName());
					}
				}
				((Sign) event.getClickedBlock().getState()).update();
			}
		}
	}

}
