package net.frozenorb.potpvp.rematch.listener;

import net.frozenorb.potpvp.PotPvPND;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class RematchUnloadListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PotPvPND.getInstance().getRematchHandler().unloadRematchData(event.getPlayer());
    }

}