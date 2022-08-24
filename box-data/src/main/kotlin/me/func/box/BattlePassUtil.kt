package me.func.box

import implario.humanize.Humanize
import me.func.box.quest.QuestGenerator
import me.func.box.quest.QuestType
import me.func.box.quest.ServerType
import me.func.mod.Anime
import me.func.protocol.ActionLog
import me.func.protocol.LogPacket
import ru.cristalix.core.network.ISocketClient

const val BATTLEPASS_RECHARGE_HOURS = 12

object BattlePassUtil {
    @JvmStatic
    fun update(user: User, type: QuestType, value: Int, absolute: Boolean = false, serverType: ServerType) {
        user.let { data ->
            val player = user.player ?: return

            data.stat.data?.find { it.serverType == serverType && it.questType == type }?.let {
                if (it.goal <= it.now)
                    return
                if ((data.stat.data?.indexOf(it) ?: 0) > 5 && data.stat.progress?.advanced != true)
                    return

                if (absolute) it.now = value
                else it.now += value

                if (it.goal <= it.now) {
                    Anime.topMessage(user.player!!, "§lЗадание выполнено! §6Награда: §b${it.exp} опыта §6баттлпасса")
                    ISocketClient.get().write(LogPacket(
                        player.uniqueId,
                        ActionLog.QUEST,
                        "Игрок выполнил квест ${type.name}/${it.goal}, получил ${it.exp} опыта"
                    ))
                    data.stat.progress!!.exp += it.exp
                }
            }
        }
    }

    @JvmStatic
    fun getQuestLore(user: User): List<String> {
        user.let { dataUser ->
            val now = System.currentTimeMillis()

            if (now - dataUser.stat.lastGenerationTime > 1000 * 60 * 60 * BATTLEPASS_RECHARGE_HOURS) {
                dataUser.stat.apply {
                    data = QuestGenerator.generate()
                    lastGenerationTime = now
                }
            }

            val minutes =
                (BATTLEPASS_RECHARGE_HOURS * 60 - (System.currentTimeMillis() - dataUser.stat.lastGenerationTime) / 1000 / 60).toInt()
            val hours = minutes / 60

            return listOf(
                "Ваши задания на сегодня (Обновление через $hours ${
                    Humanize.plurals("час", "часа", "часов", hours)
                } ${minutes % 60} ${
                    Humanize.plurals("минута", "минуты", "минут", minutes % 60)
                }):"
            ).plus(dataUser.stat.data!!.mapIndexed { index, quest -> " - " + quest.getLore((dataUser.stat.progress?.advanced ?: false) || index < 6) })
        }
    }
}