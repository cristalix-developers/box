package me.func.box

import ru.cristalix.core.stats.player.PlayerWrapper
import java.util.*

class User(uuid: UUID, name: String, var stat: Stat?) : PlayerWrapper(uuid, name) {

    init {
        if (stat == null) {
            stat = Stat(uuid, 0, 0, 0, 0)
        }
    }
}