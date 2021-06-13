package me.func.box

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import clepto.cristalix.WorldMeta
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.box.bar.WaitingPlayers
import net.md_5.bungee.api.chat.ComponentBuilder
import org.bukkit.*
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
import ru.cristalix.core.tab.ITabService
import ru.cristalix.core.tab.TabTextComponent
import ru.cristalix.core.text.TextFormat
import java.util.*
import java.util.concurrent.CompletableFuture
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
    var slots = System.getenv("SLOT").toInt()
    var size = System.getenv("SIZE").toInt()
    val hub = "BOXL-1"
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

        // Регистрация сервисов
        val core = CoreApi.get()

        val realmService = IRealmService.get()

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        val id = realmService.currentRealmInfo.realmId.id
        info.status = RealmStatus.WAITING_FOR_PLAYERS
        info.extraSlots = 1
        info.maxPlayers = slots
        info.readableName = "БКоробка ${slots / 2}x${slots / 2} #$id"
        info.groupName = "БКоробка ${slots / 2}x${slots / 2} #$id"

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
                            player.sendTitle("§eПоехали!", "Враги без ника")
                            app.getUser(player)!!.stat!!.games++
                            waitingBar.removeViewer(player.uniqueId)

                            if (teams.any { it.players.contains(player.uniqueId) })
                                return@forEach
                            teams.sortedBy { it.players.size }[0].players.add(player.uniqueId)

                            // Скорборды
                            B.postpone(30) {
                                val address = UUID.randomUUID().toString()
                                val objective =
                                    Cristalix.scoreboardService().getPlayerObjective(player.uniqueId, address)
                                objective.displayName = "Бедроковая коробка"
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
                                    .record("Онлайн") {
                                        (realmService.getOnlineOnRealms("BOX4") +
                                                realmService.getOnlineOnRealms("BOX8") +
                                                realmService.getOnlineOnRealms("BOX5")).toString()
                                    }
                                Cristalix.scoreboardService().setCurrentObjective(player.uniqueId, address)
                            }
                        }
                        // Отпрака игроков по домам
                        B.postpone(2) {
                            // Генерация комнат
                            Generator.generateRooms(zero, size, size, size, 10, 5)
                                .forEachIndexed { index, location ->
                                    teams[index].location = location
                                }
                            // Таб
                            val tabView = ITabService.get().createConstantTabView()
                            tabView.addPrefix(TabTextComponent(
                                1,
                                TextFormat.RBRACKETS,
                                { player -> teams.any { it.players.contains(player) } },
                                { player ->
                                    val team = teams.filter {
                                        it.players.contains(
                                            player
                                        )
                                    }
                                    val text = if (team.isEmpty())
                                        "Наблюдатель"
                                    else
                                        "" + team[0].color.chatColor + team[0].color.teamName
                                    CompletableFuture.completedFuture(ComponentBuilder(
                                        text
                                    ).create())
                                }
                            ))
                            val tab = ITabService.get()
                            tab.enable()
                            teams.forEach { team ->
                                team.players.forEach {
                                    val player = Bukkit.getPlayer(it) ?: return@forEach
                                    player.itemOnCursor = null
                                    player.teleport(team.location)
                                    team.team!!.addPlayer(player)
                                    player.scoreboard = board
                                    tab.setTabView(player.uniqueId, tabView)
                                    tab.update(player)
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
                    //Обновление компасов
                    Bukkit.getOnlinePlayers().forEach { player ->
                        if (player.inventory.contains(Material.COMPASS)) {
                            try {
                                val nearestPlayer =
                                    Bukkit.getPlayer(teams.filter { !it.players.contains(player.uniqueId) }
                                        .map {
                                            it.players.minByOrNull { current ->
                                                Bukkit.getPlayer(current).location.distanceSquared(
                                                    player.location
                                                )
                                            }
                                        }
                                        .minByOrNull { current ->
                                            Bukkit.getPlayer(current).location.distanceSquared(
                                                player.location
                                            )
                                        })
                                player.compassTarget = nearestPlayer.location
                            } catch (exception: Exception) {
                            }
                        }
                    }

                    if (time == Status.GAME.lastSecond)
                        status = Status.END
                    Winner.tryGetWinner()
                    // Идет игра
                }
                Status.END -> {
                    status = Status.CLOSE
                    // Рестарт игры
                    B.bc(Formatting.error("Перезагрузка..."))
                    waitingBar.updateMessage()
                    B.postpone(100) {
                        teams.forEach {
                            it.players.forEach { player ->
                                try {
                                    val find = Bukkit.getPlayer(player)
                                    if (find != null)
                                        it.team!!.removePlayer(find)
                                } catch (ignored: Exception) {
                                }
                            }
                            it.players.clear()
                            it.location = null
                            it.bed = true
                        }
                        Bukkit.getOnlinePlayers().forEachIndexed { index, it ->
                            B.postpone(index) { Cristalix.transfer(listOf(it.uniqueId), RealmId.of(hub)) }
                        }
                        loadMap()
                        time = 0
                        status = Status.STARTING
                        B.postpone(120) {
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