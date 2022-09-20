package me.func.box.quest

object QuestGenerator {

    private val easyQuests: List<BattlePassQuest> = listOf(
        BattlePassQuest("Устраните противников в любом режиме", ServerType.ANY, QuestType.KILL, 5),
        BattlePassQuest("Устраните противников в режиме 4x4", ServerType.BOX4X4, QuestType.KILL, 2),
        BattlePassQuest("Устраните противников в режиме 4x4 Lucky", ServerType.BOX8, QuestType.KILL, 2),
        BattlePassQuest("Устраните противников в режиме 1x4", ServerType.BOX1X4, QuestType.KILL, 2),

        BattlePassQuest("Одержите победу в любом режиме", ServerType.ANY, QuestType.WIN, 2),
        BattlePassQuest("Одержите победу в режиме 4x4", ServerType.BOX4X4, QuestType.WIN, 1),
        BattlePassQuest("Одержите победу в режиме 4x4 Lucky", ServerType.BOX8, QuestType.WIN, 1),
        BattlePassQuest("Одержите победу в режиме 1x4", ServerType.BOX1X4, QuestType.WIN, 1),

        BattlePassQuest("Сыграйте в любой режим", ServerType.ANY, QuestType.PLAY, 3),
        BattlePassQuest("Сыграйте в режиме 4x4", ServerType.BOX4X4, QuestType.PLAY, 2),
        BattlePassQuest("Сыграйте в режиме 4x4 Lucky", ServerType.BOX8, QuestType.PLAY, 2),
        BattlePassQuest("Сыграйте в режиме 1x4", ServerType.BOX1X4, QuestType.PLAY, 2),

        BattlePassQuest("Сломайте кровать противнику в любом режиме", ServerType.ANY, QuestType.BEDBREAK, 2),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4", ServerType.BOX4X4, QuestType.BEDBREAK, 1),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BEDBREAK, 1),
        BattlePassQuest("Сломайте кровать противнику в режиме 1x4", ServerType.BOX1X4, QuestType.BEDBREAK, 1),

        BattlePassQuest("Совершите покупку в любом режиме", ServerType.ANY, QuestType.BUYITEMS, 15),
        BattlePassQuest("Совершите покупку в режиме 4x4", ServerType.BOX4X4, QuestType.BUYITEMS, 10),
        BattlePassQuest("Совершите покупку в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BUYITEMS, 10),
        BattlePassQuest("Совершите покупку в режиме 1x4", ServerType.BOX1X4, QuestType.BUYITEMS, 10),

        BattlePassQuest("Сломайте блоки в любом режиме", ServerType.ANY, QuestType.BLOCKBREAK, 100),
        BattlePassQuest("Сломайте блоки в режиме 4x4", ServerType.BOX4X4, QuestType.BLOCKBREAK, 70),
        BattlePassQuest("Сломайте блоки в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BLOCKBREAK, 70),
        BattlePassQuest("Сломайте блоки в режиме 1x4", ServerType.BOX1X4, QuestType.BLOCKBREAK, 70)

    ).map { it.apply { exp = 35 } }

    private val rareQuests: List<BattlePassQuest> = listOf(
        BattlePassQuest("Устраните противников в любом режиме", ServerType.ANY, QuestType.KILL, 5 * 4),
        BattlePassQuest("Устраните противников в режиме 4x4", ServerType.BOX4X4, QuestType.KILL, 2 * 4),
        BattlePassQuest("Устраните противников в режиме 4x4 Lucky", ServerType.BOX8, QuestType.KILL, 2 * 2),
        BattlePassQuest("Устраните противников в режиме 1x4", ServerType.BOX1X4, QuestType.KILL, 2 * 4),

        BattlePassQuest("Одержите победу в любом режиме", ServerType.ANY, QuestType.WIN, 2 * 4),
        BattlePassQuest("Одержите победу в режиме 4x4", ServerType.BOX4X4, QuestType.WIN, 1 * 4),
        BattlePassQuest("Одержите победу в режиме 4x4 Lucky", ServerType.BOX8, QuestType.WIN, 1 * 2),
        BattlePassQuest("Одержите победу в режиме 1x4", ServerType.BOX1X4, QuestType.WIN, 1 * 4),

        BattlePassQuest("Сыграйте в любой режим", ServerType.ANY, QuestType.PLAY, 3 * 4),
        BattlePassQuest("Сыграйте в режиме 4x4", ServerType.BOX4X4, QuestType.PLAY, 2 * 4),
        BattlePassQuest("Сыграйте в режиме 4x4 Lucky", ServerType.BOX8, QuestType.PLAY, 2 * 2),
        BattlePassQuest("Сыграйте в режиме 1x4", ServerType.BOX1X4, QuestType.PLAY, 2 * 4),

        BattlePassQuest("Сломайте кровать противнику в любом режиме", ServerType.ANY, QuestType.BEDBREAK, 2 * 4),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4", ServerType.BOX4X4, QuestType.BEDBREAK, 1 * 4),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BEDBREAK, 1 * 2),
        BattlePassQuest("Сломайте кровать противнику в режиме 1x4", ServerType.BOX1X4, QuestType.BEDBREAK, 1 * 4),

        BattlePassQuest("Совершите покупку в любом режиме", ServerType.ANY, QuestType.BUYITEMS, 15 * 4),
        BattlePassQuest("Совершите покупку в режиме 4x4", ServerType.BOX4X4, QuestType.BUYITEMS, 10 * 4),
        BattlePassQuest("Совершите покупку в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BUYITEMS, 10 * 2),
        BattlePassQuest("Совершите покупку в режиме 1x4", ServerType.BOX1X4, QuestType.BUYITEMS, 10 * 4),

        BattlePassQuest("Сломайте блоки в любом режиме", ServerType.ANY, QuestType.BLOCKBREAK, 100 * 4),
        BattlePassQuest("Сломайте блоки в режиме 4x4", ServerType.BOX4X4, QuestType.BLOCKBREAK, 70 * 4),
        BattlePassQuest("Сломайте блоки в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BLOCKBREAK, 70 * 2),
        BattlePassQuest("Сломайте блоки в режиме 1x4", ServerType.BOX1X4, QuestType.BLOCKBREAK, 70 * 4)
    ).map { it.apply { exp = 70 } }

    private val specialQuests: List<BattlePassQuest> = listOf(
        BattlePassQuest("Устраните противников в любом режиме", ServerType.ANY, QuestType.KILL, 5 * 9),
        BattlePassQuest("Устраните противников в режиме 4x4", ServerType.BOX4X4, QuestType.KILL, 2 * 9),
        BattlePassQuest("Устраните противников в режиме 4x4 Lucky", ServerType.BOX8, QuestType.KILL, 2 * 6),
        BattlePassQuest("Устраните противников в режиме 1x4", ServerType.BOX1X4, QuestType.KILL, 2 * 9),

        BattlePassQuest("Одержите победу в любом режиме", ServerType.ANY, QuestType.WIN, 2 * 9),
        BattlePassQuest("Одержите победу в режиме 4x4", ServerType.BOX4X4, QuestType.WIN, 1 * 9),
        BattlePassQuest("Одержите победу в режиме 4x4 Lucky", ServerType.BOX8, QuestType.WIN, 1 * 6),
        BattlePassQuest("Одержите победу в режиме 1x4", ServerType.BOX1X4, QuestType.WIN, 1 * 9),

        BattlePassQuest("Сыграйте в любой режим", ServerType.ANY, QuestType.PLAY, 3 * 9),
        BattlePassQuest("Сыграйте в режиме 4x4", ServerType.BOX4X4, QuestType.PLAY, 2 * 9),
        BattlePassQuest("Сыграйте в режиме 4x4 Lucky", ServerType.BOX8, QuestType.PLAY, 2 * 6),
        BattlePassQuest("Сыграйте в режиме 1x4", ServerType.BOX1X4, QuestType.PLAY, 2 * 9),

        BattlePassQuest("Сломайте кровать противнику в любом режиме", ServerType.ANY, QuestType.BEDBREAK, 2 * 9),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4", ServerType.BOX4X4, QuestType.BEDBREAK, 1 * 9),
        BattlePassQuest("Сломайте кровать противнику в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BEDBREAK, 1 * 6),
        BattlePassQuest("Сломайте кровать противнику в режиме 1x4", ServerType.BOX1X4, QuestType.BEDBREAK, 1 * 9),

        BattlePassQuest("Совершите покупку в любом режиме", ServerType.ANY, QuestType.BUYITEMS, 15 * 9),
        BattlePassQuest("Совершите покупку в режиме 4x4", ServerType.BOX4X4, QuestType.BUYITEMS, 0, 10 * 9),
        BattlePassQuest("Совершите покупку в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BUYITEMS, 10 * 6),
        BattlePassQuest("Совершите покупку в режиме 1x4", ServerType.BOX1X4, QuestType.BUYITEMS, 10 * 9),

        BattlePassQuest("Сломайте блоки в любом режиме", ServerType.ANY, QuestType.BLOCKBREAK, 100 * 9),
        BattlePassQuest("Сломайте блоки в режиме 4x4", ServerType.BOX4X4, QuestType.BLOCKBREAK, 70 * 9),
        BattlePassQuest("Сломайте блоки в режиме 4x4 Lucky", ServerType.BOX8, QuestType.BLOCKBREAK, 70 * 6),
        BattlePassQuest("Сломайте блоки в режиме 1x4", ServerType.BOX1X4, QuestType.BLOCKBREAK, 70 * 9)
    ).map { it.apply { exp = 140 } }

    private fun less(target: MutableList<BattlePassQuest>, src: List<BattlePassQuest>) {
        target.add(src.filter { !target.contains(it) }.random())
    }

    fun generate() = mutableListOf<BattlePassQuest>().apply {
        repeat(4) { less(this, easyQuests) }
        repeat(3) { less(this, rareQuests) }
        less(this, specialQuests)
    }
}