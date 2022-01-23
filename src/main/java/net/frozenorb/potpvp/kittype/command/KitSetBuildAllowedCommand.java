package net.frozenorb.potpvp.kittype.command;

import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kt.command.Command;
import net.frozenorb.potpvp.kt.command.data.parameter.Param;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitSetBuildAllowedCommand {
  @Command(names = "kittype setBuildAllowed", permission = "op", description = "Sets a kit's build type")
  public static void kitLoadDefault(Player sender,
      @Param(name="kit type") KitType kitType,
      @Param(name="build allowed") boolean buildAllowed) {

    kitType.setBuildingAllowed(buildAllowed);
    sender.sendMessage(ChatColor.YELLOW + "Set build allowed to " + buildAllowed + " for kit " + kitType + ".");
  }
}