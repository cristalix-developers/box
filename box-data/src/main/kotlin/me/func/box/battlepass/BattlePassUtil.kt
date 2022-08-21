package me.func.box.battlepass

import implario.humanize.Humanize
import me.func.box.User
import me.func.box.battlepass.quest.QuestGenerator
import me.func.box.battlepass.quest.QuestType
import me.func.mod.Anime

val BATTLEPASS_RECHARGE_HOURS = 12

object BattlePassUtil {
    @JvmStatic
    fun update(user: User, type: QuestType, value: Int, absolute: Boolean = false, serverType: ServerType) {
        user.let { data ->
            data.stat.data?.find { it.server == serverType && it.questType == type }?.let {
                if (it.goal <= it.now)
                    return
                if ((data.stat.data?.indexOf(it) ?: 0) > 5 && data.stat.progress?.advanced != true)
                    return

                if (absolute) it.now = value
                else it.now += value

                if (it.goal <= it.now) {
                    Anime.topMessage(user.player!!, "§lЗадание выполнено! §6Награда: §b${it.exp} опыта §6баттлпасса")
                    BattlePassLog.log(user.player!!.uniqueId, TypeLog.QUEST,
                        "Игрок выполнил квест ${type.name}/${it.goal}. ServerType - ${serverType.name}, получил ${it.exp} опыта")
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