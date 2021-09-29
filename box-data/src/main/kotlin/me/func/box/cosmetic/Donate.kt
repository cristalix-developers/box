package me.func.box.cosmetic

import me.func.box.User

interface Donate {

    fun getPrice(): Int

    fun getTitle(): String

    fun getCode(): String

    fun getRare(): Rare

    fun give(user: User)

}