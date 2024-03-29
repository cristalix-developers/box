package me.func.battlepass

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Indexes.hashed
import com.mongodb.client.model.Sorts
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.func.protocol.GetLogPacket
import me.func.protocol.LogPacket
import me.func.serviceapi.mongo.MongoAdapter
import me.func.serviceapi.runListener
import ru.cristalix.core.GlobalSerializers
import ru.cristalix.core.microservice.MicroServicePlatform
import ru.cristalix.core.microservice.MicroserviceBootstrap
import ru.cristalix.core.network.Capability
import ru.cristalix.core.network.ISocketClient
import java.util.*

fun main() {
    val mongo = MongoAdapter(
        System.getenv("MONGO_URI"),
        System.getenv("MONGO_DB"),
        System.getenv("MONGO_COLLECTION")
    )

    mongo.collection.createIndex(hashed("uuidPlayer")) { _, _ -> }
    mongo.collection.createIndex(hashed("action")) { _, _ -> }
    mongo.collection.createIndex(hashed("data")) { _, _ -> }
    mongo.collection.createIndex(hashed("timestamp")) { _, _ -> }

    // Начало работы брокера-сообщений
    MicroserviceBootstrap.bootstrap(MicroServicePlatform(2))

    fun findLogPlayer(uuid: UUID) = mongo.collection.find(Filters.eq("uuidPlayer", uuid.toString()))

    ISocketClient.get().apply {
        registerCapabilities(
            Capability.builder()
                .className(LogPacket::class.java.name)
                .notification(true)
                .build(),
            Capability.builder()
                .className(GetLogPacket::class.java.name)
                .notification(true)
                .build()
        )

        runListener<LogPacket> { realm, pckg ->
            CoroutineScope(Dispatchers.IO).launch {
                mongo.save(pckg)
                println("Log ${pckg.uuidPlayer}: ${pckg.action.name} - ${pckg.data}, from ${realm.realmName}")
            }
        }

        fun getLastLogs(player: UUID, total: Int, accept: (List<LogPacket>) -> Unit) {
            val list = mutableListOf<LogPacket>()

            findLogPlayer(player)
                .sort(Sorts.descending("timestamp"))
                .limit(total)
                .forEach({
                    list.add(GlobalSerializers.fromJson(com.mongodb.util.JSON.serialize(it), LogPacket::class.java))
                }, { _: Void?, error: Throwable? ->
                    run {
                        if (error != null) {
                            error.printStackTrace();
                            return@run
                        }
                        accept(list)
                    }
                })
        }

        runListener<GetLogPacket> { realm, pckg ->
            CoroutineScope(Dispatchers.IO).launch {
                getLastLogs(pckg.player, pckg.count) { lastLog ->
                    println("Send last ${pckg.count} logs to ${pckg.player} from ${realm.realmName}")
                    forward(realm, pckg.apply { logs = lastLog })
                }
            }
        }
    }
}