package com.nixholas.plutus.tasks;

import com.nixholas.plutus.PlutusCore;
import com.nixholas.plutus.Util;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.TimerTask;

public class PlayerPositionTask extends TimerTask {

    private final PlutusCore plutusCore;

    public PlayerPositionTask(PlutusCore plutusCore) {
        this.plutusCore = plutusCore;
    }

    @Override
    public void run() {

        int maxRadius = Integer.parseInt(plutusCore.getConfigurationHandler().getString("general.maximumRange"));
        for (World w : plutusCore.getServer().getWorlds()) {
            Sign[] signs = plutusCore.getSignHandler().getSigns(w, 17);

            playerLoop:
            for (Player p : plutusCore.getServer().getOnlinePlayers()) {
                if (p.getWorld().equals(w)) {
                    for (Sign s : signs) {
                        if (Util.isInRange(p.getLocation(), s.getLocation(), maxRadius)) {
                            continue playerLoop;
                        }
                    }
                    plutusCore.getInteractionHandler().stopChatInteract(p);
                }
            }
        }
    }

}
