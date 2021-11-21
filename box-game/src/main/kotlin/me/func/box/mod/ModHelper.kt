package me.func.box.mod

import me.func.box.User

object ModHelper {

    fun glow(user: User, red: Int, blue: Int, green: Int) {
        ModTransfer()
            .integer(red)
            .integer(blue)
            .integer(green)
            .send("func:glow", user)
    }
}