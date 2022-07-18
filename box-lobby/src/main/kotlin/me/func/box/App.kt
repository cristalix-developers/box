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
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.Npc
import me.func.mod.Npc.location
import me.func.mod.Npc.onClick
import me.func.mod.data.Sprites
import me.func.mod.selection.button
import me.func.mod.selection.choicer
import me.func.mod.util.command
import me.func.mod.util.listener
import me.func.protocol.npc.NpcBehaviour
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.ArmorStand
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
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

lateinit var app: App
val compass = choicer {
    title = "Бедроковая коробка"
    description = "Находи чужие кровати и уничтожай врагов!"
    buttons(
        button {
            texture = Sprites.SOLO.path()
            title = "Solo"
            description = "Онлайн: " + realmService.getOnlineOnRealms("BOX4")
            onClick { it, _, _ -> ClickServer("BOX4", 4).accept(it) }
        },
        button {
            texture = Sprites.SQUAD.path()
            title = "Squad"
            description = "Онлайн: " + realmService.getOnlineOnRealms("BOXS")
            onClick { it, _, _ -> ClickServer("BOXS", 16).accept(it) }
        },
        button {
            texture = Sprites.SPECIAL.path()
            title = "Lucky"
            description = "Онлайн: " + realmService.getOnlineOnRealms("BOX8")
            onClick { it, _, _ -> ClickServer("BOX8", 16).accept(it) }
        },
    )
}

class App : JavaPlugin() {

    lateinit var worldMeta: WorldMeta
    private lateinit var kensuke: Kensuke
    lateinit var spawn: Location
    lateinit var userManager: UserManager<User>
    lateinit var online: Map<ServerType, ArmorStand>
    lateinit var socketClient: ISocketClient

    private var oldStatScope = Scope("boxll", Stat::class.java)
    private val statScope = Scope("box-newa", Stat::class.java)

    override fun onEnable() {
        B.plugin = this
        app = this
        Platforms.set(PlatformDarkPaper())

        Anime.include(Kit.EXPERIMENTAL, Kit.NPC, Kit.STANDARD, Kit.LOOTBOX)

        // Загрузка карты
        worldMeta = MapLoader().load("Event")!!
        spawn = worldMeta.getLabel("spawn").add(0.0, 3.0, 0.0).toCenterLocation()

        userManager = BukkitUserManager(
            listOf(oldStatScope, statScope),
            { session, context -> User(session, context.getData(statScope), context.getData(oldStatScope)) },
            { user, context -> context.store(statScope, user.stat) }
        )

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.WAITING_FOR_PLAYERS
        info.maxPlayers = 1200
        info.readableName = "Бедроковая коробка"
        info.groupName = "Бедроковая коробка"
        info.isLobbyServer = true
        info.servicedServers = arrayOf("BOX4", "BOX8", "BOX5", "BOXE", "BOXS")

        // Регистрация сервисов
        val core = CoreApi.get()

        socketClient = core.socketClient
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

        online = ServerType.values().associateWith { server ->
            Npc.npc {
                location(server.origin)
                name = server.title
                behaviour = NpcBehaviour.STARE_AT_PLAYER
                skinUrl = "https://webdata.c7x.dev/textures/skin/${server.skin}"
                skinDigest = server.skin
                onClick {
                    val navigator = ClickServer(server.name, server.slot)
                    navigator.accept(it.player)
                }
            }
            val stand = worldMeta.world.spawnEntity(
                server.origin.clone().add(0.0, 2.3, 0.0),
                EntityType.ARMOR_STAND
            ) as ArmorStand

            stand.isMarker = true
            stand.isVisible = false
            stand.setGravity(false)
            stand.isCustomNameVisible = true

            stand
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
            online.forEach { (type, stand) ->
                val online = IRealmService.get().getOnlineOnRealms(type.name)
                stand.customName = "§b$online игроков в игре"
                val desc = "Онлайн: $online"
                when (type) {
                    ServerType.BOX4 -> compass.storage[0]
                    ServerType.BOXS -> compass.storage[1]
                    ServerType.BOX8 -> compass.storage[2]
                    else -> null
                }?.description = desc
            }
        }, 5, 10)

        listener(FamousListener, GlobalListener, Lootbox, DonateViewer())

        fun register(name: String, apply: (Stat, Int) -> String) = command(name) { player, args ->
            if (player.isOp) player.sendMessage(
                apply(
                    app.userManager.getUser(Bukkit.getPlayer(args[0]).uniqueId).stat,
                    args[1].toInt()
                )
            )
        }

        register("money") { stat, value ->
            stat.money = value
            "Деньги выданы"
        }

        register("wins") { stat, value ->
            stat.wins = value
            "Победы изменены"
        }

        register("kills") { stat, value ->
            stat.kills = value
            "Убийства изменены"
        }
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
                            "§f" + ChatColor.stripColor(entry.data?.stat?.lastSeenName),
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