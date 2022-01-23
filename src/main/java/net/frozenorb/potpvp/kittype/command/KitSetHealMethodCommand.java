package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetHealMethodCommand {
  @Command(names = "kittype setHealingMethod", permission = "op", description = "Sets a kit's healing method.")
  public static void kitLoadDefault(Player sender,
      @Param(name="kit type") KitType kitType,
      @Param(name="healing method") HealingMethod healMethod) {

    kitType.setHealingMethod(healMethod);
    sender.sendMessage(ChatColor.YELLOW + "Set healing method to " + healMethod + " for kit " + kitType + ".");
  }
}