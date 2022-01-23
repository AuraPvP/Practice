package net.frozenorb.potpvp.queue.listener;

import com.google.common.collect.ImmutableList;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kittype.KitType;
import net.frozenorb.potpvp.kittype.menu.select.CustomSelectKitTypeMenu;
import net.frozenorb.potpvp.listener.RankedMatchQualificationListener;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.party.Party;
import net.frozenorb.potpvp.queue.QueueHandler;
import net.frozenorb.potpvp.queue.QueueItems;
import net.frozenorb.potpvp.util.CC;
import net.frozenorb.potpvp.util.ItemListener;
import net.frozenorb.potpvp.validation.PotPvPValidation;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.md_5.bungee.api.ChatColor.BOLD;

// This class followes a different organizational style from other item listeners
// because we need seperate listeners for ranked/unranked, we have methods which
// we call which generate a Consumer<Player> designed for either ranked/unranked,
// based on the argument passed. Returning Consumers makes this code slightly
// harder to follow, but saves us from a lot of duplication
public final class QueueItemListener extends ItemListener {

    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionRanked = selectionMenuAddition(true);
    private final Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionAdditionUnranked = selectionMenuAddition(false);
    private final QueueHandler queueHandler;

    public QueueItemListener(QueueHandler queueHandler) {
        this.queueHandler = queueHandler;

        addHandler(QueueItems.JOIN_SOLO_UNRANKED_QUEUE_ITEM, joinSoloConsumer(false));
        addHandler(QueueItems.JOIN_SOLO_RANKED_QUEUE_ITEM, joinSoloConsumer(true));

        addHandler(QueueItems.JOIN_PARTY_UNRANKED_QUEUE_ITEM, joinPartyConsumer(false));
        addHandler(QueueItems.JOIN_PARTY_RANKED_QUEUE_ITEM, joinPartyConsumer(true));

        addHandler(QueueItems.LEAVE_SOLO_UNRANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));
        addHandler(QueueItems.LEAVE_SOLO_RANKED_QUEUE_ITEM, p -> queueHandler.leaveQueue(p, false));

        Consumer<Player> leaveQueuePartyConsumer = player -> {
            Party party = PotPvPND.getInstance().getPartyHandler().getParty(player);

            // don't message, players who aren't leader shouldn't even get this item
            if (party != null && party.isLeader(player.getUniqueId())) {
                queueHandler.leaveQueue(party, false);
            }
        };

        addHandler(QueueItems.LEAVE_PARTY_UNRANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
        addHandler(QueueItems.LEAVE_PARTY_RANKED_QUEUE_ITEM, leaveQueuePartyConsumer);
    }

    private Consumer<Player> joinSoloConsumer(boolean ranked) {
        return player -> {
            if (ranked) {
                if (!RankedMatchQualificationListener.isQualified(player.getUniqueId())) {
                    int needed = RankedMatchQualificationListener.getWinsNeededToQualify(player.getUniqueId());
                    player.sendMessage(ChatColor.RED + "You need " + RankedMatchQualificationListener.MIN_MATCH_WINS + " unranked 1v1 wins to join ranked, you need " + needed + " more wins");
                    return;
                }
            }

            if (PotPvPValidation.canJoinQueue(player)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(player, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked,
                    ChatColor.DARK_GRAY + "Join " + (ranked ? "Ranked" : "Unranked") + " Queue", ranked).openMenu(player);
            }
        };
    }

    private Consumer<Player> joinPartyConsumer(boolean ranked) {
        return player -> {
            Party party = PotPvPND.getInstance().getPartyHandler().getParty(player);

            // just fail silently, players who aren't a leader
            // of a party shouldn't even have this item
            if (party == null || !party.isLeader(player.getUniqueId())) {
                return;
            }

            if (ranked) {
                for (UUID member : party.getMembers()) {
                    if (!RankedMatchQualificationListener.isQualified(member)) {
                        int needed = RankedMatchQualificationListener.getWinsNeededToQualify(member);
                        player.sendMessage(ChatColor.RED + "Your party can't join ranked queues because " + PotPvPND.getInstance().getUuidCache().name(member) + " has less than " + RankedMatchQualificationListener.MIN_MATCH_WINS + " unranked 1v1 wins. They need " + needed + " more wins");
                        return;
                    }
                }
            }

            // try to check validation issues in advance
            // (will be called again in QueueHandler#joinQueue)
            if (PotPvPValidation.canJoinQueue(party)) {
                new CustomSelectKitTypeMenu(kitType -> {
                    queueHandler.joinQueue(party, kitType, ranked);
                    player.closeInventory();
                }, ranked ? selectionAdditionRanked : selectionAdditionUnranked, "Play " + (ranked ? "Ranked" : "Unranked"), ranked).openMenu(player);
            }
        };
    }

    private Function<KitType, CustomSelectKitTypeMenu.CustomKitTypeMeta> selectionMenuAddition(boolean ranked) {
        return kitType -> {
            MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();

            int inFightsRanked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && m.isRanked());
            int inQueueRanked = queueHandler.countPlayersQueued(kitType, true);

            int inFightsUnranked = matchHandler.countPlayersPlayingMatches(m -> m.getKitType() == kitType && !m.isRanked());
            int inQueueUnranked = queueHandler.countPlayersQueued(kitType, false);

            return new CustomSelectKitTypeMenu.CustomKitTypeMeta(
                // clamp value to >= 1 && <= 64
                Math.max(1, Math.min(64, ranked ? inQueueRanked + inFightsRanked : inQueueUnranked + inFightsUnranked)),
                ranked ?
                ImmutableList.of(
                        ChatColor.WHITE + " ", ChatColor.DARK_RED + "" + org.bukkit.ChatColor.BOLD + "Ranked",
                        ChatColor.WHITE + "&c▪ &fQueued: " + ChatColor.DARK_RED + inFightsRanked,
                        ChatColor.WHITE + "&c▪ &fFighting: " + ChatColor.DARK_RED + inQueueRanked
                ) :
                ImmutableList.of(
                        ChatColor.WHITE + " ", ChatColor.DARK_RED + "" + org.bukkit.ChatColor.BOLD + "Unranked",
                        ChatColor.WHITE + "&c▪ &fQueued: " + ChatColor.DARK_RED + inQueueUnranked,
                        ChatColor.WHITE + "&c▪ &fFighting: " + ChatColor.DARK_RED + inFightsUnranked

                )
            );
        };
    }

}