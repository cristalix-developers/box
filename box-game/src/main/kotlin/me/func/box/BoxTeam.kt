package me.func.box

import org.bukkit.Location
import org.bukkit.scoreboard.Team
import ru.cristalix.core.formatting.Color
import java.util.*

data class BoxTeam(
    val players: MutableList<UUID>,
    var bed: Boolean,
    val color: Color,
    var location: Location?,
    var team: Team?
)
