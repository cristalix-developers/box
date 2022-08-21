package me.func.box.battlepass

import ru.cristalix.core.CoreApi
import java.util.UUID

object BattlePassLog {

    fun log(uuidPlayer: UUID, typeLog: TypeLog, log: String) {
        CoreApi.get().socketClient.write(BattlePassSendLog(
            Log(
                UUID.randomUUID(),
                uuidPlayer,
                typeLog,
                log
            ))
        )
    }
}

enum class TypeLog {
    BATTLEPASS,
    QUEST,
    REWARD,
}