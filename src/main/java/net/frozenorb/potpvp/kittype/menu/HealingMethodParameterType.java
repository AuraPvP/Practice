package net.frozenorb.potpvp.kittype.menu;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.kt.command.data.parameter.ParameterType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HealingMethodParameterType implements ParameterType<HealingMethod> {

  @Override
  public HealingMethod transform(@NotNull CommandSender sender, @NotNull String source) {
    return HealingMethod.valueOf(source);
  }

  @NotNull
  @Override
  public List<String> tabComplete(@NotNull Player player, @NotNull Set<String> flags,
      @NotNull String source) {
    List<String> completions = new ArrayList<>();

    for(HealingMethod method : HealingMethod.values()) {
      if(!player.isOp())
        continue;

      if(StringUtils.startsWithIgnoreCase(method.name(), source)) {
        completions.add(method.name());
      }
    }

    return completions;
  }
}
