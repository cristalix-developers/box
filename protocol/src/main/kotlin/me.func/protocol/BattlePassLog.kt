package me.func.protocol

import me.func.serviceapi.mongo.Unique
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.network.CorePackage
import java.util.*

data class LogPacket(
    val uuidPlayer: UUID,
    val action: ActionLog,
    val data: String,
    override val uuid: UUID = UUID.randomUUID(),
    val timestamp: Long = System.currentTimeMillis()
): CorePackage(), Unique

enum class ActionLog(val item: Material) {
    BATTLEPASS(Material.DIAMOND),
    SKIPLEVEL(Material.GOLD_INGOT),
    QUEST(Material.PAPER),
    REWARD(Material.GOLDEN_APPLE),
}