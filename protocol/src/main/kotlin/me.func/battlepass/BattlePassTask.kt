package me.func.battlepass

import com.mongodb.client.model.Indexes.hashed
import me.func.serviceapi.mongo.MongoAdapter
import me.func.serviceapi.mongo.Unique
import me.func.serviceapi.runListener
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.CorePackage
import ru.cristalix.core.network.ISocketClient
import java.time.LocalDateTime
import java.util.*

fun main() {
    val mongo = MongoAdapter(
        System.getenv("MONGO_URI"),
        System.getenv("MONGO_DB"),
        System.getenv("MONGO_COLLECTION")
    )

    mongo.collection.createIndex(hashed("player")) { _, _ -> }
    mongo.collection.createIndex(hashed("typelog")) { _, _ -> }
    mongo.collection.createIndex(hashed("message")) { _, _ -> }

    // Начало работы брокера-сообщений
    MicroserviceBootstrap.bootstrap(MicroServicePlatform(2))

    ISocketClient.get().apply {
        registerCapabilities(
            Capability.builder()
                .className(BattlePassSendLog::class.java.name)
                .notification(true)
                .build()
        )

        runListener<BattlePassSendLog>{ realm, pckg ->
            mongo.save(pckg.log)
            println("Log saved: ${pckg.log.playerUUID}: ${pckg.log.typeLog.name} - ${pckg.log.message}")
        }
    }
}


data class BattlePassSendLog(
    var log: Log // to
) : CorePackage()

data class Log(
    override val uuid: UUID = UUID.randomUUID(),
    var time: LocalDateTime,
    var playerUUID: UUID,
    var typeLog: TypeLog,
    var message: String
): Unique