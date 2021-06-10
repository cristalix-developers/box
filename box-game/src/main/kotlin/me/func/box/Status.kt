package me.func.box

enum class Status(val title: String, val lastSecond: Int) {
    STARTING("Набор игроков", 30),
    GAME("Игра", 10000),
    END("Перезагрузка", 10010),
}