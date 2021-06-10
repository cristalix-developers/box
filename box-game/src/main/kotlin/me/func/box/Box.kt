package me.func.box

import clepto.bukkit.B
import clepto.cristalix.WorldMeta
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
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.RealmUpdatePackage
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.stats.IStatService
import ru.cristalix.core.stats.PlayerScope
import ru.cristalix.core.stats.UserManager
import ru.cristalix.core.stats.impl.StatService
import ru.cristalix.core.stats.impl.network.StatServiceConnectionData
import dev.implario.bukkit.platform.Platforms
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.transfer.ITransferService


lateinit var app: Box

class Box : JavaPlugin() {

    private val statScope = PlayerScope("box", Stat::class.java)

    private lateinit var worldMeta: WorldMeta
    lateinit var spawn: Location
    lateinit var zero: Location
    private lateinit var userManager: UserManager<User>
    var status = Status.STARTING
    var slots = 100
    var size = 100
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
        worldMeta = MapLoader().load("prod")!!
        spawn = worldMeta.getLabel("spawn").add(0.0, 2.0, 0.0).toCenterLocation()
        zero = Location(worldMeta.world, 0.0, 20.0, 0.0)
        Generator.generateContentOfCube(Location(worldMeta.world, 0.0, 20.0, 0.0), size, size, size)

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.STARTING_GAME
        info.extraSlots = slots
        info.maxPlayers = slots
        info.readableName = "Бедроковая коробка"
        info.groupName = "Бедроковая коробка"

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
                        waitingBar.stop()
                        // Генерация комнат
                        Generator.generateRooms(zero, size, size, size, 10, 5)
                            .forEachIndexed { index, location ->
                                teams[index].location = location
                            }
                        // Заполение команд
                        Bukkit.getOnlinePlayers().forEach { player ->
                            player.inventory.clear()
                            player.inventory.addItem(woodPickaxe)

                            if (teams.any { it.players.contains(player.uniqueId) })
                                return@forEach
                            teams.sortedBy { it.players.size }[0].players.add(player.uniqueId)
                        }
                        // Отпрака игроков по домам
                        teams.forEach { team ->
                            team.players.forEach {
                                val player = Bukkit.getPlayer(it)
                                player.teleport(team.location)
                                team.team!!.addPlayer(player)
                                player.scoreboard = board
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
                    // Рестарт игры
                    teams.forEach {
                        it.players.forEach { player -> it.team!!.removePlayer(Bukkit.getPlayer(player)) }
                        it.players.clear()
                        it.location = null
                        it.bed = true
                    }
                    val service = ITransferService.get()
                    Bukkit.getOnlinePlayers().forEach {
                        service.transfer(it.uniqueId, RealmId.of("BOXL", 1))
                    }
                    waitingBar = WaitingPlayers()
                    status = Status.STARTING
                    Generator.generateContentOfCube(Location(worldMeta.world, 0.0, 20.0, 0.0), size, size, size)
                    time = 0
                }
            }
        }, 5, 20)
    }

    fun getUser(player: Player): User? {
        return userManager.getUser(player)
    }

    fun getWorld(): World {
        return worldMeta.world
    }

    fun getNMSWorld(): net.minecraft.server.v1_12_R1.World {
        return worldMeta.world.handle
    }
}