package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetRankedStateCommand {
  @Command(names = "kittype setRanked", permission = "op", description = "Sets a kit's ranked value")
  public static void kitLoadDefault(Player sender,
      @Param(name="kit type") KitType kitType,
      @Param(name="ranked") boolean isRanked) {

    kitType.setSupportsRanked(isRanked);
    sender.sendMessage(ChatColor.YELLOW + "Set ranked value to " + isRanked + " for kit " + kitType + ".");
  }
}