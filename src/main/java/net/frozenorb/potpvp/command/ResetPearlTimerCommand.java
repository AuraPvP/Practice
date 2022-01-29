package net.frozenorb.potpvp.command;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchTeam;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ResetPearlTimerCommand {

  @Command(names = "resetpearl", permission = "")
  public static void resetpearl(Player sender, @Param(name = "target", defaultValue = "self") Player target) {
    PotPvPND.getInstance().getPearlCooldownListener().clearCooldown(sender);
  }
}
