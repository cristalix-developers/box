package me.func.box.cosmetic

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.inventory.ClickableItem
import ru.cristalix.core.inventory.InventoryContents
import sun.audio.AudioPlayer.player

object SeasonKit : Donate {

    private val starter = Starter.SNOW
    private val armor = Armor.SNOW
    private val sword = Sword.SNOW
    const val seasonCounter = 5

    override fun getPrice(): Int {
        return 149
    }

    override fun getTitle(): String {
        TODO("Not yet implemented")
    }

    override fun getCode(): String {
        TODO("Not yet implemented")
    }

    override fun getRare(): Rare {
        TODO("Not yet implemented")
    }

    override fun give(user: User) {
        if (user.stat.swords == null) user.stat.swords = arrayListOf(sword)
        else user.stat.swords!!.add(sword)
        user.stat.currentSword = sword
        if (user.stat.starters == null) user.stat.starters = arrayListOf(starter)
        else user.stat.starters!!.add(starter)
        user.stat.currentStarter = starter
        if (user.stat.skins == null) user.stat.skins = arrayListOf(armor.getCode())
        else user.stat.skins!!.add(armor.getCode())
        user.stat.currentSkin = armor.getCode()
    }

    fun fill(contents: InventoryContents) {
        contents.setLayout(
            "XXHHHXXOP",
        )
        contents.add('H', ClickableItem.empty(starter.getItem()))
        contents.add('H', ClickableItem.empty(armor.getItem()))
        contents.add('H', ClickableItem.empty(sword.getItem()))
        contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
    }
}