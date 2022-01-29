package net.frozenorb.potpvp.scoreboard;

import java.util.List;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.scoreboard.ScoreboardConfiguration;
import net.frozenorb.potpvp.kt.scoreboard.TitleGetter;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PotPvPScoreboardConfiguration {


  /* fucking qlib, titlegetter should totally be an interface */
  public static class AnimatedTitleGetter extends TitleGetter {
    int index;
    int frequency, counter;
    List<String> animation;

    public AnimatedTitleGetter(int frequency, List<String> animation) {
      super("fuck you");

      this.index = 0;
      this.frequency = frequency;
      this.counter = 0;
      this.animation = animation;
    }

    @NotNull
    public String getTitle(Player player) {
      counter = (counter + 1) % frequency;
      if(counter == 0)
        index = (index + 1) % animation.size();
      return animation.get(index);
    }
  }

  public static ScoreboardConfiguration create() {
    return new ScoreboardConfiguration(
        new AnimatedTitleGetter(3, PotPvPND.getInstance().getMainConfig().getStringList("Practice.Scoreboard-Animations")),
        new MultiplexingScoreGetter(
            new MatchScoreGetter(),
            new LobbyScoreGetter(),
            new GameScoreGetter()
        )
    );
  }
}
