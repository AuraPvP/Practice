package net.frozenorb.potpvp.kt.scoreboard

import org.bukkit.entity.Player
import java.util.LinkedList

interface ScoreGetter {

    fun getScores(scores: LinkedList<String>, player: Player)

}