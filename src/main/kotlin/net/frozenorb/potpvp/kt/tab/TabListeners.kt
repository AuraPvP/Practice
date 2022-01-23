package net.frozenorb.potpvp.kt.tab

import net.frozenorb.potpvp.PotPvPND
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.EventHandler
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.event.player.PlayerJoinEvent

class TabListeners : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        object : BukkitRunnable() {
            override fun run() {
                PotPvPND.getInstance().tabEngine.addPlayer(event.player)
            }
        }.runTaskLater(PotPvPND.getInstance(), 10L)
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent) {
        PotPvPND.getInstance().tabEngine.removePlayer(event.player)
        TabLayout.remove(event.player)
    }

}