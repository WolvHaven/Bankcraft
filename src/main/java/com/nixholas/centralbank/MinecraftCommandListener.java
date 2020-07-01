package com.nixholas.centralbank;

import java.util.UUID;

import com.nixholas.centralbank.banking.ExperienceBankingHandler;
import com.nixholas.centralbank.banking.MoneyBankingHandler;
import com.nixholas.centralbank.constants.MaterialConstants;
import com.nixholas.centralbank.database.AccountDatabaseInterface;
import com.nixholas.centralbank.database.DatabaseManagerInterface;
import com.nixholas.centralbank.database.SignDatabaseInterface;
import com.nixholas.centralbank.database.flatfile.DatabaseManagerFlatFile;
import com.nixholas.centralbank.database.flatfile.ExperienceFlatFileInterface;
import com.nixholas.centralbank.database.flatfile.MoneyFlatFileInterface;
import com.nixholas.centralbank.database.flatfile.SignFlatFileInterface;
import com.nixholas.centralbank.database.mysql.DatabaseManagerMysql;
import com.nixholas.centralbank.database.mysql.ExperienceMysqlInterface;
import com.nixholas.centralbank.database.mysql.MoneyMysqlInterface;
import com.nixholas.centralbank.database.mysql.SignMysqlInterface;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class MinecraftCommandListener implements CommandExecutor{

	private CentralBank centralBank;
	private ConfigurationHandler coHa;

	public MinecraftCommandListener(CentralBank centralBank) {
		this.centralBank = centralBank;
		this.coHa = centralBank.getConfigurationHandler();
	}


	@SuppressWarnings("unused")
	private CentralBank plugin;
	public Double betrag;

	public void sendHelp(Player p) {
		p.sendMessage("---Bankcraft-Help---");
		p.sendMessage("/bank "+coHa.getString("signAndCommand.help")+" Shows the help page.");
		if (CentralBank.perms.has(p, "bankcraft.command.version") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.version")+" Shoes the current version of Bankcraft.");
		if (CentralBank.perms.has(p, "bankcraft.command.balance") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.balance")+" PLAYER Shows your banked money.");
		if (CentralBank.perms.has(p, "bankcraft.command.balancexp") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.balancexp")+" PLAYER Shows your banked XP.");
		if (CentralBank.perms.has(p, "bankcraft.command.deposit") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.deposit")+" AMOUNT Deposits money to your Account.");
		if (CentralBank.perms.has(p, "bankcraft.command.withdraw") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.withdraw")+" AMOUNT Withdraws money from your Account.");
		if (CentralBank.perms.has(p, "bankcraft.command.depositxp") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.depositxp")+" AMOUNT Deposits XP to your Account.");
		if (CentralBank.perms.has(p, "bankcraft.command.withdrawxp") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.withdrawxp")+" AMOUNT Withdraws XP from your Account.");
		if (CentralBank.perms.has(p, "bankcraft.command.transfer") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.transfer")+" PLAYER AMOUNT Transfers money to another Account.");
		if (CentralBank.perms.has(p, "bankcraft.command.transferxp") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.transferxp")+" PLAYER AMOUNT Transfers XP to another Account.");
		if (CentralBank.perms.has(p, "bankcraft.command.interesttimer") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.interesttimer")+" Shows the remaining time until the next wave of interests.");
		if (CentralBank.perms.has(p, "bankcraft.command.exchange") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.exchange")+" AMOUNT Exchanges money to XP.");
		if (CentralBank.perms.has(p, "bankcraft.command.exchangexp") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.exchangexp")+" AMOUNT Exchanges XP to money.");
		if (CentralBank.perms.has(p, "bankcraft.command.rankstats") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.rankstats")+" Shows the richest players.");
		if (CentralBank.perms.has(p, "bankcraft.command.rankstatsxp") || CentralBank.perms.has(p, "bankcraft.command"))
			p.sendMessage("/bank "+coHa.getString("signAndCommand.rankstatsxp")+" Shows the players with the most experience banked.");
	}

	public void sendAdminHelp(Player p) {
		p.sendMessage("---Bankcraft-AdminHelp---");
		p.sendMessage("/bankadmin help Shows the help page.");
		if (CentralBank.perms.has(p, "bankcraft.command.set") || CentralBank.perms.has(p, "bankcraft.command.admin"))
		p.sendMessage("/bankadmin "+coHa.getString("signAndCommand.admin.set")+" PLAYER AMOUNT Sets a players money.");
		if (CentralBank.perms.has(p, "bankcraft.command.setxp") || CentralBank.perms.has(p, "bankcraft.command.admin"))
		p.sendMessage("/bankadmin "+coHa.getString("signAndCommand.admin.setxp")+" PLAYER AMOUNT Sets a players XP.");
		if (CentralBank.perms.has(p, "bankcraft.command.grant") || CentralBank.perms.has(p, "bankcraft.command.admin"))
		p.sendMessage("/bankadmin "+coHa.getString("signAndCommand.admin.grant")+" PLAYER AMOUNT Grants a Player money.");
		if (CentralBank.perms.has(p, "bankcraft.command.grantxp") || CentralBank.perms.has(p, "bankcraft.command.admin"))
		p.sendMessage("/bankadmin "+coHa.getString("signAndCommand.admin.grantxp")+" PLAYER AMOUNT Grants a player XP.");
		if (CentralBank.perms.has(p, "bankcraft.command.clear") || CentralBank.perms.has(p, "bankcraft.command.admin"))
		p.sendMessage("/bankadmin "+coHa.getString("signAndCommand.admin.clear")+" PLAYER Clears money from a players Account.");
		if (CentralBank.perms.has(p, "bankcraft.command.clearxp") || CentralBank.perms.has(p, "bankcraft.command.admin"))
		p.sendMessage("/bankadmin "+coHa.getString("signAndCommand.admin.clearxp")+" PLAYER Clears XP from a players Account.");
		if (CentralBank.perms.has(p, "bankcraft.command.databaseimport") || CentralBank.perms.has(p, "bankcraft.command.admin"))
		p.sendMessage("/bankadmin "+coHa.getString("signAndCommand.admin.databaseimport")+" OLDDATA NEWDATA Moves data from one database type to another");
		if (CentralBank.perms.has(p, "bankcraft.command.reloadconfig") || CentralBank.perms.has(p, "bankcraft.command.admin"))
		p.sendMessage("/bankadmin "+coHa.getString("signAndCommand.admin.reloadconfig")+" Reloads the config of bankcraft.");
		
	}



	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String cmdlabel, final String[] vars) {
		final Player p;
		if (sender instanceof Player) {
			p = (Player) sender;
			if (cmdlabel.equalsIgnoreCase("bank") || cmdlabel.equalsIgnoreCase("bc")) {
				if (vars.length == 0) {
					sendHelp(p);
					return true;
				}
				if (vars.length == 1) {
					if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.help"))) {
						sendHelp(p);
						return true;
					}
					
					if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.version"))) {
						p.sendMessage("This server uses Bankcraft "+ centralBank.getDescription().getVersion());
						return true;
					}

					if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balance")) && (CentralBank.perms.has(p, "bankcraft.command.balance") || CentralBank.perms.has(p, "bankcraft.command"))) {
						return centralBank.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
					}
					if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balancexp")) && (CentralBank.perms.has(p, "bankcraft.command.balancexp") || CentralBank.perms.has(p, "bankcraft.command"))) {
						return centralBank.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
					}
					
					if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.interesttimer")) && (CentralBank.perms.has(p, "bankcraft.command.interesttimer") || CentralBank.perms.has(p, "bankcraft.command"))) {
						return centralBank.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
					}
					
					if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.rankstats")) && (CentralBank.perms.has(p, "bankcraft.command.rankstats") || CentralBank.perms.has(p, "bankcraft.command"))) {
						return centralBank.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
					}
					
					if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.rankstatsxp")) && (CentralBank.perms.has(p, "bankcraft.command.rankstatsxp") || CentralBank.perms.has(p, "bankcraft.command"))) {
						return centralBank.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
					}
				}
				if (vars.length == 2) {
					if (Util.isPositive(vars[1]) || vars[1].equalsIgnoreCase("all")) {
						if (vars[0].equalsIgnoreCase("add") && (CentralBank.perms.has(p, "bankcraft.admin"))) {
							BlockIterator bi = new BlockIterator(p,20);
							Block sb = null;
							while (bi.hasNext() &&  (sb = bi.next()).getType().equals(Material.AIR))
							{}
							
							if (MaterialConstants.WalledSigns.contains(sb.getType())) {
								Sign sign = (Sign) sb.getState();
								if (sign.getLine(0).contains("[Bank]")) {
									Integer typsign = -1;
									try {
										typsign = centralBank.getSignDatabaseInterface().getType(sb.getX(), sb.getY(), sb.getZ(), sb.getWorld());
									} catch (Exception e) {
										e.printStackTrace();
									}
									if (typsign == 1 || typsign == 2 || typsign == 3 || typsign == 4 || typsign == 6 || typsign == 7 || typsign == 8 || typsign == 9 || typsign == 12 || typsign == 13 || typsign == 14 || typsign == 15) {

											Integer x = sb.getX();
											Integer y = sb.getY();
											Integer z = sb.getZ();
											World w = sb.getWorld();
											
											Integer newType;
											Integer currentType = centralBank.getSignDatabaseInterface().getType(x, y, z, w);
											
											if (currentType == 1 || currentType == 2 || currentType == 6 || currentType == 7|| currentType ==  12|| currentType ==  13) {
												newType = currentType +2;
												centralBank.getSignDatabaseInterface().changeType(x, y, z, newType, w);
											}
												
												
											centralBank.getSignDatabaseInterface().addAmount(x, y, z, w, vars[1]);
											coHa.printMessage(p, "message.amountAddedSuccessfullyToSign", vars[1], p.getUniqueId());
											centralBank.getSignHandler().updateSign(sb,0);
											return true;
											}

										
									}
								}
							}
						
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balance")) && (CentralBank.perms.has(p, "bankcraft.command.balance.other") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], "", p, centralBank.getUUIDHandler().getUUID(vars[1],p));
									  }
								});
							return true;
						}
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balancexp")) && (CentralBank.perms.has(p, "bankcraft.command.balancexp.other") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], "", p, centralBank.getUUIDHandler().getUUID(vars[1],p));
									}
								});
							return true;
							}
						
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.deposit")) && (CentralBank.perms.has(p, "bankcraft.command.deposit") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
									}
								});
							return true;
							}
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.withdraw")) && (CentralBank.perms.has(p, "bankcraft.command.withdraw") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
									}
								});
							return true;
							}
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.depositxp")) && (CentralBank.perms.has(p, "bankcraft.command.depositxp") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
									}
								});
							return true;
							}
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.withdrawxp")) && (CentralBank.perms.has(p, "bankcraft.command.withdrawxp") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
									}
								});
							return true;
							}
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.exchange")) && (CentralBank.perms.has(p, "bankcraft.command.exchange") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
									}
								});
							return true;
							}
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.exchangexp")) && (CentralBank.perms.has(p, "bankcraft.command.exchangexp") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
									}
								});
							return true;
							}

						else {
							p.sendMessage(ChatColor.RED + coHa.getString("chat.prefix") + "Wrong Syntax or missing permissions! Please see /bank help for more information!");
						}

					} else {
						
						if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balance")) && (CentralBank.perms.has(p, "bankcraft.command.balance.other") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], null, p, centralBank.getUUIDHandler().getUUID(vars[1],p));
									}
								});
							return true;
							}
						else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balancexp")) && (CentralBank.perms.has(p, "bankcraft.command.balancexp.other") || CentralBank.perms.has(p, "bankcraft.command"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getInteractionHandler().interact(vars[0], null, p, centralBank.getUUIDHandler().getUUID(vars[1],p));
									}
								});
							return true;
							}
					}


				}

				if (vars.length == 3) {
					if (Util.isPositive(vars[2]) || vars[2].equalsIgnoreCase("all")) {
						if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.transfer")) && (CentralBank.perms.has(p, "bankcraft.command.transfer") || CentralBank.perms.has(p, "bankcraft.command"))) {
							final double amount;
							if (vars[2].equalsIgnoreCase("all")) {
								amount = centralBank.getMoneyDatabaseInterface().getBalance(p.getUniqueId());
							} else {
								amount = Double.parseDouble(vars[2]);
							}
							
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  ((MoneyBankingHandler) centralBank.getBankingHandlers()[0]).transferFromAccountToAccount(p.getUniqueId(), centralBank.getUUIDHandler().getUUID(vars[1],p), amount,p);
								  }
								});
							

							return true;
						} else {
							
						if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.transferxp")) && (CentralBank.perms.has(p, "bankcraft.command.transferxp") || CentralBank.perms.has(p, "bankcraft.command"))) {

							final int amount;
							if (vars[2].equalsIgnoreCase("all")) {
								amount = centralBank.getExperienceDatabaseInterface().getBalance(p.getUniqueId());
							} else {
								amount = Integer.parseInt(vars[2]);
							}
							
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  ((ExperienceBankingHandler) centralBank.getBankingHandlers()[1]).transferFromAccountToAccount(p.getUniqueId(), centralBank.getUUIDHandler().getUUID(vars[1],p), amount,p);
								  }
								});
							
							return true;
						}
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

						if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.help"))) {
							sendAdminHelp(p);
							return true;
						}
						
						if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.reloadconfig")) && (CentralBank.perms.has(p, "bankcraft.command.reloadconfig") || CentralBank.perms.has(p, "bankcraft.command.admin"))) {
							centralBank.reloadConfig();
							p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Config reloaded!");
							return true;
						}
					}
					else if (vars.length == 2) {
						if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.clear")) && (CentralBank.perms.has(p, "bankcraft.command.clear") || CentralBank.perms.has(p, "bankcraft.command.admin"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									 centralBank.getMoneyDatabaseInterface().setBalance(centralBank.getUUIDHandler().getUUID(vars[1],p), 0D);
									 p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Account cleared!");
									}
								});
							return true;
						}
						if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.clearxp")) && (CentralBank.perms.has(p, "bankcraft.command.clearxp") || CentralBank.perms.has(p, "bankcraft.command.admin"))) {
							CentralBank.execService.submit(new Runnable() {
								  public void run() {
									  centralBank.getExperienceDatabaseInterface().setBalance(centralBank.getUUIDHandler().getUUID(vars[1],p), 0);
									  p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "XP-Account cleared!");
									}
								});
							return true;
						}
					}
					else if (vars.length == 3) {
						if (Util.isDouble(vars[2])) {
							if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.set")) && (CentralBank.perms.has(p, "bankcraft.command.set") || CentralBank.perms.has(p, "bankcraft.command.admin"))) {
								CentralBank.execService.submit(new Runnable() {
									  public void run() {
										  centralBank.getMoneyDatabaseInterface().setBalance(centralBank.getUUIDHandler().getUUID(vars[1],p), Double.parseDouble(vars[2]));
										  p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Account set!");
										}
									});
								return true;
							}

							if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.setxp")) && (CentralBank.perms.has(p, "bankcraft.command.setxp") || CentralBank.perms.has(p, "bankcraft.command.admin"))) {
								CentralBank.execService.submit(new Runnable() {
									  public void run() {
										  centralBank.getExperienceDatabaseInterface().setBalance(centralBank.getUUIDHandler().getUUID(vars[1],p), Integer.parseInt(vars[2]));
										  p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "XP-Account set!");
										}
									});
								return true;
							}


							if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.grant")) && (CentralBank.perms.has(p, "bankcraft.command.grant") || CentralBank.perms.has(p, "bankcraft.command.admin"))) {
								CentralBank.execService.submit(new Runnable() {
									  public void run() {
										  centralBank.getMoneyDatabaseInterface().addToAccount(centralBank.getUUIDHandler().getUUID(vars[1],p), Double.parseDouble(vars[2]));
										  p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Granted "+vars[2]+" Money to "+vars[1]+"!");
										}
									});
								return true;
							}

							if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.grantxp")) && (CentralBank.perms.has(p, "bankcraft.command.grantxp") || CentralBank.perms.has(p, "bankcraft.command.admin"))) {
								CentralBank.execService.submit(new Runnable() {
									  public void run() {
										  centralBank.getExperienceDatabaseInterface().addToAccount(centralBank.getUUIDHandler().getUUID(vars[1],p), Integer.parseInt(vars[2]));
										  p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Granted "+vars[2]+" Experience to "+vars[1]+"!");
										}
									});
								return true;
							}
						} else {
							if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.databaseimport")) && (CentralBank.perms.has(p, "bankcraft.command.databaseimport") || CentralBank.perms.has(p, "bankcraft.command.admin"))) {
								
								p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Importing...");
								
								DatabaseManagerInterface loadDataMan = null;
								AccountDatabaseInterface<Double> loadDataMoney = null;
								AccountDatabaseInterface <Integer> loadDataXp = null;
								SignDatabaseInterface loadDataSign = null;
								
								DatabaseManagerInterface saveDataMan = null;
								AccountDatabaseInterface <Double> saveDataMoney = null;
								AccountDatabaseInterface <Integer> saveDataXp = null;
								SignDatabaseInterface saveDataSign = null;
								
								
								if (vars[1].equalsIgnoreCase("flatfile")) {
									//Load flatFile
									p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Importing from flatfile...");
									loadDataMan = new DatabaseManagerFlatFile(centralBank);
									loadDataMoney = new MoneyFlatFileInterface(centralBank);
									loadDataXp = new ExperienceFlatFileInterface(centralBank);
									loadDataSign = new SignFlatFileInterface(centralBank);
								}
								
								if (vars[1].equalsIgnoreCase("mysql")) {
									//Load mysql
									p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Importing from mysql...");
									loadDataMan = new DatabaseManagerMysql(centralBank);
									loadDataMoney = new MoneyMysqlInterface(centralBank);
									loadDataXp = new ExperienceMysqlInterface(centralBank);
									loadDataSign = new SignMysqlInterface(centralBank);
								}
								
								if (vars[2].equalsIgnoreCase("flatfile")) {
									//Load flatFile
									p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Exporting to flatfile...");
									saveDataMan = new DatabaseManagerFlatFile(centralBank);
									saveDataMoney = new MoneyFlatFileInterface(centralBank);
									saveDataXp = new ExperienceFlatFileInterface(centralBank);
									saveDataSign = new SignFlatFileInterface(centralBank);
								}
								
								if (vars[2].equalsIgnoreCase("mysql")) {
									//Load mysql
									p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Exporting to mysql...");
									saveDataMan = new DatabaseManagerMysql(centralBank);
									saveDataMoney = new MoneyMysqlInterface(centralBank);
									saveDataXp = new ExperienceMysqlInterface(centralBank);
									saveDataSign = new SignMysqlInterface(centralBank);
								}
								
								//get them ready
								loadDataMan.setupDatabase();
								saveDataMan.setupDatabase();
								
								//move money data
								for (UUID uuid: loadDataMoney.getAccounts()) {
									saveDataMoney.setBalance(uuid, loadDataMoney.getBalance(uuid));
								}
								
								//move xp data
								for (UUID uuid: loadDataXp.getAccounts()) {
									saveDataXp.setBalance(uuid, loadDataXp.getBalance(uuid));
								}
								
								//move sign data
								String amounts;
								String[] amountsArray;
								int type;
								for (Location location: loadDataSign.getLocations(-1, null)) {
									//Get amounts
									amountsArray = loadDataSign.getAmounts((int)location.getX(), (int)location.getY(), (int)location.getZ(), location.getWorld());
									amounts = amountsArray[0];
									for (int i = 1; i< amountsArray.length; i++) {
										amounts+=":"+amountsArray[i];
									}
									
									//Get type
									type = loadDataSign.getType((int)location.getX(), (int)location.getY(), (int)location.getZ(), location.getWorld());
									
									//Create new sign in save database
									saveDataSign.createNewSign((int)location.getX(), (int)location.getY(), (int)location.getZ(), location.getWorld(), type, amounts);
								}
								
								//close databases
								loadDataMan.closeDatabase();
								saveDataMan.closeDatabase();
								
								//Send success message
								p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Moved all data from "+vars[1]+" to "+vars[2]+"!");
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
			CentralBank.log.info("[Bankcraft] Please use this ingame!");
		}

		return false;
	}

}
