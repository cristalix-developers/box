package me.func.box

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
) :
    Donate {

    NONE(ItemStack(Material.BEDROCK), 0, Rare.COMMON, "Стандартное сообщение", "убит игроком"),
    FIRE(ItemStack(Material.BLAZE_POWDER), 29, Rare.COMMON, "Огонь", "был превращён в пыль игроком"),
    WILD_WEST(ItemStack(Material.BOW),29, Rare.COMMON, "Дикий запад", "был превращён в свинец благодарю игроку"),
    PIRATE(ItemStack(Material.BONE), 29, Rare.COMMON, "Пират", "был отправлен в рундуке Дэйви Джонса игроком"),
    GALACTIC(ItemStack(Material.ENDER_STONE), 29, Rare.COMMON, "Галактический", "был превращён в космическую пыль игроком"),
    COMPUTER(ItemStack(Material.RECORD_7), 29, Rare.COMMON, "Компьютер", "был удалён игроком"),
    HONORABLE(ItemStack(Material.DIAMOND), 29, Rare.COMMON, "Почётный", "умер в ближнем бою против"),
    BARBECUE(ItemStack(Material.COOKED_BEEF),39, Rare.RARE, "Барбекю", "был измазан в соусе барбекю игроком"),
    INSECT(ItemStack(Material.RED_MUSHROOM), 39, Rare.RARE, "Насекомое", "был истреблён игроком"),
    BANANA(ItemStack(Material.GOLD_INGOT), 39, Rare.RARE, "Банан", "был очищен от кожуры игроком"),
    BUZZ(ItemStack(Material.CHORUS_FLOWER), 39, Rare.RARE, "Жужжание", "был зажужжан до смерти игроком"),
    BOX(ItemStack(Material.REDSTONE_BLOCK), 39, Rare.RARE, "Бокс", "принял апперкот от"),
    FORGOTTEN(ItemStack(Material.REDSTONE), 39, Rare.RARE, "Забычен", "был растоптан"),
    MIDDLE_AGES(ItemStack(Material.IRON_HELMET), 49, Rare.LEGENDARY, "Средневековье", "был убит рыцарем"),
    GAV(ItemStack(Material.LEASH),49, Rare.LEGENDARY, "ГАВ", "был укушен игроком"),
    JAMMED(ItemStack(Material.SLIME_BLOCK), 49, Rare.LEGENDARY, "Замемлен", "был задэблен игроком"),
    ;

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

    fun getItem(current: Boolean, has: Boolean): org.bukkit.inventory.ItemStack {
        return dev.implario.bukkit.item.item {
            type = itemStack.getType()
            amount = 1
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title)
        }.build()
    }
}