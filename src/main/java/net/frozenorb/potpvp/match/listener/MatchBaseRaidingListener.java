package net.frozenorb.potpvp.match.listener;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.TitleType;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.match.event.MatchCountdownStartEvent;
import net.frozenorb.potpvp.match.event.MatchEndEvent;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MatchBaseRaidingListener implements Listener {

  @EventHandler
  public void onMatchStart(MatchCountdownStartEvent event) {
    if(event.getMatch().getKitType().getId().equals("BaseRaiding")) {
      int random = ThreadLocalRandom.current().nextInt(0, 2);
      int other = random == 0 ? 1 : 0;

      Player trapper = Bukkit.getPlayer(event.getMatch().getTeams().get(random).getFirstMember());
      Player raider = Bukkit.getPlayer(event.getMatch().getTeams().get(other).getFirstMember());

      trapper.sendMessage(CC.translate("&a&lTRAPPER &fYou are trapping"));
      raider.sendMessage(CC.translate("&c&lRAIDER &fYou are raiding"));

      trapper.setMetadata("trapper", new FixedMetadataValue(PotPvPND.getInstance(), true));
      LunarClientAPI.getInstance().sendTitle(trapper, TitleType.TITLE, ChatColor.GREEN
          + "" + ChatColor.BOLD + "TRAPPER " + ChatColor.WHITE + "You are trapping.", Duration.ofSeconds(3L));


      raider.setMetadata("raider", new FixedMetadataValue(PotPvPND.getInstance(), true));
      LunarClientAPI.getInstance().sendTitle(raider, TitleType.TITLE, ChatColor.RED
          + "" + ChatColor.BOLD + "RAIDER " + ChatColor.WHITE + "You are raiding.", Duration.ofSeconds(3L));
    }
  }

  @EventHandler
  public void onMatchEnd(MatchEndEvent event) {
    if(event.getMatch().getKitType().getId().equals("BaseRaiding")) {
      for(UUID uuid : event.getMatch().getAllPlayers()) {
        Bukkit.getPlayer(uuid).removeMetadata("trapper", PotPvPND.getInstance());
        Bukkit.getPlayer(uuid).removeMetadata("raider", PotPvPND.getInstance());
      }
    }
  }

  @EventHandler
  public void playerDeathEvent(PlayerDeathEvent event) {
    Match match = PotPvPND.getInstance().getMatchHandler().getMatchPlaying(event.getEntity());
    if(match.getKitType().getId().equals("BaseRaiding")) {
      for(UUID uuid : match.getAllPlayers()) {
        Bukkit.getPlayer(uuid).removeMetadata("trapper", PotPvPND.getInstance());
        Bukkit.getPlayer(uuid).removeMetadata("raider", PotPvPND.getInstance());
      }
    }
  }

  @EventHandler
  public void playerLeaveEvent(PlayerQuitEvent event) {
    event.getPlayer().removeMetadata("trapper", PotPvPND.getInstance());
    event.getPlayer().removeMetadata("raider", PotPvPND.getInstance());
    }
  }
