package me.func.box

import org.bukkit.Location
import ru.cristalix.core.formatting.Color
import java.util.*

data class Team(
    val players: MutableList<UUID>,
    val bed: Boolean,
    val color: Color,
    var location: Location?
)
