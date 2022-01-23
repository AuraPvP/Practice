package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetHardcoreHealingCommand {
  @Command(names = "kittype setHardcoreHealing", permission = "op", description = "Sets a kit's hardcore healing value")
  public static void kitLoadDefault(Player sender,
      @Param(name="kit type") KitType kitType,
      @Param(name="hardcore healing") boolean hardcoreHealing) {

    kitType.setHardcoreHealing(hardcoreHealing);
    sender.sendMessage(ChatColor.YELLOW + "Set hardcore healing to " + hardcoreHealing + " for kit " + kitType + ".");
  }
}