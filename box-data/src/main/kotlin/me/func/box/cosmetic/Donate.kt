package me.func.box.cosmetic

import me.func.box.User
import org.bukkit.inventory.ItemStack

interface Donate {

    fun getPrice(): Int

    fun getTitle(): String

    fun getCode(): String

    fun getRare(): Rare

    fun getIcon() : ItemStack

    fun give(user: User)
}