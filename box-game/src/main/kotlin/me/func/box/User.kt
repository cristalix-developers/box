package me.func.box

import org.bukkit.Location
import ru.cristalix.core.stats.player.PlayerWrapper
import java.util.*

class User(uuid: UUID, name: String, var stat: Stat?) : PlayerWrapper(uuid, name) {

    var bed: Location? = null
    var tempKills = 0
    var compassToPlayer = true
    var finalKills = 0

    init {
        if (stat == null) {
            stat = Stat(uuid, 0, 0, 0, 0)
        }
        if (stat!!.id == null)
            stat!!.id = uuid
    }
}