
package net.frozenorb.potpvp.scoreboard;

import java.util.Date;
import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.elo.EloHandler;
import net.frozenorb.potpvp.kt.util.TimeUtils;
import net.frozenorb.potpvp.lobby.listener.LobbyParkourListener;
import net.frozenorb.potpvp.lobby.listener.LobbyParkourListener.Parkour;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.party.PartyHandler;
import net.frozenorb.potpvp.queue.MatchQueue;
import net.frozenorb.potpvp.queue.MatchQueueEntry;
import net.frozenorb.potpvp.queue.QueueHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.function.BiConsumer;

final class LobbyScoreGetter implements BiConsumer<Player, LinkedList<String>> {

    @Override
    public void accept(Player player, LinkedList<String> scores) {
        PartyHandler partyHandler = PotPvPND.getInstance().getPartyHandler();
        EloHandler eloHandler = PotPvPND.getInstance().getEloHandler();

        scores.add("&cOnline" + ChatColor.GRAY + ": " + "&f" + PotPvPND.getInstance().getCache().getOnlineCount());
        scores.add("&cFighting" + ChatColor.GRAY + ": " + "&f" + PotPvPND.getInstance().getCache().getFightsCount());
        scores.add("&cQueueing" + ChatColor.GRAY + ": " + "&f" + PotPvPND.getInstance().getCache().getQueuesCount());

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            int size = playerParty.getMembers().size();
            String leader = Bukkit.getPlayer(playerParty.getLeader()).getName();
            scores.add("");
            scores.add("&aYour Team&7: &f" + size + " &7(" + leader + "&7)");

        }

        final MatchQueueEntry entry=this.getQueueEntry(player);
        if (entry != null) {
            final String waitTimeFormatted=TimeUtils.formatIntoMMSS(entry.getWaitSeconds());
            final MatchQueue queue=entry.getQueue();
            scores.add("&7");
            scores.add("&f" + (queue.isRanked() ? "&4Ranked Queue" : "&4Unranked Queue"));
            scores.add(" &cLadder: &f" + queue.getKitType().getDisplayName());
            scores.add(" &cWaiting: &f" + waitTimeFormatted);
            if (queue.isRanked()) {
                final int elo=eloHandler.getElo(entry.getMembers(), queue.getKitType());
                final int window=entry.getWaitSeconds() * 5;
                scores.add(" &cRange: &f" + Math.max(0, elo - window) + " &4➜ &f" + (elo + window));
            }
        }

        Parkour parkour = LobbyParkourListener.parkourMap.get(player.getUniqueId());
        if (parkour != null) {
            long elapsedParkourTime = System.currentTimeMillis() - parkour.getTimeStarted();
            scores.add("&7");
            scores.add("&4Parkour:");
            scores.add(" &cElapsed: &f" + TimeUtils.formatLongIntoMMSS(elapsedParkourTime/1000));
        }

    }

    private MatchQueueEntry getQueueEntry(Player player) {
        PartyHandler partyHandler = PotPvPND.getInstance().getPartyHandler();
        QueueHandler queueHandler = PotPvPND.getInstance().getQueueHandler();

        Party playerParty = partyHandler.getParty(player);
        if (playerParty != null) {
            return queueHandler.getQueueEntry(playerParty);
        } else {
            return queueHandler.getQueueEntry(player.getUniqueId());
        }
    }

}