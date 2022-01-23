package net.frozenorb.potpvp.scoreboard;

import com.qrakn.morpheus.game.event.impl.skywars.SkywarsGameEventTypeParameter.PotPvP;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.BiConsumer;

import net.frozenorb.potpvp.PotPvPND;
import net.frozenorb.potpvp.kt.scoreboard.ScoreFunction;
import net.frozenorb.potpvp.kt.util.PlayerUtils;
import net.frozenorb.potpvp.kt.util.TimeUtils;
import net.frozenorb.potpvp.match.listener.MatchBoxingListener;
import net.frozenorb.potpvp.match.listener.MatchPearlFightListener;
import net.frozenorb.potpvp.pvpclasses.pvpclasses.ArcherClass;
import net.frozenorb.potpvp.pvpclasses.pvpclasses.BardClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;

import net.frozenorb.potpvp.kittype.HealingMethod;
import net.frozenorb.potpvp.match.Match;
import net.frozenorb.potpvp.match.MatchHandler;
import net.frozenorb.potpvp.match.MatchState;
import net.frozenorb.potpvp.match.MatchTeam;

// the list here must be viewed as rendered javadoc to make sense. In IntelliJ, click on
// 'MatchScoreGetter' and press Control+Q
/**
 * Implements the scoreboard as defined in {@link net.frozenorb.potpvp.scoreboard}<br />
 * This class is divided up into multiple prodcedures to reduce overall complexity<br /><br />
 *
 * Although there are many possible outcomes, for a 4v4 match this code would take the
 * following path:<br /><br />
 *
 * <ul>
 *   <li>accept()</li>
 *   <ul>
 *     <li>renderParticipantLines()</li>
 *     <ul>
 *       <li>render4v4MatchLines()</li>
 *       <ul>
 *         <li>renderTeamMemberOverviewLines()</li>
 *         <li>renderTeamMemberOverviewLines()</li>
 *       </ul>
 *     </ul>
 *     <li>renderMetaLines()</li>
 *   </ul>
 * </ul>
 */
final class MatchScoreGetter implements BiConsumer<Player, LinkedList<String>> {

    // we can't count heals on an async thread so we use
    // a task to count and then report values (via this map) to
    // the scoreboard thread
    private Map<UUID, Integer> healsLeft = ImmutableMap.of();

    MatchScoreGetter() {
        Bukkit.getScheduler().runTaskTimer(PotPvPND.getInstance(), () -> {
            MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();
            Map<UUID, Integer> newHealsLeft = new HashMap<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                Match playing = matchHandler.getMatchPlaying(player);

                if (playing == null) {
                    continue;
                }

                HealingMethod healingMethod = playing.getKitType().getHealingMethod();

                if (healingMethod == null) {
                    continue;
                }

                int count = healingMethod.count(player.getInventory().getContents());
                newHealsLeft.put(player.getUniqueId(), count);
            }

            this.healsLeft = newHealsLeft;
        }, 10L, 10L);
    }

    @Override
    public void accept(Player player, LinkedList<String> scores) {
        Optional<UUID> followingOpt = PotPvPND.getInstance().getFollowHandler().getFollowing(player);
        MatchHandler matchHandler = PotPvPND.getInstance().getMatchHandler();
        Match match = matchHandler.getMatchPlayingOrSpectating(player);
        List<MatchTeam> teams = match.getTeams();
        MatchTeam ourTeam = match.getTeam(player.getUniqueId());
        MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);
        int ourTeamSize = 0;
        if(ourTeam != null)
            ourTeamSize = ourTeam.getAllMembers().size();
        int otherTeamSize = otherTeam.getAllMembers().size();

        boolean participant = match.getTeam(player.getUniqueId()) != null;
        boolean renderPing = true;

        if (participant) {
            renderPing = renderParticipantLines(scores, match, player);
        } else {
            MatchTeam previousTeam = match.getPreviousTeam(player.getUniqueId());
            renderSpectatorLines(scores, match, previousTeam);
        }
        if (!(ourTeamSize == 1) && !(otherTeamSize == 1)) {
                renderMetaLines(scores, match, participant);
            }

    }

    private boolean renderParticipantLines(List<String> scores, Match match, Player player) {
        boolean participant = match.getTeam(player.getUniqueId()) != null;
        List<MatchTeam> teams = match.getTeams();

        // only render scoreboard if we have two teams
        if (teams.size() != 2) {
            return false;
        }

        // this method won't be called if the player isn't a participant
        MatchTeam ourTeam = match.getTeam(player.getUniqueId());
        MatchTeam otherTeam = teams.get(0) == ourTeam ? teams.get(1) : teams.get(0);

        // we use getAllMembers instead of getAliveMembers to avoid
        // mid-match scoreboard changes as players die / disconnect
        int ourTeamSize = ourTeam.getAllMembers().size();
        int otherTeamSize = otherTeam.getAllMembers().size();
        int totalSize = otherTeamSize + ourTeamSize;

        if (ourTeamSize == 1 && otherTeamSize == 1) {
            if ((ourTeam.getAliveMembers().size() + otherTeam.getAliveMembers().size()) == 1) {
                renderMatchOverLines(scores);
            } else {
                render1v1MatchLines(scores, otherTeam, match);
                renderMetaLines(scores, match, participant);
                renderBoxingMatchLines(scores, player, ourTeam, otherTeam);
                renderPearlFightMatchLines(scores, player, ourTeam, otherTeam);
                renderPingMatchLines(
                    scores,
                    match,
                    otherTeam,
                    Bukkit.getServer().getPlayer(PotPvPND.getInstance().getUuidCache().name(ourTeam.getFirstMember())));
            }
        } else if (ourTeamSize <= 2 && otherTeamSize <= 2) {
            render2v2MatchLines(scores, ourTeam, otherTeam, player, match.getKitType().getHealingMethod());
        } else if (ourTeamSize <= 4 && otherTeamSize <= 4) {
            render4v4MatchLines(scores, ourTeam, otherTeam);
        } else if (ourTeam.getAllMembers().size() <= 9) {
            renderLargeMatchLines(scores, ourTeam, otherTeam);
        } else {
            renderJumboMatchLines(scores, ourTeam, otherTeam);
        }

        String archerMarkScore = getArcherMarkScore(player);
        String bardEffectScore = getBardEffectScore(player);
        String bardEnergyScore = getBardEnergyScore(player);

        if (archerMarkScore != null) {
            scores.add("&6&lArcher Mark&7: &f" + archerMarkScore);
        }

        if (bardEffectScore != null) {
            scores.add("&a&lBard Effect&7: &f" + bardEffectScore);
        }

        if (bardEnergyScore != null) {
            scores.add("&b&lBard Energy&7: &f" + bardEnergyScore);
        }

        return false;
    }

    private void render1v1MatchLines(List<String> scores, MatchTeam otherTeam, Match match) {
        scores.add("&cOpponent: &f" + PotPvPND.getInstance().getUuidCache().name(otherTeam.getFirstMember()));
        scores.add("&cLadder: &f" + match.getKitType().getId());
        scores.add("&cArena: &f" + match.getArena().getSchematic());
    }

    private void render2v2MatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam, Player player, HealingMethod healingMethod) {
        // 2v2, but potentially 1v2 / 1v1 if players have died
        UUID partnerUuid = null;

        for (UUID teamMember : ourTeam.getAllMembers()) {
            if (teamMember != player.getUniqueId()) {
                partnerUuid = teamMember;
                break;
            }
        }

        if (partnerUuid != null) {
            String healthStr;
            String healsStr;
            String namePrefix;

            if (ourTeam.isAlive(partnerUuid)) {
                Player partnerPlayer = Bukkit.getPlayer(partnerUuid); // will never be null (or isAlive would've returned false)
                double health = Math.round(partnerPlayer.getHealth()) / 2D;
                int heals = healsLeft.getOrDefault(partnerUuid, 0);

                ChatColor healthColor;
                ChatColor healsColor;

                if (health > 8) {
                    healthColor = ChatColor.GREEN;
                } else if (health > 6) {
                    healthColor = ChatColor.YELLOW;
                } else if (health > 4) {
                    healthColor = PotPvPND.getInstance().getDominantColor();
                } else if (health > 1) {
                    healthColor = ChatColor.RED;
                } else {
                    healthColor = ChatColor.DARK_RED;
                }

                if (heals > 20) {
                    healsColor = ChatColor.GREEN;
                } else if (heals > 12) {
                    healsColor = ChatColor.YELLOW;
                } else if (heals > 8) {
                    healsColor = PotPvPND.getInstance().getDominantColor();
                } else if (heals > 3) {
                    healsColor = ChatColor.RED;
                } else {
                    healsColor = ChatColor.DARK_RED;
                }

                namePrefix = "&a";
                healthStr = healthColor.toString() + health + " *❤*" + ChatColor.GRAY;

                if (healingMethod != null) {
                    healsStr = " &l⏐ " + healsColor.toString() + heals + " " + (heals == 1 ? healingMethod.getShortSingular() : healingMethod.getShortPlural());
                } else {
                    healsStr = "";
                }
            } else {
                namePrefix = "&7&m";
                healthStr = "&4RIP";
                healsStr = "";
            }

            scores.add(namePrefix + PotPvPND.getInstance().getUuidCache().name(partnerUuid));
            scores.add(healthStr + healsStr);
            scores.add("&b");
        }

        scores.add("&c&lOpponents");
        scores.addAll(renderTeamMemberOverviewLines(otherTeam));

        // Removes the space
        if (PotPvPND.getInstance().getMatchHandler().getMatchPlaying(player).getState() == MatchState.IN_PROGRESS) {
            scores.add("&c");
        }
    }

    private void render4v4MatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // Above a 2v2, but up to a 4v4.
        scores.add("&aTeam &a(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLinesWithHearts(ourTeam));
        scores.add("&b");
        scores.add("&cOpponents &c(" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLines(otherTeam));
        if (PotPvPND.getInstance().getMatchHandler().getMatchPlaying(Bukkit.getPlayer(ourTeam.getFirstAliveMember())).getState() == MatchState.IN_PROGRESS) {
            scores.add("&c");
        }
    }

    private void renderLargeMatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // We just display THEIR team's names, and the other team is a number.
        scores.add("&aTeam &a(" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size() + ")");
        scores.addAll(renderTeamMemberOverviewLinesWithHearts(ourTeam));
        scores.add("&b");
        scores.add("&cOpponents: &f" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
    }

    private void renderJumboMatchLines(List<String> scores, MatchTeam ourTeam, MatchTeam otherTeam) {
        // We just display numbers.
        scores.add("&aTeam: &f" + ourTeam.getAliveMembers().size() + "/" + ourTeam.getAllMembers().size());
        scores.add("&cOpponents: &f" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
    }

    private void renderSpectatorLines(List<String> scores, Match match, MatchTeam oldTeam) {
        final String rankedStr=match.isRanked() ? " (R)" : "";


        final List<MatchTeam> teams=match.getTeams();
        if (teams.size() == 2) {

            final MatchTeam teamOne=teams.get(0);
            final MatchTeam teamTwo=teams.get(1);

            if (teamOne.getAllMembers().size() != 1 && teamTwo.getAllMembers().size() != 1) {

                if (oldTeam == null) {
                    scores.add("&aTeam One: &f" + teamOne.getAliveMembers().size() + "/" + teamOne.getAllMembers().size());
                    scores.add("&cTeam Two: &f" + teamTwo.getAliveMembers().size() + "/" + teamTwo.getAllMembers().size());
                } else {

                    final MatchTeam otherTeam=(oldTeam == teamOne) ? teamTwo : teamOne;
                    scores.add("&fTeam: &f" + oldTeam.getAliveMembers().size() + "/" + oldTeam.getAllMembers().size());
                    scores.add("&fOpponents: &f" + otherTeam.getAliveMembers().size() + "/" + otherTeam.getAllMembers().size());
                }
            }
        }
    }

    private void renderMetaLines(List<String> scores, Match match, boolean participant) {
        Date startedAt = match.getStartedAt();
        Date endedAt = match.getEndedAt();
        String formattedDuration;

        // short circuit for matches which are still counting down
        // or which ended before they started (if a player disconnects
        // during countdown)
        if (startedAt == null) {
            return;
        } else {
            // we go from when it started to either now (if it's in progress)
            // or the timestamp at which the match actually ended
            formattedDuration = TimeUtils.formatLongIntoMMSS(ChronoUnit.SECONDS.between(
                startedAt.toInstant(),
                endedAt == null ? Instant.now() : endedAt.toInstant()
            ));
        }

        // spectators don't have any bold entries on their scoreboard
        scores.add("&cDuration: &f" + formattedDuration);

        scores.remove("&cLadder: &f" + match.getKitType().getId());
        scores.remove("&cArena: &f" + match.getArena().getSchematic());
    }

  private void renderBoxingMatchLines(List<String> scores, Player player, MatchTeam ourTeam, MatchTeam otherTeam) {
        Match match = PotPvPND.getInstance().getMatchHandler().getMatchPlaying(player);
        if (!match.getKitType().getId().contains("Boxing"))
            return;

        int yourteam = 0;
        int theirteam = 0;
        int you = MatchBoxingListener.getHitMap().getOrDefault(player.getUniqueId(), 0);
        for(UUID uuid : ourTeam.getAllMembers()) { yourteam += MatchBoxingListener.getHitMap().getOrDefault(uuid, 0); }
        for(UUID uuid : otherTeam.getAllMembers()) { theirteam += MatchBoxingListener.getHitMap().getOrDefault(uuid, 0); }

        String hitValue = (yourteam >= theirteam) ? "&a(+" + (yourteam - theirteam) + "&a)" : "&c(-" + (theirteam - yourteam) + "&c)";
        if(PotPvPND.getInstance().getPartyHandler().hasParty(player)) {
            scores.add("");
            scores.add("&cHits: " + hitValue);
            scores.add(" &cYour Team: &f" + yourteam);
            scores.add(" &cTheir Team: &f" + theirteam);
        } else {
            scores.add("");
            scores.add("&cHits: " + hitValue);
            scores.add(" &cYou&7: &f" + you);
            scores.add(" &cThem&7: &f" + theirteam);
        }
    }

    private void renderPearlFightMatchLines(List<String> scores, Player player, MatchTeam ourTeam, MatchTeam otherTeam) {
        Match match = PotPvPND.getInstance().getMatchHandler().getMatchPlaying(player);
        if (!match.getKitType().getId().contains("PearlFight"))
            return;

        String you = MatchPearlFightListener.getLivesIcon(MatchPearlFightListener.getLivesMap().getOrDefault(player.getUniqueId(), 3));
        String them = MatchPearlFightListener.getLivesIcon(MatchPearlFightListener.getLivesMap().getOrDefault(otherTeam.getFirstMember(), 3));
        scores.add("");
        scores.add("&cLives:");
        scores.add(" &cYou&7: &f" + you);
        scores.add(" &cThem&7: &f" + them);
        }

    private void renderPingMatchLines(List<String> scores, Match match, MatchTeam otherTeam, Player ourPlayer) {
        List<MatchTeam> teams = match.getTeams();
        MatchTeam ourTeam = teams.get(0);
        final Player otherPlayer = Bukkit.getPlayer((match.getTeam(ourPlayer.getUniqueId()) == ourTeam) ? otherTeam.getFirstMember() : ourTeam.getFirstMember());
        scores.add("");
        scores.add("&cPing: &a" + PlayerUtils.getPing(ourPlayer) + "ms" + " &7┃ &c" + PlayerUtils.getPing(otherPlayer) + "ms");
    }

    private void renderMatchOverLines(List<String> scores) {
        scores.add("Match ended.");
    }


    /* Returns the names of all alive players, colored + indented, followed
       by the names of all dead players, colored + indented. */

    private List<String> renderTeamMemberOverviewLinesWithHearts(MatchTeam team) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // seperate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + PotPvPND.getInstance().getUuidCache().name(teamMember) + " " + getHeartString(team, teamMember));
            } else {
                deadLines.add(" &7&m" + PotPvPND.getInstance().getUuidCache().name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        return result;
    }

    private List<String> renderTeamMemberOverviewLines(MatchTeam team) {
        List<String> aliveLines = new ArrayList<>();
        List<String> deadLines = new ArrayList<>();

        // seperate lists to sort alive players before dead
        // + color differently
        for (UUID teamMember : team.getAllMembers()) {
            if (team.isAlive(teamMember)) {
                aliveLines.add(" &f" + PotPvPND.getInstance().getUuidCache().name(teamMember));
            } else {
                deadLines.add(" &7&m" + PotPvPND.getInstance().getUuidCache().name(teamMember));
            }
        }

        List<String> result = new ArrayList<>();

        result.addAll(aliveLines);
        result.addAll(deadLines);

        return result;
    }

    private String getHeartString(MatchTeam ourTeam, UUID partnerUuid) {
        if (partnerUuid != null) {
            String healthStr;

            if (ourTeam.isAlive(partnerUuid)) {
                Player partnerPlayer = Bukkit.getPlayer(partnerUuid); // will never be null (or isAlive would've returned false)
                double health = Math.round(partnerPlayer.getHealth()) / 2D;

                ChatColor healthColor;

                if (health > 8) {
                    healthColor = ChatColor.GREEN;
                } else if (health > 6) {
                    healthColor = ChatColor.YELLOW;
                } else if (health > 4) {
                    healthColor = PotPvPND.getInstance().getDominantColor();
                } else if (health > 1) {
                    healthColor = ChatColor.RED;
                } else {
                    healthColor = ChatColor.DARK_RED;
                }

                healthStr = healthColor.toString() + "(" + health + " ❤)";
            } else {
                healthStr = "&4(RIP)";
            }

            return healthStr;
        } else {
            return "&4(RIP)";
        }
    }

    public String getArcherMarkScore(Player player) {
        if (ArcherClass.isMarked(player)) {
            long diff = ArcherClass.getMarkedPlayers().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.getTIME_FANCY().apply(diff / 1000F));
            }
        }

        return (null);
    }

    public String getBardEffectScore(Player player) {
        if (BardClass.getLastEffectUsage().containsKey(player.getName()) && BardClass.getLastEffectUsage().get(player.getName()) >= System.currentTimeMillis()) {
            float diff = BardClass.getLastEffectUsage().get(player.getName()) - System.currentTimeMillis();

            if (diff > 0) {
                return (ScoreFunction.getTIME_SIMPLE().apply((int) (diff / 1000F)));
            }
        }

        return (null);
    }

    public String getBardEnergyScore(Player player) {
        if (BardClass.getEnergy().containsKey(player.getName())) {
            float energy = BardClass.getEnergy().get(player.getName());

            if (energy > 0) {
                // No function here, as it's a "raw" value.
                return (String.valueOf(BardClass.getEnergy().get(player.getName())));
            }
        }

        return (null);
    }
}
