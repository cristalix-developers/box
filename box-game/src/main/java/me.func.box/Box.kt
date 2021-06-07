package me.func.box

import clepto.bukkit.B
import clepto.cristalix.WorldMeta
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ru.cristalix.core.CoreApi
import ru.cristalix.core.inventory.IInventoryService
import ru.cristalix.core.inventory.InventoryService
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.stats.IStatService
import ru.cristalix.core.stats.PlayerScope
import ru.cristalix.core.stats.UserManager
import ru.cristalix.core.stats.impl.StatService
import ru.cristalix.core.stats.impl.network.StatServiceConnectionData


lateinit var app: Box

class Box : JavaPlugin() {

    private val statScope = PlayerScope("box", Stat::class.java)

    private lateinit var worldMeta: WorldMeta
    lateinit var userManager: UserManager<User>
    private var status = Status.STARTING

    override fun onEnable() {
        B.plugin = this
        app = this

        // Загрузка карты
        worldMeta = MapLoader().load("prod")!!

        // Конфигурация реалма
        val info = IRealmService.get().currentRealmInfo
        info.status = RealmStatus.STARTING_GAME
        info.extraSlots = 30
        info.maxPlayers = 30
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

        B.events(BlockListener())

        var time = 0

        Bukkit.getScheduler().runTaskTimer(this, {
            time++

            status = Status.values().find { it.lastSecond == time } ?: status

            when (status) {
                Status.STARTING -> {
                    if (time == 29 && Bukkit.getOnlinePlayers().size < 8) {
                        time = 9
                        return@runTaskTimer
                    }
                    // Игра начинается
                    time = 31
                }
                Status.GAME -> {
                    // Идет игра
                }
                Status.END -> {
                    // Рестарт игры
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