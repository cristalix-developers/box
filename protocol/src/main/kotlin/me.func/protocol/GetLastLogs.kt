package me.func.protocol

import ru.cristalix.core.network.CorePackage
import java.util.*

data class GetLastLogs(
    var player: UUID, // to
    var count: Int, // to
    var logs: List<LogPacket> //
): CorePackage() {

    constructor(player: UUID, count: Int) : this(player, count, Collections.emptyList())

}