package me.func.battlepass

import ru.cristalix.core.CoreApi
import java.time.LocalDateTime
import java.util.UUID

object BattlePassLog {

    fun log(uuidPlayer: UUID, typeLog: TypeLog, log: String) {
        CoreApi.get().socketClient.write(
            BattlePassSendLog(
            Log(
                UUID.randomUUID(),
                LocalDateTime.now(),
                uuidPlayer,
                typeLog,
                log
            )
            )
        )
    }
}

enum class TypeLog {
    BATTLEPASS,
    QUEST,
    REWARD,
}