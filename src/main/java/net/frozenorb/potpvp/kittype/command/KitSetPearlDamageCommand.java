package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetPearlDamageCommand {
  @Command(names = "kittype setPearlDamage", permission = "op", description = "Sets a kit's pearl damage value")
  public static void kitLoadDefault(Player sender,
      @Param(name="kit type") KitType kitType,
      @Param(name="pearl damage") boolean pearlDamage) {

    kitType.setPearlDamage(pearlDamage);
    sender.sendMessage(ChatColor.YELLOW + "Set pearl damage to " + pearlDamage + " for kit " + kitType + ".");
  }
}