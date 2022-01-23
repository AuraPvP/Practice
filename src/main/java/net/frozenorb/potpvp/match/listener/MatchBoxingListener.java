package net.frozenorb.potpvp.match.listener;

import java.util.HashMap;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.event.MatchEndEvent;
import net.frozenorb.potpvp.match.event.MatchStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spigotmc.SpigotConfig;

import java.util.Objects;
import java.util.UUID;

public class MatchBoxingListener implements Listener {

  @Getter
  static HashMap<UUID, Integer> hitMap = new HashMap();

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onDamage(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();
      Player attacker = (Player) event.getDamager();
      Match match = PotPvPND.getInstance().getMatchHandler().getMatchPlaying(attacker);
      if (match.getKitType().getId().contains("Boxing")) {
        hitMap.put(attacker.getUniqueId(), hitMap.getOrDefault(attacker.getUniqueId(), 0) + 1);
        if(hitMap.get(attacker.getUniqueId()) >= 100) {
          player.setHealth(0);
          hitMap.remove(player.getUniqueId());
          hitMap.remove(attacker.getUniqueId());
        }
        event.setDamage(0.0);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onMatchEnd(MatchEndEvent event) {
    Match match = event.getMatch();

    if(match.getKitType().getId().contains("Boxing")) {
      for(UUID uuid : match.getAllPlayers()) {
       hitMap.remove(uuid);
      }
    }
  }
}
