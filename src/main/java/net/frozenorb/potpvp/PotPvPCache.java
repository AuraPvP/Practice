package net.frozenorb.potpvp;

import lombok.Getter;
import org.bukkit.Bukkit;

@Getter
public class PotPvPCache implements Runnable {

    private int onlineCount = 0;
    private int fightsCount = 0;
    private int queuesCount = 0;

    @Override
    public void run() {
        onlineCount = Bukkit.getOnlinePlayers().size();
        fightsCount = PotPvPND.getInstance().getMatchHandler().countPlayersPlayingInProgressMatches();
        queuesCount = PotPvPND.getInstance().getQueueHandler().getQueuedCount();
    }

}
