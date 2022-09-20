package me.func.box

import clepto.bukkit.B
import clepto.cristalix.Cristalix.scoreboardService
import dev.implario.bukkit.item.item
import dev.implario.bukkit.platform.Platforms
import dev.implario.games5e.sdk.cristalix.MapLoader
import dev.implario.games5e.sdk.cristalix.WorldMeta
import dev.implario.kensuke.Kensuke
import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.Scope
import dev.implario.kensuke.UserManager
import dev.implario.kensuke.impl.bukkit.BukkitKensuke
import dev.implario.kensuke.impl.bukkit.BukkitUserManager
import dev.implario.platform.impl.darkpaper.PlatformDarkPaper
import me.func.box.bar.WaitingPlayers
import me.func.box.cosmetic.Starter
import me.func.box.data.BoxTeam
import me.func.box.data.Status
import me.func.box.listener.*
import me.func.box.map.Generator
import me.func.box.map.TradeMenu
import me.func.box.quest.ServerType
import me.func.mod.Anime
import me.func.mod.Kit
import me.func.mod.conversation.ModTransfer
import me.func.mod.util.after
import me.func.mod.util.listener
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_12_R1.EnumItemSlot
import org.bukkit.*
import org.bukkit.Bukkit.getWorld
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftLivingEntity
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.NameTagVisibility
import org.bukkit.scoreboard.Team
import ru.cristalix.core.CoreApi
import ru.cristalix.core.datasync.EntityDataParameters
import ru.cristalix.core.formatting.Color
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.RealmUpdatePackage
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartyService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.scoreboard.IScoreboardService
import ru.cristalix.core.scoreboard.ScoreboardService
import ru.cristalix.core.tab.ITabService
import ru.cristalix.core.tab.TabTextComponent
import ru.cristalix.core.text.TextFormat
import ru.cristalix.core.transfer.ITransferService
import ru.cristalix.core.transfer.TransferService
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.math.max
import kotlin.math.min

lateinit var app: Box
const val MAX_GAME_STREAK_COUNT = 8
var sessionDurability = System.getProperty("TIME", "4000").toInt()

class Box : JavaPlugin() {
    private val oldStatScope = Scope("boxll", Stat::class.java)
    private val statScope = Scope("box-newa", Stat::class.java)

    private lateinit var worldMeta: WorldMeta
    lateinit var spawn: Location
    lateinit var userManager: UserManager<User>
    lateinit var zero: Location
    lateinit var kensuke: Kensuke
    lateinit var serverType: ServerType
    var isLuckyGame = System.getenv("LUCKY").toInt() == 1
    var slots = System.getenv("SLOT").toInt()
    val winMoney = System.getenv("WIN_REWARD").toInt()
    val finalMoney = System.getenv("FINAL_REWARD").toInt()
    val killMoney = System.getenv("KILL_REWARD").toInt()
    var size = System.getenv("SIZE").toInt()
    private var teamSize = System.getenv("TEAM").toInt()
    var status = Status.STARTING
    val hub = "BOXL-2"
    var waitingBar = WaitingPlayers()
    var woodPickaxe = ItemStack(Material.WOOD_PICKAXE)
    var teams = listOf(
        BoxTeam(mutableListOf(), true, Color.RED, null, null),
        BoxTeam(mutableListOf(), true, Color.BLUE, null, null),
        BoxTeam(mutableListOf(), true, Color.GREEN, null, null),
        BoxTeam(mutableListOf(), true, Color.YELLOW, null, null)
    )
    var gameCounter = 0

    override fun onEnable() {
        Anime.include(Kit.EXPERIMENTAL, Kit.STANDARD)
        app = this

        B.plugin = this

        EntityDataParameters.register()
        teams = teams.dropLast(teams.size - teamSize)
        Platforms.set(PlatformDarkPaper())
        woodPickaxe = item {
            type = Material.WOOD_PICKAXE
            nbt("Unbreakable", 1)
        }
        userManager = BukkitUserManager(
            listOf(statScope, oldStatScope),
            { session: KensukeSession, context ->
                User(
                    session,
                    context.getData(statScope),
                    context.getData(oldStatScope),
                )
            },
            { user, context -> context.store(statScope, user.stat) }
        )

        // Загрузка карты
        loadMap()
        // Создание туши
        val guide = worldMeta.getLabel("guide")
        val arguments = guide.tag.split(" ")
        guide.yaw = arguments[0].toFloat()
        guide.pitch = arguments[1].toFloat()
        val mob = worldMeta.world.spawnEntity(
            guide, arrayListOf(
                EntityType.ZOMBIE, EntityType.WITHER_SKELETON, EntityType.SKELETON
            ).random()
        ) as LivingEntity
        mob.isCustomNameVisible = true
        mob.customName = "Моб-антистресс"
        val bedrock = CraftItemStack.asNMSCopy(item {
            type = Material.BEDROCK
        })
        val handle = (mob as CraftLivingEntity).handle
        handle.setSlot(EnumItemSlot.HEAD, bedrock)
        handle.setSlot(EnumItemSlot.OFFHAND, bedrock)
        handle.setSlot(EnumItemSlot.MAINHAND, bedrock)

        // Регистрация сервисов
        val core = CoreApi.get()
        core.registerService(IPartyService::class.java, PartyService(ISocketClient.get()))
        core.registerService(ITransferService::class.java, TransferService(ISocketClient.get()))
        core.registerService(IInventoryService::class.java, InventoryService())

        val realmService = IRealmService.get()

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        val id = realmService.currentRealmInfo.realmId.id
        info.status = RealmStatus.WAITING_FOR_PLAYERS
        info.extraSlots = 1
        info.maxPlayers = slots
        info.groupName = "Коробка#$id v.3.4.6"
        info.readableName = "Коробка#$id v.3.4.6"

        // Получает тип вервета
        with(info.realmId.realmName) {
            when {
                contains(ServerType.BOX1X4.address) -> serverType = ServerType.BOX1X4
                contains(ServerType.BOX4X4.address) -> serverType = ServerType.BOX4X4
                contains(ServerType.BOXLUCKY.address) -> serverType = ServerType.BOXLUCKY
                else -> serverType = ServerType.ANY
            }
        }

        kensuke = BukkitKensuke.setup(this)
        kensuke.addGlobalUserManager(userManager)
        kensuke.globalRealm = info.realmId.realmName
        userManager.isOptional = true
        // Регистрация обработчиков
        listener(
            BlockListener(),
            DefaultListener(),
            TradeMenu(),
            EnchantTable(),
            ShootBowListener(),
            FixAnvilRename(),
            TeamChange
        )

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
            app.teams.forEach {
                it.players.removeIf { player ->
                    val craftPlayer = Bukkit.getPlayer(player)
                    craftPlayer == null || !craftPlayer.isOnline
                }
            }

            when (status) {
                Status.STARTING -> {
                    if (time == Status.STARTING.lastSecond) {
                        // Обновление статуса реалма
                        val realm = IRealmService.get().currentRealmInfo
                        realm.status = RealmStatus.GAME_STARTED_RESTRICTED
                        ISocketClient.get().write(RealmUpdatePackage(RealmUpdatePackage.UpdateType.UPDATE, realm))
                        // Смена статуса игры и остановка счетчика игроков
                        status = Status.GAME
                        gameCounter++
                        // Заполнение команд
                        Bukkit.getOnlinePlayers().forEach { player ->
                            player.inventory.clear()
                            player.openInventory.topInventory.clear()
                            player.itemOnCursor = null
                            player.inventory.addItem(woodPickaxe)
                            player.sendTitle("§eПоехали!", "Враги без ника")
                            val user = app.getUser(player)!!
                            val starter = user.stat.currentStarter
                            if (starter != null && starter != Starter.NONE) {
                                if (starter == Starter.FUSE && slots > 20) player.sendMessage(Formatting.error("Данный стартовый набор недоступен в выбранном типе игры."))
                                else after(5 * 20) { starter.consumer(user.player!!) }
                            }
                            ModTransfer().integer(0).send("box:start", user.player)
                            user.stat.games++
                            waitingBar.removeViewer(player.uniqueId)

                            if (!teams.any { it.players.contains(player.uniqueId) })
                                teams.minByOrNull { it.players.size }!!.players.add(player.uniqueId)

                            // Скорборды
                            val address = UUID.randomUUID().toString()
                            val objective =
                                scoreboardService().getPlayerObjective(player.uniqueId, address)
                            objective.displayName = "Бедроковая коробка"
                            val group = objective.startGroup("Игра")
                            teams.forEach {
                                group.record { "" + it.color.chatColor + "■ §f" + it.color.teamName + (if (it.bed) " §a✔" else " §c" + it.players.size) }
                            }
                            group
                                .record { "" }
                                .record { "Убийств: §c" + user.tempKills }
                                .record { "Финальных: §6" + user.finalKills }
                                .record { "Кровать " + if (user.bed != null) "§a✔" else "§c䂄" }
                                .record {
                                    val pTime = sessionDurability + 10 - time
                                    "§7Авторестарт " + String.format("%02d:%02d", pTime / 60, pTime % 60)
                                }
                            scoreboardService().setCurrentObjective(player.uniqueId, address)
                        }
                        // Отпрака игроков по домам
                        after {
                            // Генерация комнат
                            Generator.generateRooms(zero, size, size, size, 10, 5)
                                .forEachIndexed { index, location ->
                                    teams[index].location = location
                                }
                            // Таб
                            val tabView = ITabService.get().createConstantTabView()
                            tabView.addPrefix(
                                TabTextComponent(
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
                                        CompletableFuture.completedFuture(
                                            ComponentBuilder(
                                                text
                                            ).create()
                                        )
                                    },
                                    { player ->
                                        CompletableFuture.completedFuture(
                                            teams.indexOfFirst {
                                                it.players.contains(
                                                    player
                                                )
                                            }
                                        )
                                    },
                                )
                            )
                            val tab = ITabService.get()
                            tab.enable()
                            teams.forEach { team ->
                                team.players.forEach {
                                    val player = Bukkit.getPlayer(it) ?: return@forEach
                                    player.itemOnCursor = null
                                    player.teleport(team.location)
                                    team.team!!.addEntry(player.name)
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
                    } else if (time == Status.STARTING.lastSecond - 10 && Bukkit.getOnlinePlayers().size + 2 < slots) {
                        time = 0
                        return@runTaskTimer
                    }
                    if (time == Status.STARTING.lastSecond - 10 && Bukkit.getOnlinePlayers().size + 2 >= slots) {
                        Generator.generateCube(zero, size, size, size)
                        return@runTaskTimer
                    }
                    if (Bukkit.getOnlinePlayers().size + 2 < slots) {
                        time = 0
                        return@runTaskTimer
                    }
                }
                Status.GAME -> {
                    // Если игра длиться дольше 20 минут включить ники
                    if (time == 20 * 60) {
                        app.teams.forEach { it.team!!.nameTagVisibility = NameTagVisibility.ALWAYS }
                    }
                    if (time == 27 * 60) {
                        Bukkit.getOnlinePlayers().forEach { player ->
                            if (player.gameMode == GameMode.SURVIVAL)
                                player.isGlowing = true
                        }
                    }
                    //Обновление компасов
                    Bukkit.getOnlinePlayers().forEach { player ->
                        if (player.inventory.contains(Material.COMPASS)) {
                            player.compassTarget = if (app.getUser(player)!!.compassToPlayer) {
                                val someone =
                                    teams.filter { it.players.size > 0 && !it.players.contains(player.uniqueId) }
                                        .map {
                                            it.players.minByOrNull { current ->
                                                Bukkit.getPlayer(current).location.distanceSquared(
                                                    player.location
                                                )
                                            }
                                        }.minByOrNull { current ->
                                            Bukkit.getPlayer(current).location.distanceSquared(
                                                player.location
                                            )
                                        }
                                if (someone != null)
                                    Bukkit.getPlayer(someone).location
                                else
                                    player.location
                            } else {
                                val teams = teams.filter { !it.players.contains(player.uniqueId) }
                                val tempBeds = teams.map {
                                    it.players.minByOrNull { current ->
                                        val enemyBed =
                                            app.getUser(current)?.bed ?: return@minByOrNull 999999.0
                                        enemyBed.distanceSquared(player.location)
                                    }
                                }.minByOrNull { current ->
                                    if (current == null)
                                        return@minByOrNull 99999.0
                                    val enemyBed = app.getUser(current)?.bed ?: return@minByOrNull 999999.0
                                    enemyBed.distanceSquared(player.location)
                                }
                                val permanentBed = teams.firstOrNull { it.bed }
                                if (tempBeds != null)
                                    Bukkit.getPlayer(tempBeds).location
                                else if (permanentBed != null)
                                    permanentBed.location
                                else
                                    player.location
                            }

                            val locationStatus = if (player.compassTarget.y > player.location.y) "§a↑"
                            else if (player.compassTarget.y < player.location.y) "§c↓"
                            else "§6↕"

                            if (player.itemInHand.getType() == Material.COMPASS) {
                                val distance = player.compassTarget?.distance(player.location) ?: 9999.0
                                player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    TextComponent("§aДо цели осталось: " + (if (distance > 1000.0) "ОШИБКА" else (distance.toInt().toString() + " " + locationStatus)))
                                )
                            }
                        }
                    }

                    if (time == sessionDurability)
                        status = Status.END
                    Winner.tryGetWinner()
                    // Идет игра
                }
                Status.END -> {
                    status = Status.CLOSE
                    // Рестарт игры
                    Bukkit.getOnlinePlayers().forEach { it.sendMessage(Formatting.error("Перезагрузка...")) }
                    waitingBar.updateMessage()
                    after(100) {
                        teams.forEach {
                            it.players.forEach { player ->
                                try {
                                    val find = Bukkit.getPlayer(player)
                                    if (find != null)
                                        it.team!!.removePlayer(find)
                                } catch (ignored: Exception) {
                                }
                            }
                            it.team!!.nameTagVisibility = NameTagVisibility.HIDE_FOR_OTHER_TEAMS
                            it.players.clear()
                            it.location = null
                            it.bed = true
                        }
                        getWorld().livingEntities.filter { it.hasMetadata("lucky") }.forEach { it.remove() }
                        Bukkit.unloadWorld(worldMeta.world, false)
                        time = 0
                        status = Status.STARTING
                        after(20) {
                            ITransferService.get().transferBatch(Bukkit.getOnlinePlayers().map { it.uniqueId }, RealmId.of(hub))

                            if (gameCounter == MAX_GAME_STREAK_COUNT) {
                                Bukkit.shutdown()
                            } else {
                                loadMap()
                                IRealmService.get().currentRealmInfo.status = RealmStatus.WAITING_FOR_PLAYERS
                            }
                        }
                    }
                }
            }
        }, 5, 20)

        B.regCommand(
            { player, strings ->
                if (player.isOp) {
                    slots = strings[0].toInt()
                }
                "Усновлено $slots слотов"
            }, "slot"
        )

        B.regCommand(
            { player, strings ->
                if (player.isOp) {
                    val arg = strings[0].toInt()
                    size = max(50, min(arg, 130))
                }
                "Усновлен размер $size"
            }, "size"
        )

        B.regCommand(
            { player, _ ->
                if (player.isOp) {
                    status = Status.END
                }
                "Игра будет прекращена"
            }, "end"
        )

        B.regCommand(
            { player, strings ->
                if (player.isOp) {
                    sessionDurability = strings[0].toInt()
                }
                "Максимальное время игры изменено"
            }, "time"
        )
    }

    fun getUser(player: Player): User? {
        return userManager.getUser(player.uniqueId)
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
        worldMeta = WorldMeta(MapLoader.load("Box", "prod"))
        spawn = worldMeta.getLabel("spawn").add(0.0, 2.0, 0.0).toCenterLocation()
        zero = Location(worldMeta.world, 0.0, 20.0, 0.0)
        Generator.generateContentOfCube(Location(worldMeta.world, 0.0, 20.0, 0.0), size, size, size)
    }
}