package net.frozenorb.potpvp.match.listener;

import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchEndEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class MatchPearlFightListener implements Listener {

    @Getter
    static HashMap<UUID, Integer> livesMap = new HashMap();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDeath(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
        Player player = (Player) event.getEntity();
        Match match = PotPvPND.getInstance().getMatchHandler().getMatchPlaying(player);
        if (match.getKitType().getId().contains("PearlFight")) {
          event.setDamage(0);
          if(event.getCause() == DamageCause.VOID) {
            livesMap.put(player.getUniqueId(), livesMap.getOrDefault(player.getUniqueId(), 3) - 1);
            if(livesMap.get(player.getUniqueId()) == 0) {
              event.setDamage(20);
              return;
            }

            MatchTeam matchTeam = match.getTeam(player.getUniqueId());
            int team = match.getTeams().indexOf(matchTeam);
            if(team == 0) {
              player.teleport(match.getArena().getTeam1Spawn());
            } else {
              player.teleport(match.getArena().getTeam2Spawn());
            }
          }
        }
      }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMatchEnd(MatchEndEvent event) {
      Match match = event.getMatch();

      if(match.getKitType().getId().contains("PearlFight")) {
        for(UUID uuid : match.getAllPlayers()) {
          livesMap.remove(uuid);
        }
      }
    }


    public static String getLivesIcon(int lives) {
      if(lives == 1) {
        return ChatColor.GREEN + "❤" + ChatColor.GRAY + "❤❤";
      } else if(lives == 2) {
        return ChatColor.GREEN + "❤❤" + ChatColor.GRAY + "❤";
      } else if(lives == 3) {
        return ChatColor.GREEN + "❤❤❤";
      } else {
        return ChatColor.GRAY + "❤❤❤";
      }
    }
  }

