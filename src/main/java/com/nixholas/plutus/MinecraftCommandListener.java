package com.nixholas.plutus;

import com.nixholas.plutus.banking.ExperienceBankingHandler;
import com.nixholas.plutus.banking.MoneyBankingHandler;
import com.nixholas.plutus.constants.MaterialConstants;
import com.nixholas.plutus.database.AccountDatabaseInterface;
import com.nixholas.plutus.database.DatabaseManagerInterface;
import com.nixholas.plutus.database.SignDatabaseInterface;
import com.nixholas.plutus.database.flatfile.DatabaseManagerFlatFile;
import com.nixholas.plutus.database.flatfile.ExperienceFlatFileInterface;
import com.nixholas.plutus.database.flatfile.MoneyFlatFileInterface;
import com.nixholas.plutus.database.flatfile.SignFlatFileInterface;
import com.nixholas.plutus.database.mysql.DatabaseManagerMysql;
import com.nixholas.plutus.database.mysql.ExperienceMysqlInterface;
import com.nixholas.plutus.database.mysql.MoneyMysqlInterface;
import com.nixholas.plutus.database.mysql.SignMysqlInterface;
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

import java.util.UUID;

public class MinecraftCommandListener implements CommandExecutor {

    private final PlutusCore plutusCore;
    private final ConfigurationHandler coHa;

    public MinecraftCommandListener(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;
        this.coHa = plutusCore.getConfigurationHandler();
    }


    @SuppressWarnings("unused")
    private PlutusCore plugin;
    public Double betrag;

    public void sendHelp(Player p) {
        p.sendMessage("---Centralbank-Help---");
        p.sendMessage("/bank " + coHa.getString("signAndCommand.help") + " Shows the help page.");
        if (PlutusCore.perms.has(p, "Centralbank.command.version") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.version") + " Shoes the current version of Centralbank.");
        if (PlutusCore.perms.has(p, "Centralbank.command.balance") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.balance") + " PLAYER Shows your banked money.");
        if (PlutusCore.perms.has(p, "Centralbank.command.balancexp") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.balancexp") + " PLAYER Shows your banked XP.");
        if (PlutusCore.perms.has(p, "Centralbank.command.deposit") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.deposit") + " AMOUNT Deposits money to your Account.");
        if (PlutusCore.perms.has(p, "Centralbank.command.withdraw") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.withdraw") + " AMOUNT Withdraws money from your Account.");
        if (PlutusCore.perms.has(p, "Centralbank.command.depositxp") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.depositxp") + " AMOUNT Deposits XP to your Account.");
        if (PlutusCore.perms.has(p, "Centralbank.command.withdrawxp") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.withdrawxp") + " AMOUNT Withdraws XP from your Account.");
        if (PlutusCore.perms.has(p, "Centralbank.command.transfer") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.transfer") + " PLAYER AMOUNT Transfers money to another Account.");
        if (PlutusCore.perms.has(p, "Centralbank.command.transferxp") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.transferxp") + " PLAYER AMOUNT Transfers XP to another Account.");
        if (PlutusCore.perms.has(p, "Centralbank.command.interesttimer") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.interesttimer") + " Shows the remaining time until the next wave of interests.");
        if (PlutusCore.perms.has(p, "Centralbank.command.exchange") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.exchange") + " AMOUNT Exchanges money to XP.");
        if (PlutusCore.perms.has(p, "Centralbank.command.exchangexp") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.exchangexp") + " AMOUNT Exchanges XP to money.");
        if (PlutusCore.perms.has(p, "Centralbank.command.rankstats") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.rankstats") + " Shows the richest players.");
        if (PlutusCore.perms.has(p, "Centralbank.command.rankstatsxp") || PlutusCore.perms.has(p, "Centralbank.command"))
            p.sendMessage("/bank " + coHa.getString("signAndCommand.rankstatsxp") + " Shows the players with the most experience banked.");
    }

    public void sendAdminHelp(Player p) {
        p.sendMessage("---Centralbank-AdminHelp---");
        p.sendMessage("/bankadmin help Shows the help page.");
        if (PlutusCore.perms.has(p, "Centralbank.command.set") || PlutusCore.perms.has(p, "Centralbank.command.admin"))
            p.sendMessage("/bankadmin " + coHa.getString("signAndCommand.admin.set") + " PLAYER AMOUNT Sets a players money.");
        if (PlutusCore.perms.has(p, "Centralbank.command.setxp") || PlutusCore.perms.has(p, "Centralbank.command.admin"))
            p.sendMessage("/bankadmin " + coHa.getString("signAndCommand.admin.setxp") + " PLAYER AMOUNT Sets a players XP.");
        if (PlutusCore.perms.has(p, "Centralbank.command.grant") || PlutusCore.perms.has(p, "Centralbank.command.admin"))
            p.sendMessage("/bankadmin " + coHa.getString("signAndCommand.admin.grant") + " PLAYER AMOUNT Grants a Player money.");
        if (PlutusCore.perms.has(p, "Centralbank.command.grantxp") || PlutusCore.perms.has(p, "Centralbank.command.admin"))
            p.sendMessage("/bankadmin " + coHa.getString("signAndCommand.admin.grantxp") + " PLAYER AMOUNT Grants a player XP.");
        if (PlutusCore.perms.has(p, "Centralbank.command.clear") || PlutusCore.perms.has(p, "Centralbank.command.admin"))
            p.sendMessage("/bankadmin " + coHa.getString("signAndCommand.admin.clear") + " PLAYER Clears money from a players Account.");
        if (PlutusCore.perms.has(p, "Centralbank.command.clearxp") || PlutusCore.perms.has(p, "Centralbank.command.admin"))
            p.sendMessage("/bankadmin " + coHa.getString("signAndCommand.admin.clearxp") + " PLAYER Clears XP from a players Account.");
        if (PlutusCore.perms.has(p, "Centralbank.command.databaseimport") || PlutusCore.perms.has(p, "Centralbank.command.admin"))
            p.sendMessage("/bankadmin " + coHa.getString("signAndCommand.admin.databaseimport") + " OLDDATA NEWDATA Moves data from one database type to another");
        if (PlutusCore.perms.has(p, "Centralbank.command.reloadconfig") || PlutusCore.perms.has(p, "Centralbank.command.admin"))
            p.sendMessage("/bankadmin " + coHa.getString("signAndCommand.admin.reloadconfig") + " Reloads the config of Centralbank.");

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
                        p.sendMessage("This server uses Centralbank " + plutusCore.getDescription().getVersion());
                        return true;
                    }

                    if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balance")) && (PlutusCore.perms.has(p, "Centralbank.command.balance") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                        return plutusCore.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
                    }
                    if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balancexp")) && (PlutusCore.perms.has(p, "Centralbank.command.balancexp") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                        return plutusCore.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
                    }

                    if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.interesttimer")) && (PlutusCore.perms.has(p, "Centralbank.command.interesttimer") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                        return plutusCore.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
                    }

                    if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.rankstats")) && (PlutusCore.perms.has(p, "Centralbank.command.rankstats") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                        return plutusCore.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
                    }

                    if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.rankstatsxp")) && (PlutusCore.perms.has(p, "Centralbank.command.rankstatsxp") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                        return plutusCore.getInteractionHandler().interact(vars[0], "", p, p.getUniqueId());
                    }
                }
                if (vars.length == 2) {
                    if (Util.isPositive(vars[1]) || vars[1].equalsIgnoreCase("all")) {
                        if (vars[0].equalsIgnoreCase("add") && (PlutusCore.perms.has(p, "Centralbank.admin"))) {
                            BlockIterator bi = new BlockIterator(p, 20);
                            Block sb = null;
                            while (bi.hasNext() && (sb = bi.next()).getType().equals(Material.AIR)) {
                            }

                            if (MaterialConstants.WalledSigns.contains(sb.getType())) {
                                Sign sign = (Sign) sb.getState();
                                if (sign.getLine(0).contains("[Bank]")) {
                                    Integer typsign = -1;
                                    try {
                                        typsign = plutusCore.getSignDatabaseInterface().getType(sb.getX(), sb.getY(), sb.getZ(), sb.getWorld());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    if (typsign == 1 || typsign == 2 || typsign == 3 || typsign == 4 || typsign == 6 || typsign == 7 || typsign == 8 || typsign == 9 || typsign == 12 || typsign == 13 || typsign == 14 || typsign == 15) {

                                        Integer x = sb.getX();
                                        Integer y = sb.getY();
                                        Integer z = sb.getZ();
                                        World w = sb.getWorld();

                                        Integer newType;
                                        Integer currentType = plutusCore.getSignDatabaseInterface().getType(x, y, z, w);

                                        if (currentType == 1 || currentType == 2 || currentType == 6 || currentType == 7 || currentType == 12 || currentType == 13) {
                                            newType = currentType + 2;
                                            plutusCore.getSignDatabaseInterface().changeType(x, y, z, newType, w);
                                        }


                                        plutusCore.getSignDatabaseInterface().addAmount(x, y, z, w, vars[1]);
                                        coHa.printMessage(p, "message.amountAddedSuccessfullyToSign", vars[1], p.getUniqueId());
                                        plutusCore.getSignHandler().updateSign(sb, 0);
                                        return true;
                                    }


                                }
                            }
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balance")) && (PlutusCore.perms.has(p, "Centralbank.command.balance.other") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], "", p, plutusCore.getUUIDHandler().getUUID(vars[1], p));
                                }
                            });
                            return true;
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balancexp")) && (PlutusCore.perms.has(p, "Centralbank.command.balancexp.other") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], "", p, plutusCore.getUUIDHandler().getUUID(vars[1], p));
                                }
                            });
                            return true;
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.deposit")) && (PlutusCore.perms.has(p, "Centralbank.command.deposit") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
                                }
                            });
                            return true;
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.withdraw")) && (PlutusCore.perms.has(p, "Centralbank.command.withdraw") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
                                }
                            });
                            return true;
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.depositxp")) && (PlutusCore.perms.has(p, "Centralbank.command.depositxp") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
                                }
                            });
                            return true;
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.withdrawxp")) && (PlutusCore.perms.has(p, "Centralbank.command.withdrawxp") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
                                }
                            });
                            return true;
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.exchange")) && (PlutusCore.perms.has(p, "Centralbank.command.exchange") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
                                }
                            });
                            return true;
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.exchangexp")) && (PlutusCore.perms.has(p, "Centralbank.command.exchangexp") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], vars[1], p, p.getUniqueId());
                                }
                            });
                            return true;
                        } else {
                            p.sendMessage(ChatColor.RED + coHa.getString("chat.prefix") + "Wrong Syntax or missing permissions! Please see /bank help for more information!");
                        }

                    } else {

                        if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balance")) && (PlutusCore.perms.has(p, "Centralbank.command.balance.other") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], null, p, plutusCore.getUUIDHandler().getUUID(vars[1], p));
                                }
                            });
                            return true;
                        } else if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.balancexp")) && (PlutusCore.perms.has(p, "Centralbank.command.balancexp.other") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getInteractionHandler().interact(vars[0], null, p, plutusCore.getUUIDHandler().getUUID(vars[1], p));
                                }
                            });
                            return true;
                        }
                    }


                }

                if (vars.length == 3) {
                    if (Util.isPositive(vars[2]) || vars[2].equalsIgnoreCase("all")) {
                        if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.transfer")) && (PlutusCore.perms.has(p, "Centralbank.command.transfer") || PlutusCore.perms.has(p, "Centralbank.command"))) {
                            final double amount;
                            if (vars[2].equalsIgnoreCase("all")) {
                                amount = plutusCore.getMoneyDatabaseInterface().getBalance(p.getUniqueId());
                            } else {
                                amount = Double.parseDouble(vars[2]);
                            }

                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    ((MoneyBankingHandler) plutusCore.getBankingHandlers()[0]).transferFromAccountToAccount(p.getUniqueId(), plutusCore.getUUIDHandler().getUUID(vars[1], p), amount, p);
                                }
                            });


                            return true;
                        } else {

                            if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.transferxp")) && (PlutusCore.perms.has(p, "Centralbank.command.transferxp") || PlutusCore.perms.has(p, "Centralbank.command"))) {

                                final int amount;
                                if (vars[2].equalsIgnoreCase("all")) {
                                    amount = plutusCore.getExperienceDatabaseInterface().getBalance(p.getUniqueId());
                                } else {
                                    amount = Integer.parseInt(vars[2]);
                                }

                                PlutusCore.execService.submit(new Runnable() {
                                    public void run() {
                                        ((ExperienceBankingHandler) plutusCore.getBankingHandlers()[1]).transferFromAccountToAccount(p.getUniqueId(), plutusCore.getUUIDHandler().getUUID(vars[1], p), amount, p);
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
                    } else if (vars.length == 1) {

                        if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.help"))) {
                            sendAdminHelp(p);
                            return true;
                        }

                        if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.reloadconfig")) && (PlutusCore.perms.has(p, "Centralbank.command.reloadconfig") || PlutusCore.perms.has(p, "Centralbank.command.admin"))) {
                            plutusCore.reloadConfig();
                            p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Config reloaded!");
                            return true;
                        }
                    } else if (vars.length == 2) {
                        if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.clear")) && (PlutusCore.perms.has(p, "Centralbank.command.clear") || PlutusCore.perms.has(p, "Centralbank.command.admin"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getMoneyDatabaseInterface().setBalance(plutusCore.getUUIDHandler().getUUID(vars[1], p), 0D);
                                    p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Account cleared!");
                                }
                            });
                            return true;
                        }
                        if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.clearxp")) && (PlutusCore.perms.has(p, "Centralbank.command.clearxp") || PlutusCore.perms.has(p, "Centralbank.command.admin"))) {
                            PlutusCore.execService.submit(new Runnable() {
                                public void run() {
                                    plutusCore.getExperienceDatabaseInterface().setBalance(plutusCore.getUUIDHandler().getUUID(vars[1], p), 0);
                                    p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "XP-Account cleared!");
                                }
                            });
                            return true;
                        }
                    } else if (vars.length == 3) {
                        if (Util.isDouble(vars[2])) {
                            if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.set")) && (PlutusCore.perms.has(p, "Centralbank.command.set") || PlutusCore.perms.has(p, "Centralbank.command.admin"))) {
                                PlutusCore.execService.submit(new Runnable() {
                                    public void run() {
                                        plutusCore.getMoneyDatabaseInterface().setBalance(plutusCore.getUUIDHandler().getUUID(vars[1], p), Double.parseDouble(vars[2]));
                                        p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Account set!");
                                    }
                                });
                                return true;
                            }

                            if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.setxp")) && (PlutusCore.perms.has(p, "Centralbank.command.setxp") || PlutusCore.perms.has(p, "Centralbank.command.admin"))) {
                                PlutusCore.execService.submit(new Runnable() {
                                    public void run() {
                                        plutusCore.getExperienceDatabaseInterface().setBalance(plutusCore.getUUIDHandler().getUUID(vars[1], p), Integer.parseInt(vars[2]));
                                        p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "XP-Account set!");
                                    }
                                });
                                return true;
                            }


                            if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.grant")) && (PlutusCore.perms.has(p, "Centralbank.command.grant") || PlutusCore.perms.has(p, "Centralbank.command.admin"))) {
                                PlutusCore.execService.submit(new Runnable() {
                                    public void run() {
                                        plutusCore.getMoneyDatabaseInterface().addToAccount(plutusCore.getUUIDHandler().getUUID(vars[1], p), Double.parseDouble(vars[2]));
                                        p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Granted " + vars[2] + " Money to " + vars[1] + "!");
                                    }
                                });
                                return true;
                            }

                            if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.grantxp")) && (PlutusCore.perms.has(p, "Centralbank.command.grantxp") || PlutusCore.perms.has(p, "Centralbank.command.admin"))) {
                                PlutusCore.execService.submit(new Runnable() {
                                    public void run() {
                                        plutusCore.getExperienceDatabaseInterface().addToAccount(plutusCore.getUUIDHandler().getUUID(vars[1], p), Integer.parseInt(vars[2]));
                                        p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Granted " + vars[2] + " Experience to " + vars[1] + "!");
                                    }
                                });
                                return true;
                            }
                        } else {
                            if (vars[0].equalsIgnoreCase(coHa.getString("signAndCommand.admin.databaseimport")) && (PlutusCore.perms.has(p, "Centralbank.command.databaseimport") || PlutusCore.perms.has(p, "Centralbank.command.admin"))) {

                                p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Importing...");

                                DatabaseManagerInterface loadDataMan = null;
                                AccountDatabaseInterface<Double> loadDataMoney = null;
                                AccountDatabaseInterface<Integer> loadDataXp = null;
                                SignDatabaseInterface loadDataSign = null;

                                DatabaseManagerInterface saveDataMan = null;
                                AccountDatabaseInterface<Double> saveDataMoney = null;
                                AccountDatabaseInterface<Integer> saveDataXp = null;
                                SignDatabaseInterface saveDataSign = null;


                                if (vars[1].equalsIgnoreCase("flatfile")) {
                                    //Load flatFile
                                    p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Importing from flatfile...");
                                    loadDataMan = new DatabaseManagerFlatFile(plutusCore);
                                    loadDataMoney = new MoneyFlatFileInterface(plutusCore);
                                    loadDataXp = new ExperienceFlatFileInterface(plutusCore);
                                    loadDataSign = new SignFlatFileInterface(plutusCore);
                                }

                                if (vars[1].equalsIgnoreCase("mysql")) {
                                    //Load mysql
                                    p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Importing from mysql...");
                                    loadDataMan = new DatabaseManagerMysql(plutusCore);
                                    loadDataMoney = new MoneyMysqlInterface(plutusCore);
                                    loadDataXp = new ExperienceMysqlInterface(plutusCore);
                                    loadDataSign = new SignMysqlInterface(plutusCore);
                                }

                                if (vars[2].equalsIgnoreCase("flatfile")) {
                                    //Load flatFile
                                    p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Exporting to flatfile...");
                                    saveDataMan = new DatabaseManagerFlatFile(plutusCore);
                                    saveDataMoney = new MoneyFlatFileInterface(plutusCore);
                                    saveDataXp = new ExperienceFlatFileInterface(plutusCore);
                                    saveDataSign = new SignFlatFileInterface(plutusCore);
                                }

                                if (vars[2].equalsIgnoreCase("mysql")) {
                                    //Load mysql
                                    p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Exporting to mysql...");
                                    saveDataMan = new DatabaseManagerMysql(plutusCore);
                                    saveDataMoney = new MoneyMysqlInterface(plutusCore);
                                    saveDataXp = new ExperienceMysqlInterface(plutusCore);
                                    saveDataSign = new SignMysqlInterface(plutusCore);
                                }

                                //get them ready
                                loadDataMan.setupDatabase();
                                saveDataMan.setupDatabase();

                                //move money data
                                for (UUID uuid : loadDataMoney.getAccounts()) {
                                    saveDataMoney.setBalance(uuid, loadDataMoney.getBalance(uuid));
                                }

                                //move xp data
                                for (UUID uuid : loadDataXp.getAccounts()) {
                                    saveDataXp.setBalance(uuid, loadDataXp.getBalance(uuid));
                                }

                                //move sign data
                                String amounts;
                                String[] amountsArray;
                                int type;
                                for (Location location : loadDataSign.getLocations(-1, null)) {
                                    //Get amounts
                                    amountsArray = loadDataSign.getAmounts((int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getWorld());
                                    amounts = amountsArray[0];
                                    for (int i = 1; i < amountsArray.length; i++) {
                                        amounts += ":" + amountsArray[i];
                                    }

                                    //Get type
                                    type = loadDataSign.getType((int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getWorld());

                                    //Create new sign in save database
                                    saveDataSign.createNewSign((int) location.getX(), (int) location.getY(), (int) location.getZ(), location.getWorld(), type, amounts);
                                }

                                //close databases
                                loadDataMan.closeDatabase();
                                saveDataMan.closeDatabase();

                                //Send success message
                                p.sendMessage(coHa.getString("chat.color") + coHa.getString("chat.prefix") + "Moved all data from " + vars[1] + " to " + vars[2] + "!");
                                return true;
                            }

                        }

                    } else {
                        p.sendMessage(ChatColor.RED + coHa.getString("chat.prefix") + "Wrong Syntax or missing permissions! Please see /bank help for more information!");
                    }
                    return true;
                }
            }
        } else {
            PlutusCore.log.info("[Centralbank] Please use this ingame!");
        }

        return false;
    }

}
