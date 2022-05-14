package me.func.box.cosmetic

import me.func.box.User
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack

/**
 * @author Рейдж 20.08.2021
 * @project box
 */
enum class BreakBedEffect(
    private val itemStack: ItemStack,
    private val price: Int,
    private val particle: Particle?,
    private val rare: Rare,
    private val title: String,
) : Donate {
    NONE(ItemStack(Material.BEDROCK), 0, null, Rare.COMMON, "Без эффекта"),
    SPELL_INSTANT(ItemStack(Material.FIREWORK), 39, Particle.SPELL_INSTANT, Rare.COMMON, "Фейрверк"),
    WATER_DROP(ItemStack(Material.WATER_BUCKET), 39, Particle.DRIP_WATER, Rare.COMMON, "Капли воды"),
    VILLAGER_HAPPY(ItemStack(Material.LIME_GLAZED_TERRACOTTA), 39, Particle.VILLAGER_HAPPY, Rare.COMMON, "Счастливый житель"),
    VILLAGER_ANGRY(ItemStack(Material.NETHER_STALK), 99, Particle.VILLAGER_ANGRY, Rare.RARE, "Злой житель"),
    SPELL_WITCH(ItemStack(Material.POTION), 99, Particle.SPELL_WITCH, Rare.RARE, "Колдунья"),
    SLIME(ItemStack(Material.SLIME_BALL), 99, Particle.SLIME, Rare.RARE, "Слизь"),
    REDSTONE(ItemStack(Material.REDSTONE), 99, Particle.REDSTONE, Rare.RARE, "Красный камень"),
    NOTE(ItemStack(Material.BOOK), 199, Particle.NOTE, Rare.LEGENDARY, "Ноты"),
    HEAR(ItemStack(Material.DIAMOND), 199, Particle.HEART, Rare.LEGENDARY, "Сердечки"),
    FALLING_DUST(ItemStack(Material.FLINT_AND_STEEL), 199, Particle.FALLING_DUST, Rare.LEGENDARY, "Падающая пыль"),
    LAVA(ItemStack(Material.LAVA_BUCKET), 199, Particle.LAVA, Rare.LEGENDARY, "Лава"), ;

    override fun getPrice(): Int {
        return price
    }

    override fun getTitle(): String {
        return title
    }

    override fun getRare(): Rare {
        return rare
    }

    override fun getCode(): String {
        return name
    }

    fun getItemStack() : ItemStack {
        return itemStack
    }

    fun getParticle() : Particle? {
        return particle
    }

    fun getItem(current: Boolean, has: Boolean): ItemStack {
        return dev.implario.bukkit.item.item {
            type = itemStack.getType()
            amount = 1
            text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + rare.color + rare.title + " \n§7Название: " + rare.color + title)
        }
    }

    override fun give(user: User) {
        user.stat.breakBedEffects.add(this)
        user.stat.currentBreakBedEffect = this
    }
}