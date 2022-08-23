package me.func.battlepass

import com.mongodb.client.model.Indexes.hashed
import me.func.protocol.LogPacket
import me.func.serviceapi.mongo.MongoAdapter
import me.func.serviceapi.runListener
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.ISocketClient

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
                .className(LogPacket::class.java.name)
                .notification(true)
                .build()
        )

        runListener<LogPacket>{ realm, pckg ->
            mongo.save(pckg)
            println("Log ${pckg.uuidPlayer}: ${pckg.action.name} - ${pckg.data}, from ${realm.realmName}")
        }
    }
}