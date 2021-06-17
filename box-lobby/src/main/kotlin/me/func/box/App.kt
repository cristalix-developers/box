package me.func.box

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.kensuke.Kensuke
import dev.implario.kensuke.Scope
import dev.implario.kensuke.Session
import dev.implario.kensuke.impl.bukkit.BukkitKensuke
import dev.implario.kensuke.impl.bukkit.BukkitUserManager
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.boards.bukkitapi.Boards
import ru.cristalix.core.CoreApi
import ru.cristalix.core.account.IAccountService
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartyService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService
import ru.cristalix.npcs.data.NpcBehaviour
import ru.cristalix.npcs.server.Npc
import ru.cristalix.npcs.server.Npcs
import java.util.*


lateinit var app: App

class App : JavaPlugin() {

    private val statScope = Scope("box", Stat::class.java)

    private lateinit var worldMeta: WorldMeta
    private lateinit var kensuke: Kensuke
    lateinit var spawn: Location
    private var userManager = BukkitUserManager(
        listOf(statScope),
        { session, context -> User(session, context.getData(statScope)) },
        { user, context -> context.store(statScope, user.stat) }
    )

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
        info.isLobbyServer = true
        info.servicedServers = arrayOf("BOX4", "BOX8", "BOX5", "BOXE", "BOXS", "BOXN")

        // Регистрация сервисов
        val core = CoreApi.get()

        core.registerService(IPartyService::class.java, PartyService(ISocketClient.get()))
        core.registerService(ITransferService::class.java, TransferService(ISocketClient.get()))
        core.registerService(IInventoryService::class.java, InventoryService())

        kensuke = BukkitKensuke.setup(this)
        kensuke.addGlobalUserManager(userManager)
        kensuke.globalRealm = info.realmId.realmName

        B.events(FamousListener(), GlobalListener())

        createTop(Location(worldMeta.world, -258.0, 115.6, 19.5), "Убийств", "Топ убийств", "kills") {
            "" + it.kills
        }
        createTop(Location(worldMeta.world, -266.5, 115.6, 28.0, -90f, 0f), "Побед", "Топ побед", "wins") {
            "" + it.wins
        }

        Npcs.init(this)
        Npcs.spawn(
            Npc.builder()
                .location(Location(worldMeta.world, -253.0, 112.0, 33.0, 95f, 0f))
                .name("§c§l8 §fx §c§l2 §eПВП 1.8")
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
                .location(Location(worldMeta.world, -252.0, 112.0, 29.0, 145f, 0f))
                .name("§c§l4 §fx §c§l2 §eПВП 1.8")
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
                .location(Location(worldMeta.world, -257.0, 112.0, 34.0, 165f, 0f))
                .name("§c§l50 §fx §c§l2 §eПВП 1.8")
                .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                .skinUrl("https://webdata.c7x.dev/textures/skin/30392bb3-2c69-11e8-b5ea-1cb72caa35fd")
                .skinDigest("30392bb3-2c6911e8b5ea1cb72caa35fd")
                .type(EntityType.PLAYER)
                .onClick { player ->
                    val navigator = ClickServer("BOX5", 100)
                    navigator.accept(player)
                }.build()
        )
        Npcs.spawn(
            Npc.builder()
                .location(Location(worldMeta.world, -264.0, 112.0, 36.0, -180f, 0f))
                .name("§c§l4 §fx §c§l4 §eПВП 1.8")
                .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                .skinUrl("https://webdata.c7x.dev/textures/skin/7f3fea26-be9f-11e9-80c4-1cb72caa35fd")
                .skinDigest("7f3fea26-be9f11e980c41cb72caa35fd")
                .type(EntityType.PLAYER)
                .onClick { player ->
                    val navigator = ClickServer("BOXS", 16)
                    navigator.accept(player)
                }.build()
        )
        Npcs.spawn(
            Npc.builder()
                .location(Location(worldMeta.world, -249.0, 112.0, 20.0, 45f, 0f))
                .name("§c§l4 §fx §c§l4 §eПВП 1.9")
                .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                .skinUrl("https://webdata.c7x.dev/textures/skin/e7c13d3d-ac38-11e8-8374-1cb72caa35fd")
                .skinDigest("e7c13d3d-ac3811e883741cb72caa35fd")
                .type(EntityType.PLAYER)
                .onClick { player ->
                    val navigator = ClickServer("BOXN", 16)
                    navigator.accept(player)
                }.build()
        )
    }

    private fun createTop(location: Location, string: String, title: String, key: String, function: (Stat) -> String) {
        val blocks = Boards.newBoard()
        blocks.addColumn("#", 10.0)
        blocks.addColumn("Игрок", 95.0)
        blocks.addColumn(string, 55.0)
        blocks.title = title
        blocks.location = location
        Boards.addBoard(blocks)

        Bukkit.getScheduler().scheduleSyncRepeatingTask(
            this, {
                kensuke.getLeaderboard(userManager, statScope, key, 10).thenAccept {
                    blocks.clearContent()

                    for (entry in it) {
                        if (entry.data.stat.lastSeenName == null)
                            entry.data.stat.lastSeenName = IAccountService.get().getNameByUuid(UUID.fromString(entry.data.id)).get()
                        blocks.addContent(
                            UUID.fromString(entry.data.id), "" + entry.position, entry.data.stat.lastSeenName, "§d" + function(entry.data.stat)
                        )
                    }

                    blocks.updateContent()
                }
            }, 20, 10 * 20
        )
    }

    fun getUser(player: Player): User? {
        return userManager.getUser(player)
    }

}