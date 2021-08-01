package me.func.box.listener.lucky

import me.func.box.User

enum class LuckEvent(val luckyConsumer: (User) -> Any) {

    TEST({ it.player!!.sendMessage("Что-то происходит") }),;

    fun accept(user: User) {
        luckyConsumer(user)
    }

}