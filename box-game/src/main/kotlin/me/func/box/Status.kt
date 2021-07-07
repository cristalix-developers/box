package me.func.box

enum class Status(val title: String, val lastSecond: Int) {
    STARTING("Набор игроков", 30),
    GAME("Игра", 4000),
    END("Перезагрузка", 4010),
    CLOSE("Закрытие", 4011),
}