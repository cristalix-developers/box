package me.func.box

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import io.netty.buffer.Unpooled
import me.func.box.reward.DailyRewardManager
import me.func.box.reward.WeekRewards
import net.minecraft.server.v1_12_R1.PacketDataSerializer
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload
import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import ru.cristalix.core.display.DisplayChannels
import ru.cristalix.core.display.messages.Mod
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.realm.IRealmService
import java.io.File
import java.nio.file.Files
import java.util.*


class FamousListener : Listener {

    private val realmService = IRealmService.get()

    private var modList = try {
        File("./mods/").listFiles()!!
            .filter { it.name.contains("bundle") }
            .map {
                val buffer = Unpooled.buffer()
                buffer.writeBytes(Mod.serialize(Mod(Files.readAllBytes(it.toPath()))))
                buffer
            }.toList()
    } catch (exception: Exception) {
        throw RuntimeException(exception)
    }

    @EventHandler
    fun PlayerJoinEvent.handle() {
        val user = app.getUser(player)

        B.postpone(2) {
            player.allowFlight = true
            player.teleport(app.spawn)

            modList.forEach {
                user.sendPacket(
                    PacketPlayOutCustomPayload(
                        DisplayChannels.MOD_CHANNEL,
                        PacketDataSerializer(it.retainedSlice())
                    )
                )
            }

            val stat = user.stat
            stat.lastSeenName = Cristalix.getDisplayName(player)
            val address = UUID.randomUUID().toString()
            val objective =
                Cristalix.scoreboardService().getPlayerObjective(player.uniqueId, address)
            objective.displayName = "Бедроковая коробка"
            objective.startGroup("Статистика")
                .record("Монет") { "§e" + stat.money }
                .record("Убийств") { "§c" + stat.kills }
                .record("Смертей") { "" + stat.deaths }
                .record("Побед") { "§b" + stat.wins }
                .record("Игр") { "§d" + stat.games }
            objective.startGroup("Сервер")
                .record("Онлайн") {
                    (realmService.getOnlineOnRealms("BOX4") +
                            realmService.getOnlineOnRealms("BOX8") +
                            realmService.getOnlineOnRealms("BOX5") +
                            realmService.getOnlineOnRealms("BOXL") +
                            realmService.getOnlineOnRealms("BOXE") +
                            realmService.getOnlineOnRealms("BOXN") +
                            realmService.getOnlineOnRealms("BOXS")).toString()
                }
            Cristalix.scoreboardService().setCurrentObjective(player.uniqueId, address)
        }

        B.postpone(10) {
            val now = System.currentTimeMillis()
            // Обнулить комбо сбора наград если прошло больше суток или комбо >7
            if ((user.stat.rewardStreak > 0 && now - user.stat.lastEnter > 24 * 60 * 60 * 1000) || user.stat.rewardStreak > 6) {
                user.stat.rewardStreak = 0
            }
            if (now - user.stat.dailyClaimTimestamp > 14 * 60 * 60 * 1000) {
                user.stat.dailyClaimTimestamp = now
                DailyRewardManager.open(user, false)

                val dailyReward = WeekRewards.values()[user.stat.rewardStreak]
                player.sendMessage(Formatting.fine("Ваша ежедневная награда: " + dailyReward.title))
                dailyReward.give(user)
                user.stat.rewardStreak++
            }
            user.stat.lastEnter = now
        }

        player.gameMode = GameMode.ADVENTURE
    }

    @EventHandler
    fun PlayerMoveEvent.handle() {
        if (app.spawn.distanceSquared(to) > 70 * 70)
            player.teleport(app.spawn)
    }

}