package me.func.box

import clepto.bukkit.B
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.kensuke.Kensuke
import dev.implario.kensuke.Scope
import dev.implario.kensuke.UserManager
import dev.implario.kensuke.impl.bukkit.BukkitKensuke
import dev.implario.kensuke.impl.bukkit.BukkitUserManager
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.box.donate.DonateViewer
import me.func.box.donate.Lootbox
import me.func.box.reward.DailyRewardManager
import net.minecraft.server.v1_12_R1.SoundEffects.id
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.boards.bukkitapi.Boards
import ru.cristalix.core.CoreApi
import ru.cristalix.core.account.IAccountService
import ru.cristalix.core.inventory.*
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
import kotlin.math.cos
import kotlin.math.sin

lateinit var app: App

class App : JavaPlugin() {

    private lateinit var statScope: Scope<Stat>
    lateinit var worldMeta: WorldMeta
    private lateinit var kensuke: Kensuke
    lateinit var spawn: Location
    lateinit var userManager: UserManager<User>

    override fun onEnable() {
        B.plugin = this
        app = this
        Platforms.set(PlatformDarkPaper())

        // Загрузка карты
        worldMeta = MapLoader().load("Event")!!
        spawn = worldMeta.getLabel("spawn").add(0.0, 3.0, 0.0).toCenterLocation()

        statScope = Scope("boxll", Stat::class.java)
        userManager = BukkitUserManager(
            listOf(statScope),
            { session, context -> User(session, context.getData(statScope)) },
            { user, context -> context.store(statScope, user.stat) }
        )

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.WAITING_FOR_PLAYERS
        info.maxPlayers = 1200
        info.readableName = "Бедроковая коробка"
        info.groupName = "Бедроковая коробка"
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
        userManager.isOptional = true

        createTop(Location(worldMeta.world, -258.0, 115.6, 19.5), "Убийств", "Топ убийств", "kills") {
            "" + it.kills
        }
        createTop(Location(worldMeta.world, -266.5, 115.6, 28.0, -90f, 0f), "Побед", "Топ побед", "wins") {
            "" + it.wins
        }

        val location = Location(worldMeta.world, -258.0, 116.0, 28.5)
        val chest = Location(worldMeta.world, -249.5, 111.5, 26.6)
        var counter = 0
        Bukkit.getScheduler().runTaskTimer(this, {
            counter += 15
            worldMeta.world.spawnParticle(
                Particle.REDSTONE, location.clone().add(
                    sin(-Math.toRadians(counter % 360.0)) * 2,
                    0.0,
                    cos(-Math.toRadians(counter % 360.0)) * 2,
                ), 1
            )
            worldMeta.world.spawnParticle(
                Particle.REDSTONE, location.clone().add(
                    sin(Math.toRadians(counter % 360.0)) * 7,
                    -2.0,
                    cos(Math.toRadians(counter % 360.0)) * 7,
                ), 1
            )
            worldMeta.world.spawnParticle(
                Particle.FIREWORKS_SPARK, chest.clone().add(
                    0.0,
                    sin(Math.toRadians(counter * 2 % 360.0)) * (1.1 + (counter % 100) / 300.0),
                    cos(Math.toRadians(counter * 2 % 360.0)) * (1.1 + (counter % 100) / 300.0)
                ), 1, 0.0, 0.0, 0.0, 0.05
            )
        }, 1, 1)

        Npcs.init(this)
        Npcs.spawn(
            Npc.builder()
                .location(Location(worldMeta.world, -262.0, 112.0, 36.0, -152f, 0f))
                .name(ServerType.BOX8.title)
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
                .location(Location(worldMeta.world, -255.0, 112.0, 36.0, 162f, 0f))
                .name(ServerType.BOX4.title)
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
                .location(Location(worldMeta.world, -265.0, 112.0, 34.0, -125f, 0f))
                .name(ServerType.BOX5.title)
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
                .location(Location(worldMeta.world, -258.5, 112.0, 36.0, -174f, 0f))
                .name(ServerType.BOXS.title)
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
                .location(Location(worldMeta.world, -252.0, 112.0, 34.0, 137f, 0f))
                .name(ServerType.BOXN.title)
                .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                .skinUrl("https://webdata.c7x.dev/textures/skin/e7c13d3d-ac38-11e8-8374-1cb72caa35fd")
                .skinDigest("e7c13d3d-ac3811e883741cb72caa35fd")
                .type(EntityType.PLAYER)
                .onClick { player ->
                    val navigator = ClickServer("BOXN", 16)
                    navigator.accept(player)
                }.build()
        )

        B.events(FamousListener(), GlobalListener(), Lootbox, DonateViewer())

        B.regCommand({ player, strings ->
            if (player.isOp) {
                app.getUser(Bukkit.getPlayer(strings[0])).stat.money = strings[1].toInt()
                "Деньги выданы"
            } else
                null
        }, "money")
        B.regCommand({ player, strings ->
            if (player.isOp) {
                app.getUser(Bukkit.getPlayer(strings[0])).stat.wins = strings[1].toInt()
                "Победы изменены"
            } else
                null
        }, "wins")
        B.regCommand({ player, strings ->
            if (player.isOp) {
                app.getUser(Bukkit.getPlayer(strings[0])).stat.kills = strings[1].toInt()
                "Убийства изменены"
            } else
                null
        }, "kills")
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
                            entry.data.stat.lastSeenName =
                                IAccountService.get().getNameByUuid(UUID.fromString(entry.data.session.userId)).get()
                        blocks.addContent(
                            UUID.fromString(entry.data.session.userId),
                            "" + entry.position,
                            entry.data.stat.lastSeenName,
                            "§d" + function(entry.data.stat)
                        )
                    }

                    blocks.updateContent()
                }
            }, 20, 10 * 20
        )
    }

    fun getUser(player: Player): User {
        return userManager.getUser(player.uniqueId)
    }

}