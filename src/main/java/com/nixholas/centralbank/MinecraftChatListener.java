package com.nixholas.centralbank;
//All of the imports.

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class MinecraftChatListener implements Listener {
    private final CentralBank centralBank;

    private final HashMap<Player, String> chosenInteraction = new HashMap<Player, String>();

    public MinecraftChatListener(CentralBank centralBank) {
        this.centralBank = centralBank;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {


        if (!((centralBank.getInteractionHandler().getChatSignMap().get(event.getPlayer()) == null) || centralBank.getInteractionHandler().getChatSignMap().get(event.getPlayer()) == 0)) {

            if (event.getMessage().toLowerCase().contains(centralBank.getConfigurationHandler().getString("chat.quit"))) {
                centralBank.getInteractionHandler().getChatSignMap().put(event.getPlayer(), 0);
                centralBank.getConfigurationHandler().printMessage(event.getPlayer(), "message.youHaveQuit", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                event.setCancelled(true);
                return;
            }

            //1 = deposit, xpdeposit, withdraw etc.
            if (centralBank.getInteractionHandler().getChatSignMap().get(event.getPlayer()) == 1) {
                if (centralBank.getInteractionHandler().getTypeMap().containsKey(event.getMessage())) {
                    chosenInteraction.put(event.getPlayer(), event.getMessage());
                    centralBank.getInteractionHandler().getChatSignMap().put(event.getPlayer(), 2);
                    centralBank.getConfigurationHandler().printMessage(event.getPlayer(), "message.specifyAnAmount", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                    event.setCancelled(true);
                } else {
                    //We will add an error message here, if it is not a correct interaction.
                    centralBank.getConfigurationHandler().printMessage(event.getPlayer(), "message.nonExistingInteraction", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                    event.setCancelled(true);
                }
            } else
                //Waiting for player to input a value.
                if (centralBank.getInteractionHandler().getChatSignMap().get(event.getPlayer()) == 2) {
                    if (event.getMessage().equalsIgnoreCase("all") || Util.isPositive(event.getMessage())) {

                        //Start interaction
                        if (!centralBank.getInteractionHandler().interact(chosenInteraction.get(event.getPlayer()), event.getMessage(), event.getPlayer(), event.getPlayer().getUniqueId())) {
                            centralBank.getConfigurationHandler().printMessage(event.getPlayer(), "message.specifyAnAmount", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                        } else {
                            //Reset interact
                            centralBank.getInteractionHandler().getChatSignMap().put(event.getPlayer(), 0);
                        }


                        event.setCancelled(true);
                    } else {
                        //Send error message
                        centralBank.getConfigurationHandler().printMessage(event.getPlayer(), "message.wrongAmountSyntax", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                        event.setCancelled(true);
                    }
                }
        }
    }
}
