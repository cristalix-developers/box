package me.func.box.data

enum class Status(val title: String, val lastSecond: Int) {
    STARTING("Набор игроков", 30),
    GAME("Игра", 20000),
    END("Перезагрузка", 20010),
    CLOSE("Закрытие", 20011),
}