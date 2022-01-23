package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetHeartsShownCommand {
  @Command(names = "kittype setHeartsShown", permission = "op", description = "Sets a kit's hearts shown")
  public static void kitLoadDefault(Player sender,
      @Param(name="kit type") KitType kitType,
      @Param(name="hearts shown") boolean heartsShown) {

    kitType.setHealthShown(heartsShown);
    sender.sendMessage(ChatColor.YELLOW + "Set hearts shown to " + heartsShown + " for kit " + kitType + ".");
  }
}