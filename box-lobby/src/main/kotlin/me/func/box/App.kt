package me.func.box

import clepto.bukkit.B
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartyService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.stats.IStatService
import ru.cristalix.core.stats.PlayerScope
import ru.cristalix.core.stats.UserManager
import ru.cristalix.core.stats.impl.StatService
import ru.cristalix.core.stats.impl.network.StatServiceConnectionData
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService
import ru.cristalix.npcs.data.NpcBehaviour
import ru.cristalix.npcs.server.Npc
import ru.cristalix.npcs.server.Npcs


lateinit var app: App

class App : JavaPlugin() {

    private val statScope = PlayerScope("box", Stat::class.java)

    private lateinit var worldMeta: WorldMeta
    lateinit var spawn: Location
    private lateinit var userManager: UserManager<User>

    override fun onEnable() {
        B.plugin = this
        app = this
        Platforms.set(PlatformDarkPaper())

        // Загрузка карты
        worldMeta = MapLoader().load("Event")!!
        spawn = worldMeta.getLabel("spawn").add(0.0, 3.0, 0.0).toCenterLocation()

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.WAITING_FOR_PLAYERS
        info.maxPlayers = 1200
        info.readableName = "Бедроковая коробка Лобби"
        info.groupName = "Бедроковая коробка Лобби"
        //info.isLobbyServer = true
        info.servicedServers = arrayOf("BOX")

        // Регистрация сервисов
        val core = CoreApi.get()

        core.registerService(IPartyService::class.java, PartyService(ISocketClient.get()))
        core.registerService(ITransferService::class.java, TransferService(ISocketClient.get()))
        core.registerService(IInventoryService::class.java, InventoryService())
        val statService = StatService(core.platformServer, StatServiceConnectionData.fromEnvironment())
        core.registerService(IStatService::class.java, statService)

        statService.useScopes(statScope)

        userManager = statService.registerUserManager(
            {
                val user = User(it.uuid, it.name, it.getData(statScope))
                user
            },
            { user: User, context ->
                context.store(statScope, user.stat)
            }
        )
        B.events(FamousListener(), GlobalListener())

        Npcs.init(this)
        Npcs.spawn(
            Npc.builder()
                .location(Location(worldMeta.world, -253.0, 112.0, 33.0))
                .name("§c§l8 §fx §c§l8")
                .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                .skinUrl("https://webdata.c7x.dev/textures/skin/30719b68-2c69-11e8-b5ea-1cb72caa35fd")
                .skinDigest("307264a1-2c6911e8b5ea1cb72caa35fd")
                .type(EntityType.PLAYER)
                .onClick { player ->
                    val navigator = ClickServer("BOX8", 16)
                    navigator.accept(player)
                }.build()
        )
        Npcs.spawn(
            Npc.builder()
                .location(Location(worldMeta.world, -252.0, 112.0, 29.0))
                .name("§c§l4 §fx §c§l4")
                .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                .skinUrl("https://webdata.c7x.dev/textures/skin/6f3f4a2e-7f84-11e9-8374-1cb72caa35fd")
                .skinDigest("6f3f4a2e-7f8411e983741cb72caa35fd")
                .type(EntityType.PLAYER)
                .onClick { player ->
                    val navigator = ClickServer("BOX4", 8)
                    navigator.accept(player)
                }.build()
        )
        Npcs.spawn(
            Npc.builder()
                .location(Location(worldMeta.world, -257.0, 112.0, 34.0))
                .name("§c§l50 §fx §c§l50")
                .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                .skinUrl("https://webdata.c7x.dev/textures/skin/30392bb3-2c69-11e8-b5ea-1cb72caa35fd")
                .skinDigest("30392bb3-2c6911e8b5ea1cb72caa35fd")
                .type(EntityType.PLAYER)
                .onClick { player ->
                    val navigator = ClickServer("BOX5", 100)
                    navigator.accept(player)
                }.build()
        )
    }
}