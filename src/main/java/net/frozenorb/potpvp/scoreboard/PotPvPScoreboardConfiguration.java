package net.frozenorb.potpvp.scoreboard;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.scoreboard.ScoreboardConfiguration;
import net.frozenorb.potpvp.kt.scoreboard.TitleGetter;
import net.frozenorb.potpvp.util.CC;
import org.bukkit.ChatColor;

public final class PotPvPScoreboardConfiguration {

    public static ScoreboardConfiguration create() {
        return new ScoreboardConfiguration(
                TitleGetter.forStaticString(CC.translate(PotPvPND.getInstance().getMainConfig().getString("Practice.Scoreboard-Header"))),
                new MultiplexingScoreGetter(
                        new MatchScoreGetter(),
                        new LobbyScoreGetter(),
                        new GameScoreGetter()
                )
        );
    }

}
