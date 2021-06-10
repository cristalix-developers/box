package me.func.box

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.box.bar.WaitingPlayers
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import ru.cristalix.core.CoreApi
import ru.cristalix.core.formatting.Color
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.RealmUpdatePackage
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.stats.IStatService
import ru.cristalix.core.stats.PlayerScope
import ru.cristalix.core.stats.UserManager
import ru.cristalix.core.stats.impl.StatService
import ru.cristalix.core.stats.impl.network.StatServiceConnectionData
import java.util.*
import kotlin.math.max
import kotlin.math.min

lateinit var app: Box

class Box : JavaPlugin() {

    private val statScope = PlayerScope("box", Stat::class.java)

    private lateinit var worldMeta: WorldMeta
    lateinit var spawn: Location
    private lateinit var userManager: UserManager<User>
    lateinit var zero: Location
    var status = Status.STARTING
    var slots = 8
    var size = 70
    var waitingBar = WaitingPlayers()
    val woodPickaxe = ItemStack(Material.WOOD_PICKAXE)
    val teams = arrayListOf(
        BoxTeam(mutableListOf(), true, Color.RED, null, null),
        BoxTeam(mutableListOf(), true, Color.BLUE, null, null)
    )

    override fun onEnable() {
        B.plugin = this
        app = this
        Platforms.set(PlatformDarkPaper())

        // Загрузка карты
        loadMap()

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.WAITING_FOR_PLAYERS
        info.extraSlots = 2
        info.maxPlayers = slots
        info.readableName = "Бедроковая коробка 8x8"
        info.groupName = "Бедроковая коробка 8x8"

        // Регистрация сервисов
        val core = CoreApi.get()

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

        // Регистрация обработчиков
        B.events(BlockListener(), DefaultListener(), TradeMenu())

        // Скорборд команды
        val manager = Bukkit.getScoreboardManager()
        val board = manager.newScoreboard
        teams.forEach {
            it.team = board.registerNewTeam(it.color.teamName)
            it.team!!.nameTagVisibility = NameTagVisibility.HIDE_FOR_OTHER_TEAMS
            it.team!!.color = org.bukkit.ChatColor.valueOf(it.color.name)
            it.team!!.setAllowFriendlyFire(false)
            it.team!!.prefix = "" + it.color.chatColor
            it.team!!.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER)
        }

        // Таймер
        var time = 0
        Bukkit.getScheduler().runTaskTimer(this, {
            time++

            when (status) {
                Status.STARTING -> {
                    if (time == Status.STARTING.lastSecond) {
                        // Обновление статуса реалма
                        val realm = IRealmService.get().currentRealmInfo
                        realm.status = RealmStatus.GAME_STARTED_RESTRICTED
                        ISocketClient.get().write(RealmUpdatePackage(RealmUpdatePackage.UpdateType.UPDATE, realm))
                        // Смена статуса игры и остановка счетчика игроков
                        status = Status.GAME
                        // Заполение команд
                        Bukkit.getOnlinePlayers().forEach { player ->
                            player.inventory.clear()
                            player.inventory.addItem(woodPickaxe)
                            app.getUser(player)!!.stat!!.games++
                            waitingBar.removeViewer(player.uniqueId)

                            if (teams.any { it.players.contains(player.uniqueId) })
                                return@forEach
                            teams.sortedBy { it.players.size }[0].players.add(player.uniqueId)

                            // Скорборды
                            B.postpone(1) {
                                val address = UUID.randomUUID().toString()
                                val objective =
                                    Cristalix.scoreboardService().getPlayerObjective(player.uniqueId, address)
                                objective.displayName = "Бедрок. коробка"
                                objective.startGroup("Игра")
                                    .record("Врагов") {
                                        "§c" + teams.filter { !it.players.contains(player.uniqueId) }
                                            .sumOf { it.players.size }
                                    }.record("Своих") {
                                        "§b" + teams.filter { it.players.contains(player.uniqueId) }
                                            .sumOf { it.players.size }
                                    }.record {
                                        val pTime = Status.END.lastSecond - time
                                        "§7Авторестарт " + String.format("%02d:%02d", pTime / 60, pTime % 60)
                                    }
                                objective.startGroup("Сервер")
                                    .record("Онлайн") { IRealmService.get().getOnlineOnRealms("BOX").toString() }
                                Cristalix.scoreboardService().setCurrentObjective(player.uniqueId, address)
                            }
                        }
                        // Отпрака игроков по домам
                        B.postpone(1) {
                            // Генерация комнат
                            Generator.generateRooms(zero, size, size, size, 10, 5)
                                .forEachIndexed { index, location ->
                                    teams[index].location = location
                                }
                            teams.forEach { team ->
                                team.players.forEach {
                                    val player = Bukkit.getPlayer(it) ?: return@forEach
                                    player.itemOnCursor = null
                                    player.teleport(team.location)
                                    team.team!!.addPlayer(player)
                                    player.scoreboard = board
                                }
                            }
                        }
                    }
                    waitingBar.updateMessage()
                    if (Bukkit.getOnlinePlayers().size == slots && time < Status.STARTING.lastSecond - 10) {
                        time = Status.STARTING.lastSecond - 10
                    } else if (time == Status.STARTING.lastSecond - 10 && Bukkit.getOnlinePlayers().size + 1 < slots) {
                        time = 0
                        return@runTaskTimer
                    }
                    if (time == Status.STARTING.lastSecond - 10 && Bukkit.getOnlinePlayers().size + 1 >= slots) {
                        Generator.generateCube(zero, size, size, size)
                        return@runTaskTimer
                    }
                    if (Bukkit.getOnlinePlayers().size + 1 < slots) {
                        time = 0
                        return@runTaskTimer
                    }
                }
                Status.GAME -> {
                    if (time == Status.GAME.lastSecond)
                        status = Status.END
                    // Идет игра
                }
                Status.END -> {
                    status = Status.GAME
                    // Рестарт игры
                    B.bc(Formatting.error("Перезагрузка..."))
                    waitingBar.updateMessage()
                    B.postpone(100) {
                        teams.forEach {
                            it.players.forEach { player -> it.team!!.removePlayer(Bukkit.getPlayer(player)) }
                            it.players.clear()
                            it.location = null
                            it.bed = true
                        }
                        Bukkit.getOnlinePlayers().forEach {
                            Cristalix.transfer(listOf(it.uniqueId), RealmId.of("TEST-55"))
                        }
                        loadMap()
                        time = 0
                        status = Status.STARTING
                        B.postpone(60) {
                            val realm = IRealmService.get().currentRealmInfo
                            realm.status = RealmStatus.WAITING_FOR_PLAYERS
                            ISocketClient.get().write(RealmUpdatePackage(RealmUpdatePackage.UpdateType.UPDATE, realm))
                        }
                    }
                }
            }
        }, 5, 20)

        B.regCommand({ player, strings ->
            if (player.isOp) {
                slots = strings[0].toInt()
            }
            "Усновлено $slots слотов"
        }, "slot")

        B.regCommand({ player, strings ->
            if (player.isOp) {
                val arg = strings[0].toInt()
                size = max(50, min(arg, 130))
            }
            "Усновлен размер $size"
        }, "size")

        B.regCommand({ player, _ ->
            if (player.isOp) {
                status = Status.END
            }
            "Игра будет прекращена"
        }, "end")
    }

    fun getUser(player: Player): User? {
        return userManager.getUser(player)
    }

    fun getUser(uuid: UUID): User? {
        return userManager.getUser(uuid)
    }

    fun getWorld(): World {
        return worldMeta.world
    }

    fun getNMSWorld(): net.minecraft.server.v1_12_R1.World {
        return worldMeta.world.handle
    }

    private fun loadMap() {
        worldMeta = MapLoader().load("prod")!!
        spawn = worldMeta.getLabel("spawn").add(0.0, 2.0, 0.0).toCenterLocation()
        zero = Location(worldMeta.world, 0.0, 20.0, 0.0)
        Generator.generateContentOfCube(Location(worldMeta.world, 0.0, 20.0, 0.0), size, size, size)
    }
}