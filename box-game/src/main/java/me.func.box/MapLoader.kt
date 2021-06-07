package me.func.box

import clepto.cristalix.Cristalix
import clepto.cristalix.WorldMeta
import ru.cristalix.core.map.BukkitWorldLoader
import java.util.concurrent.ExecutionException

class MapLoader {
    fun load(map: String?): WorldMeta? {
        // Загрузка карты с сервера BUIL-1
        val mapInfo = Cristalix.mapService().getLatestMapByGameTypeAndMapName("Box", map)
            .orElseThrow { RuntimeException("Map Forest wasn't found in the MapService") }
        return try {
            val meta = WorldMeta(Cristalix.mapService().loadMap(mapInfo.latest, BukkitWorldLoader.INSTANCE).get())
            meta.world.setGameRuleValue("mobGriefing", "false")
            meta.world.setGameRuleValue("doTileDrops", "false")
            meta.world.setGameRuleValue("doDaylightCycle", "true")
            meta.world.setGameRuleValue("naturalRegeneration", "true")
            meta
        } catch (exception: Exception) {
            when (exception) {
                is InterruptedException,
                is ExecutionException -> {
                    exception.printStackTrace()
                    Thread.currentThread().interrupt()
                }
            }
            throw exception
        }
    }
}