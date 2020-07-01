package com.nixholas.plutus;
//All of the imports.

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class MinecraftChatListener implements Listener {
    private final PlutusCore plutusCore;

    private final HashMap<Player, String> chosenInteraction = new HashMap<Player, String>();

    public MinecraftChatListener(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {


        if (!((plutusCore.getInteractionHandler().getChatSignMap().get(event.getPlayer()) == null) || plutusCore.getInteractionHandler().getChatSignMap().get(event.getPlayer()) == 0)) {

            if (event.getMessage().toLowerCase().contains(plutusCore.getConfigurationHandler().getString("chat.quit"))) {
                plutusCore.getInteractionHandler().getChatSignMap().put(event.getPlayer(), 0);
                plutusCore.getConfigurationHandler().printMessage(event.getPlayer(), "message.youHaveQuit", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                event.setCancelled(true);
                return;
            }

            //1 = deposit, xpdeposit, withdraw etc.
            if (plutusCore.getInteractionHandler().getChatSignMap().get(event.getPlayer()) == 1) {
                if (plutusCore.getInteractionHandler().getTypeMap().containsKey(event.getMessage())) {
                    chosenInteraction.put(event.getPlayer(), event.getMessage());
                    plutusCore.getInteractionHandler().getChatSignMap().put(event.getPlayer(), 2);
                    plutusCore.getConfigurationHandler().printMessage(event.getPlayer(), "message.specifyAnAmount", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                    event.setCancelled(true);
                } else {
                    //We will add an error message here, if it is not a correct interaction.
                    plutusCore.getConfigurationHandler().printMessage(event.getPlayer(), "message.nonExistingInteraction", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                    event.setCancelled(true);
                }
            } else
                //Waiting for player to input a value.
                if (plutusCore.getInteractionHandler().getChatSignMap().get(event.getPlayer()) == 2) {
                    if (event.getMessage().equalsIgnoreCase("all") || Util.isPositive(event.getMessage())) {

                        //Start interaction
                        if (!plutusCore.getInteractionHandler().interact(chosenInteraction.get(event.getPlayer()), event.getMessage(), event.getPlayer(), event.getPlayer().getUniqueId())) {
                            plutusCore.getConfigurationHandler().printMessage(event.getPlayer(), "message.specifyAnAmount", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                        } else {
                            //Reset interact
                            plutusCore.getInteractionHandler().getChatSignMap().put(event.getPlayer(), 0);
                        }


                        event.setCancelled(true);
                    } else {
                        //Send error message
                        plutusCore.getConfigurationHandler().printMessage(event.getPlayer(), "message.wrongAmountSyntax", "", event.getPlayer().getUniqueId(), event.getPlayer().getName());
                        event.setCancelled(true);
                    }
                }
        }
    }
}
