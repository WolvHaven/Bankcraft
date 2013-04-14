package de.hotmail.gurkilein.bankcraft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.hotmail.gurkilein.bankcraft.banking.ExperienceBankingHandler;
import de.hotmail.gurkilein.bankcraft.banking.ExperienceBukkitHandler;
import de.hotmail.gurkilein.bankcraft.banking.MoneyBankingHandler;

public class MinecraftCommandListener implements CommandExecutor{

	private Bankcraft bankcraft;
	private ConfigurationHandler coHa;

	public MinecraftCommandListener(Bankcraft bankcraft) {
		this.bankcraft = bankcraft;
		this.coHa = bankcraft.getConfigurationHandler();
	}


	@SuppressWarnings("unused")
	private Bankcraft plugin;
	public Double betrag;

	public void sendHelp(Player p) {
		p.sendMessage("---Bankcraft-Help---");
		p.sendMessage("/bank help Shows the help page.");
		p.sendMessage("/bank balance PLAYER Shows your banked money.");
		p.sendMessage("/bank balancexp PLAYER Shows your banked XP.");
		p.sendMessage("/bank deposit AMOUNT Deposits money to your Account.");
		p.sendMessage("/bank withdraw AMOUNT Withdraws money from your Account.");
		p.sendMessage("/bank depositxp AMOUNT Deposits XP to your Account.");
		p.sendMessage("/bank withdrawxp AMOUNT Withdraws XP from your Account.");
		p.sendMessage("/bank transfer PLAYER AMOUNT Transfers money to another Account.");
		p.sendMessage("/bank transferxp PLAYER AMOUNT Transfers XP to another Account.");
		p.sendMessage("/bank interesttimer Shows the remaining time until the next wave of interests.");
		p.sendMessage("/bank exchange AMOUNT Exchanges money to XP.");
		p.sendMessage("/bank exchangexp AMOUNT Exchanges XP to money.");
	}

	public void sendAdminHelp(Player p) {
		p.sendMessage("---Bankcraft-AdminHelp---");
		p.sendMessage("/bankadmin help Shows the help page.");
		p.sendMessage("/bankadmin set PLAYER AMOUNT Sets a players money.");
		p.sendMessage("/bankadmin setxp PLAYER AMOUNT Sets a players XP.");
		p.sendMessage("/bankadmin grant PLAYER AMOUNT Grants a Player money.");
		p.sendMessage("/bankadmin grantxp PLAYER AMOUNT Grants a player XP.");
		p.sendMessage("/bankadmin clear PLAYER Clears money from a players Account.");
		p.sendMessage("/bankadmin clearxp PLAYER Clears XP from a players Account.");
	}



	@Override
	public boolean onCommand(CommandSender sender, Command command, String cmdlabel, String[] vars) {
		Player p;
		if (sender instanceof Player) {
			p = (Player) sender;
			if (cmdlabel.equalsIgnoreCase("bank") || cmdlabel.equalsIgnoreCase("bc")) {
				if (vars.length == 0) {
					sendHelp(p);
					return true;
				}
				if (vars.length == 1) {
					if (vars[0].equalsIgnoreCase(coHa.getString("command.help"))) {
						sendHelp(p);
						return true;
					}

					if (vars[0].equalsIgnoreCase(coHa.getString("command.balance")) && (Bankcraft.perms.has(p, "bankcraft.command.balance") || Bankcraft.perms.has(p, "bankcraft.command"))) {
						bankcraft.getConfigurationHandler().printMessage(p, "message.balance", "", p.getName());
						return true;
					}
					if (vars[0].equalsIgnoreCase(coHa.getString("command.balancexp")) && (Bankcraft.perms.has(p, "bankcraft.command.balancexp") || Bankcraft.perms.has(p, "bankcraft.command"))) {
						bankcraft.getConfigurationHandler().printMessage(p, "message.balancexp", "", p.getName());
						return true;
					}
					
					if (vars[0].equalsIgnoreCase(coHa.getString("command.interesttimer")) && (Bankcraft.perms.has(p, "bankcraft.command.interesttimer") || Bankcraft.perms.has(p, "bankcraft.command"))) {
						bankcraft.getConfigurationHandler().printMessage(p, "message.interestTimer", "", p.getName());
						return true;
					}
				}
				if (vars.length == 2) {
					if (Util.isPositive(vars[1]) || vars[1].equalsIgnoreCase("all")) {
						if (vars[0].equalsIgnoreCase("add") && (Bankcraft.perms.has(p, "bankcraft.admin"))) {
							Block signblock = p.getTargetBlock(null, 50);
							if (signblock.getType() == Material.WALL_SIGN) {
								Sign sign = (Sign) signblock.getState();
								if (sign.getLine(0).contains("[Bank]")) {
									Integer typsign = -1;
									try {
										typsign = bankcraft.getSignDatabaseInterface().getType(signblock.getX(), signblock.getY(), signblock.getZ(), signblock.getWorld());
									} catch (Exception e) {
										e.printStackTrace();
									}
									if (typsign == 1 | typsign == 2 | typsign == 3 | typsign == 6 | typsign == 7 | typsign == 12 | typsign == 13) {

											Integer x = signblock.getX();
											Integer y = signblock.getY();
											Integer z = signblock.getZ();
											World w = signblock.getWorld();
											
											Integer newType;
											Integer currentType = bankcraft.getSignDatabaseInterface().getType(x, y, z, w);
											
											if (currentType == 1 || currentType == 2 || currentType == 6 || currentType == 7|| currentType ==  12|| currentType ==  13) {
												newType = currentType +2;
												bankcraft.getSignDatabaseInterface().changeType(x, y, z, newType, w);
											}
												
												
											bankcraft.getSignDatabaseInterface().addAmount(x, y, z, w, vars[1]);
											coHa.printMessage(p, "message.amountAddedSuccessfullyToSign", vars[1], p.getName());
											bankcraft.getSignHandler().updateSign(signblock,0);
											return true;
											}

										
									}
								}
							}
						

						else if (vars[0].equalsIgnoreCase(coHa.getString("command.deposit")) && (Bankcraft.perms.has(p, "bankcraft.command.deposit") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							double amount;
							if (vars[1].equalsIgnoreCase("all")) {
								amount = Bankcraft.econ.getBalance(p.getName());
							} else {
								amount = Double.parseDouble(vars[1]);
							}
							((MoneyBankingHandler)bankcraft.getBankingHandlers()[0]).transferFromPocketToAccount(p, p.getName(), amount,p);
							return true;
						}


						else if (vars[0].equalsIgnoreCase(coHa.getString("command.withdraw")) && (Bankcraft.perms.has(p, "bankcraft.command.withdraw") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							double amount;
							if (vars[1].equalsIgnoreCase("all")) {
								amount = bankcraft.getMoneyDatabaseInterface().getBalance(p.getName());
							} else {
								amount = Double.parseDouble(vars[1]);
							}
							((MoneyBankingHandler)bankcraft.getBankingHandlers()[0]).transferFromAccountToPocket(p.getName(), p, amount,p);
							return true;
						}

						else if (vars[0].equalsIgnoreCase(coHa.getString("command.depositxp")) && (Bankcraft.perms.has(p, "bankcraft.command.depositxp") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							int amount;
							if (vars[1].equalsIgnoreCase("all")) {
								amount = ExperienceBukkitHandler.getTotalExperience(p);
							} else {
								amount = Integer.parseInt(vars[1]);
							}
							((ExperienceBankingHandler)bankcraft.getBankingHandlers()[1]).transferFromPocketToAccount(p, p.getName(), amount,p);
							return true;
						}
						else if (vars[0].equalsIgnoreCase(coHa.getString("command.withdrawxp")) && (Bankcraft.perms.has(p, "bankcraft.command.withdrawxp") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							int amount;
							if (vars[1].equalsIgnoreCase("all")) {
								amount = bankcraft.getExperienceDatabaseInterface().getBalance(p.getName());
							} else {
								amount = Integer.parseInt(vars[1]);
							}
							((ExperienceBankingHandler)bankcraft.getBankingHandlers()[1]).transferFromAccountToPocket(p.getName(), p, amount,p);
							return true;
						}

						else if (vars[0].equalsIgnoreCase(coHa.getString("command.exchange")) && (Bankcraft.perms.has(p, "bankcraft.command.exchange") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							//TODO
							//bankInteract.use(p, vars[1], 12, null, "");
							return true;
						}

						else if (vars[0].equalsIgnoreCase(coHa.getString("command.exchangexp")) && (Bankcraft.perms.has(p, "bankcraft.command.exchangexp") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							//TODO
							//bankInteract.use(p, vars[1], 13, null, "");
							return true;
						}

						else {
							p.sendMessage(ChatColor.RED + coHa.getString("chat.prefix") + "Wrong Syntax or missing permissions! Please see /bank help for more information!");
						}

					} else {
						if (vars[0].equalsIgnoreCase(coHa.getString("command.balance")) && (Bankcraft.perms.has(p, "bankcraft.command.balance.other") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							bankcraft.getConfigurationHandler().printMessage(p, "message.balance", "", vars[1]);
							return true;
						}


						else if (vars[0].equalsIgnoreCase(coHa.getString("command.balancexp")) && (Bankcraft.perms.has(p, "bankcraft.command.balancexp.other") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							bankcraft.getConfigurationHandler().printMessage(p, "message.balancexp", "", vars[1]);
							return true;
						}
					}


				}

				if (vars.length == 3) {
					if (Util.isPositive(vars[2]) || vars[2].equalsIgnoreCase("all")) {
						if (vars[0].equalsIgnoreCase(coHa.getString("command.transfer")) && (Bankcraft.perms.has(p, "bankcraft.command.transfer") || Bankcraft.perms.has(p, "bankcraft.command"))) {
							double amount;
							if (vars[1].equalsIgnoreCase("all")) {
								amount = bankcraft.getMoneyDatabaseInterface().getBalance(p.getName());
							} else {
								amount = Double.parseDouble(vars[1]);
							}
						
							((MoneyBankingHandler)bankcraft.getBankingHandlers()[0]).transferFromAccountToAccount(p.getName(), vars[1], amount,p);
							coHa.printMessage(p, "message.transferedSuccessfully", amount+"", vars[1]);

							return true;
						}
						} else {
							
						if (vars[0].equalsIgnoreCase(coHa.getString("command.transferxp")) && (Bankcraft.perms.has(p, "bankcraft.command.transferxp") || Bankcraft.perms.has(p, "bankcraft.command"))) {

							int amount;
							if (vars[1].equalsIgnoreCase("all")) {
								amount = bankcraft.getExperienceDatabaseInterface().getBalance(p.getName());
							} else {
								amount = Integer.parseInt(vars[1]);
							}
							
							((ExperienceBankingHandler)bankcraft.getBankingHandlers()[0]).transferFromAccountToAccount(p.getName(), vars[1], amount,p);
							coHa.printMessage(p, "message.transferedSuccessfullyXp", amount+"", vars[1]);

							return true;
						}
						}

						} else {
							p.sendMessage(ChatColor.RED + coHa.getString("chat.prefix") + "Wrong Syntax or missing permissions! Please see /bank help for more information!");
							return true;
						}
			} else {

				if (cmdlabel.equalsIgnoreCase("bankadmin") || cmdlabel.equalsIgnoreCase("bcadmin")) {
					if (vars.length == 0) {
						sendAdminHelp(p);
						return true;
					}
					else if (vars.length == 1) {

						if (vars[0].equalsIgnoreCase(coHa.getString("command.admin.help"))) {
							sendAdminHelp(p);
							return true;
						}
					}
					else if (vars.length == 2) {
						if (vars[0].equalsIgnoreCase(coHa.getString("command.admin.clear")) && (Bankcraft.perms.has(p, "bankcraft.command.clear") || Bankcraft.perms.has(p, "bankcraft.command.admin"))) {
							bankcraft.getMoneyDatabaseInterface().setBalance(vars[1], 0D);
							p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Account cleared!");
							return true;
						}
						if (vars[0].equalsIgnoreCase(coHa.getString("command.admin.clearxp")) && (Bankcraft.perms.has(p, "bankcraft.command.clearxp") || Bankcraft.perms.has(p, "bankcraft.command.admin"))) {
							bankcraft.getExperienceDatabaseInterface().setBalance(vars[1], 0);
							p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "XP-Account cleared!");
							return true;
						}
					}
					else if (vars.length == 3) {
						if (Util.isDouble(vars[2])) {
							if (vars[0].equalsIgnoreCase(coHa.getString("command.admin.set")) && (Bankcraft.perms.has(p, "bankcraft.command.set") || Bankcraft.perms.has(p, "bankcraft.command.admin"))) {
								bankcraft.getMoneyDatabaseInterface().setBalance(vars[1], Double.parseDouble(vars[2]));
								p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Account set!");
								return true;
							}

							if (vars[0].equalsIgnoreCase(coHa.getString("command.admin.setxp")) && (Bankcraft.perms.has(p, "bankcraft.command.setxp") || Bankcraft.perms.has(p, "bankcraft.command.admin"))) {
								bankcraft.getExperienceDatabaseInterface().setBalance(vars[1], Integer.parseInt(vars[2]));
								p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "XP-Account set!");
								return true;
							}


							if (vars[0].equalsIgnoreCase(coHa.getString("command.admin.grant")) && (Bankcraft.perms.has(p, "bankcraft.command.grant") || Bankcraft.perms.has(p, "bankcraft.command.admin"))) {
								bankcraft.getMoneyDatabaseInterface().addToAccount(vars[1], Double.parseDouble(vars[2]));
								p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Granted "+vars[2]+" Money to "+vars[1]+"!");
								return true;
							}

							if (vars[0].equalsIgnoreCase(coHa.getString("command.admin.grantxp")) && (Bankcraft.perms.has(p, "bankcraft.command.grantxp") || Bankcraft.perms.has(p, "bankcraft.command.admin"))) {
								bankcraft.getExperienceDatabaseInterface().addToAccount(vars[1], Integer.parseInt(vars[2]));
								p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Granted "+vars[2]+" Experience to "+vars[1]+"!");
								return true;
							}
						}
					}
					else {
						p.sendMessage(ChatColor.RED + coHa.getString("chat.prefix") + "Wrong Syntax or missing permissions! Please see /bank help for more information!");
					}
					return true;
				}
			}
		} else {
			Bankcraft.log.info("[Bankcraft] Please use this ingame!");
		}

		return false;
	}

}
