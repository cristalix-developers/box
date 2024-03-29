package me.func.box.bar

import me.func.box.app
import org.bukkit.Bukkit
import ru.cristalix.core.display.enums.EnumPosition

import ru.cristalix.core.display.enums.EnumUpdateType

import ru.cristalix.core.display.messages.ProgressMessage
import ru.cristalix.core.formatting.Color

class WaitingPlayers : GameBar() {
    override val message: ProgressMessage
        get() {
            val online = Bukkit.getOnlinePlayers().size
            return ProgressMessage
                .builder()
                .name("Ожидание игроков... [$online/${app.slots}]")
                .updateType(EnumUpdateType.UPDATE)
                .color(Color.BLUE)
                .position(EnumPosition.TOPTOP)
                .start(0)
                .percent(online.toFloat() / app.slots.toFloat())
                .build()
        }
}
