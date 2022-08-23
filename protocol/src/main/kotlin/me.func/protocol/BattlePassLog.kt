package me.func.protocol

import me.func.serviceapi.mongo.Unique
import ru.cristalix.core.network.CorePackage
import java.util.*

data class LogPacket(
    val uuidPlayer: UUID,
    val action: ActionLog,
    val data: String,
    override val uuid: UUID = UUID.randomUUID(),
    val timestamp: Long = System.currentTimeMillis()
): CorePackage(), Unique

enum class ActionLog {
    BATTLEPASS,
    QUEST,
    REWARD,
}