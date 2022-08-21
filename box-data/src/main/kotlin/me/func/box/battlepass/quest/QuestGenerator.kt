package me.func.box.battlepass.quest

import me.func.box.battlepass.ServerType

object QuestGenerator {

    private val easyQuests = listOf(
        BattlePassQuest("Устраните противников в любом режиме", QuestType.KILL, ServerType.ANY, 10),
        BattlePassQuest("Устраните противников в режиме 4x4", QuestType.KILL, ServerType.BOX4X4, 5),
        BattlePassQuest("Устраните противников в режиме 4x4 Lucky", QuestType.KILL, ServerType.BOXLUCKY, 5),
        BattlePassQuest("Устраните противников в режиме 1x4", QuestType.KILL, ServerType.BOX1X4, 5),

        BattlePassQuest("Одержите победу в любом режиме", QuestType.WIN, ServerType.ANY, 5),
        BattlePassQuest("Одержите победу в режиме 4x4", QuestType.WIN, ServerType.BOX4X4, 4),
        BattlePassQuest("Одержите победу в режиме 4x4 Lucky", QuestType.WIN, ServerType.BOXLUCKY, 4),
        BattlePassQuest("Одержите победу в режиме 1x4", QuestType.WIN, ServerType.BOX1X4, 4),

        BattlePassQuest("Сыграйте в любой режим", QuestType.PLAY, ServerType.ANY, 8),
        BattlePassQuest("Сыграйте в режиме 4x4", QuestType.PLAY, ServerType.BOX4X4, 6),
        BattlePassQuest("Сыграйте в режиме 4x4 Lucky", QuestType.PLAY, ServerType.BOXLUCKY, 6),
        BattlePassQuest("Сыграйте в режиме 1x4", QuestType.PLAY, ServerType.BOX1X4, 6),

        BattlePassQuest("Сломайте кровать противнику в любом режиме", QuestType.BEDBREAK, ServerType.ANY, 5),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4", QuestType.BEDBREAK, ServerType.BOX4X4, 4),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4 Lucky", QuestType.BEDBREAK, ServerType.BOXLUCKY, 4),
        BattlePassQuest("Сломайте кровать противнику в режиме 1x4", QuestType.BEDBREAK, ServerType.BOX1X4, 4),

        BattlePassQuest("Совершите покупку в любом режиме", QuestType.BUYITEMS, ServerType.ANY, 30),
        BattlePassQuest("Совершите покупку в режиме 4x4", QuestType.BUYITEMS, ServerType.BOX4X4, 24),
        BattlePassQuest("Совершите покупку в режиме 4x4 Lucky", QuestType.BUYITEMS, ServerType.BOXLUCKY, 24),
        BattlePassQuest("Совершите покупку в режиме 1x4", QuestType.BUYITEMS, ServerType.BOX1X4, 24),

        BattlePassQuest("Сломайте блоки в любом режиме", QuestType.BLOCKBREAK, ServerType.ANY, 300),
        BattlePassQuest("Сломайте блоки в режиме 4x4", QuestType.BLOCKBREAK, ServerType.BOX4X4, 150),
        BattlePassQuest("Сломайте блоки в режиме 4x4 Lucky", QuestType.BLOCKBREAK, ServerType.BOXLUCKY, 150),
        BattlePassQuest("Сломайте блоки в режиме 1x4", QuestType.BLOCKBREAK, ServerType.BOX1X4, 150)

    ).map { it.apply { exp = 35 } }

    private val rareQuests = listOf(
        BattlePassQuest("Устраните противников в любом режиме", QuestType.KILL, ServerType.ANY, 10 * 4),
        BattlePassQuest("Устраните противников в режиме 4x4", QuestType.KILL, ServerType.BOX4X4, 5 * 4),
        BattlePassQuest("Устраните противников в режиме 4x4 Lucky", QuestType.KILL, ServerType.BOXLUCKY, 5 * 4),
        BattlePassQuest("Устраните противников в режиме 1x4", QuestType.KILL, ServerType.BOX1X4, 5 * 4),

        BattlePassQuest("Одержите победу в любом режиме", QuestType.WIN, ServerType.ANY, 5 * 4),
        BattlePassQuest("Одержите победу в режиме 4x4", QuestType.WIN, ServerType.BOX4X4, 4 * 4),
        BattlePassQuest("Одержите победу в режиме 4x4 Lucky", QuestType.WIN, ServerType.BOXLUCKY, 4 * 4),
        BattlePassQuest("Одержите победу в режиме 1x4", QuestType.WIN, ServerType.BOX1X4, 4 * 4),

        BattlePassQuest("Сыграйте в любой режим", QuestType.PLAY, ServerType.ANY, 8 * 4),
        BattlePassQuest("Сыграйте в режиме 4x4", QuestType.PLAY, ServerType.BOX4X4, 6 * 4),
        BattlePassQuest("Сыграйте в режиме 4x4 Lucky", QuestType.PLAY, ServerType.BOXLUCKY, 6 * 4),
        BattlePassQuest("Сыграйте в режиме 1x4", QuestType.PLAY, ServerType.BOX1X4, 6 * 4),

        BattlePassQuest("Сломайте кровать противнику в любом режиме", QuestType.BEDBREAK, ServerType.ANY, 5 * 4),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4", QuestType.BEDBREAK, ServerType.BOX4X4, 4 * 4),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4 Lucky", QuestType.BEDBREAK, ServerType.BOXLUCKY, 4 * 4),
        BattlePassQuest("Сломайте кровать противнику в режиме 1x4", QuestType.BEDBREAK, ServerType.BOX1X4, 4 * 4),

        BattlePassQuest("Совершите покупку в любом режиме", QuestType.BUYITEMS, ServerType.ANY, 30 * 4),
        BattlePassQuest("Совершите покупку в режиме 4x4", QuestType.BUYITEMS, ServerType.BOX4X4, 24 * 4),
        BattlePassQuest("Совершите покупку в режиме 4x4 Lucky", QuestType.BUYITEMS, ServerType.BOXLUCKY, 24 * 4),
        BattlePassQuest("Совершите покупку в режиме 1x4", QuestType.BUYITEMS, ServerType.BOX1X4, 24 * 4),

        BattlePassQuest("Сломайте блоки в любом режиме", QuestType.BLOCKBREAK, ServerType.ANY, 300 * 4),
        BattlePassQuest("Сломайте блоки в режиме 4x4", QuestType.BLOCKBREAK, ServerType.BOX4X4, 150 * 4),
        BattlePassQuest("Сломайте блоки в режиме 4x4 Lucky", QuestType.BLOCKBREAK, ServerType.BOXLUCKY, 150 * 4),
        BattlePassQuest("Сломайте блоки в режиме 1x4", QuestType.BLOCKBREAK, ServerType.BOX1X4, 150 * 4)
    ).map { it.apply { exp = 70 } }

    private val specialQuests = listOf(
        BattlePassQuest("Устраните противников в любом режиме", QuestType.KILL, ServerType.ANY, 10 * 9),
        BattlePassQuest("Устраните противников в режиме 4x4", QuestType.KILL, ServerType.BOX4X4, 5 * 9),
        BattlePassQuest("Устраните противников в режиме 4x4 Lucky", QuestType.KILL, ServerType.BOXLUCKY, 5 * 9),
        BattlePassQuest("Устраните противников в режиме 1x4", QuestType.KILL, ServerType.BOX1X4, 5 * 9),

        BattlePassQuest("Одержите победу в любом режиме", QuestType.WIN, ServerType.ANY, 5 * 9),
        BattlePassQuest("Одержите победу в режиме 4x4", QuestType.WIN, ServerType.BOX4X4, 4 * 9),
        BattlePassQuest("Одержите победу в режиме 4x4 Lucky", QuestType.WIN, ServerType.BOXLUCKY, 4 * 9),
        BattlePassQuest("Одержите победу в режиме 1x4", QuestType.WIN, ServerType.BOX1X4, 4 * 9),

        BattlePassQuest("Сыграйте в любой режим", QuestType.PLAY, ServerType.ANY, 8 * 9),
        BattlePassQuest("Сыграйте в режиме 4x4", QuestType.PLAY, ServerType.BOX4X4, 6 * 9),
        BattlePassQuest("Сыграйте в режиме 4x4 Lucky", QuestType.PLAY, ServerType.BOXLUCKY, 6 * 9),
        BattlePassQuest("Сыграйте в режиме 1x4", QuestType.PLAY, ServerType.BOX1X4, 6 * 9),

        BattlePassQuest("Сломайте кровать противнику в любом режиме", QuestType.BEDBREAK, ServerType.ANY, 5 * 9),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4", QuestType.BEDBREAK, ServerType.BOX4X4, 4 * 9),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4 Lucky", QuestType.BEDBREAK, ServerType.BOXLUCKY, 4 * 9),
        BattlePassQuest("Сломайте кровать противнику в режиме 1x4", QuestType.BEDBREAK, ServerType.BOX1X4, 4 * 9),

        BattlePassQuest("Совершите покупку в любом режиме", QuestType.BUYITEMS, ServerType.ANY, 30 * 9),
        BattlePassQuest("Совершите покупку в режиме 4x4", QuestType.BUYITEMS, ServerType.BOX4X4, 24 * 9),
        BattlePassQuest("Совершите покупку в режиме 4x4 Lucky", QuestType.BUYITEMS, ServerType.BOXLUCKY, 24 * 9),
        BattlePassQuest("Совершите покупку в режиме 1x4", QuestType.BUYITEMS, ServerType.BOX1X4, 24 * 9),

        BattlePassQuest("Сломайте блоки в любом режиме", QuestType.BLOCKBREAK, ServerType.ANY, 300 * 9),
        BattlePassQuest("Сломайте блоки в режиме 4x4", QuestType.BLOCKBREAK, ServerType.BOX4X4, 150 * 9),
        BattlePassQuest("Сломайте блоки в режиме 4x4 Lucky", QuestType.BLOCKBREAK, ServerType.BOXLUCKY, 150 * 9),
        BattlePassQuest("Сломайте блоки в режиме 1x4", QuestType.BLOCKBREAK, ServerType.BOX1X4, 150 * 9)
    ).map { it.apply { exp = 140 } }

    fun generate() = listOf(
        easyQuests.random(),
        easyQuests.random(),
        easyQuests.random(),
        rareQuests.random(),
        rareQuests.random(),
        specialQuests.random(),
        easyQuests.random(),
        rareQuests.random()
    )

}