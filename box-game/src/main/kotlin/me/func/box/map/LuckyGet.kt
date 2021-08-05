package me.func.box.map

import me.func.box.User
import me.func.box.app
import me.func.box.data.Status
import me.func.box.listener.lucky.LuckEvent
import org.bukkit.Location
import java.util.*

object LuckyGet {

    fun removeBlock(location: Location, uuid: UUID, user: User) {
        if (app.status != Status.GAME)
            return
        app.getWorld().getEntity(uuid).remove()

        LuckEvent.values().random().accept(user)
        user.stat.luckyOpened++
        location.block.removeMetadata("lucky", app)
    }

}