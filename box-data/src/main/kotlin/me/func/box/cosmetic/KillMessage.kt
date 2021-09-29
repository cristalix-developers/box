package me.func.box.cosmetic

import me.func.box.User
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * @author Рейдж 20.08.2021
 * @project box
 */
enum class KillMessage(
    private val itemStack: ItemStack,
    private val price: Int,
    private val rare: Rare,
    private val title: String,
    private val description: String
) : Donate {
    NONE(ItemStack(Material.BEDROCK), 0, Rare.COMMON, "Стандартное сообщение", "убит"),
    FIRE(ItemStack(Material.BLAZE_POWDER), 29, Rare.COMMON, "Огонь", "превращён в пыль"),
    GALACTIC(ItemStack(Material.ENDER_STONE), 29, Rare.COMMON, "Галактический", "превращен в космическую пыль"),
    COMPUTER(ItemStack(Material.RECORD_7), 29, Rare.COMMON, "Компьютер", "был удалён"),
    HONORABLE(ItemStack(Material.DIAMOND), 29, Rare.COMMON, "Почётный", "завоеван"),
    BARBECUE(ItemStack(Material.COOKED_BEEF),39, Rare.RARE, "Барбекю", "измазан в соусе барбекю"),
    INSECT(ItemStack(Material.RED_MUSHROOM), 39, Rare.RARE, "Насекомое", "истреблён"),
    BANANA(ItemStack(Material.GOLD_INGOT), 39, Rare.RARE, "Банан", "очищен от кожуры"),
    BUZZ(ItemStack(Material.CHORUS_FLOWER), 39, Rare.RARE, "Жужжание", "зажужжан до смерти"),
    FORGOTTEN(ItemStack(Material.REDSTONE), 59, Rare.RARE, "Забычен", "растоптан"),
    MIDDLE_AGES(ItemStack(Material.IRON_HELMET), 79, Rare.LEGENDARY, "Средневековье", "прибит"),
    GAV(ItemStack(Material.LEASH),99, Rare.LEGENDARY, "Кусь", "укушен"),
    JAMMED(ItemStack(Material.SLIME_BLOCK), 99, Rare.LEGENDARY, "Замемлен", "был замемлен"), ;

    override fun getCode(): String {
        return name
    }

    override fun getPrice(): Int {
        return price
    }

    override fun getTitle(): String {
        return title
    }

    override fun getRare(): Rare {
        return rare
    }

    fun getDescription(): String {
        return description
    }

    fun getItemStack() : ItemStack {
        return itemStack
    }

    fun getItem(current: Boolean, has: Boolean): ItemStack {
        return dev.implario.bukkit.item.item {
            type = itemStack.getType()
            amount = 1
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title + "\n§7Пример: \n§dFunc ${getDescription()} reidj")
        }.build()
    }

    override fun give(user: User) {
        user.stat.killMessages.add(this)
        user.stat.currentKillMessage = this
    }
}