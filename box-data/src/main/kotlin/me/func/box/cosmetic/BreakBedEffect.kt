package me.func.box.cosmetic

import dev.implario.bukkit.item.item
import me.func.box.User
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.inventory.ItemStack

/**
 * @author Рейдж 20.08.2021
 * @project box
 */
enum class BreakBedEffect(
    private val price: Int,
    private val particle: Particle?,
    private val rare: Rare,
    private val title: String,
    private val itemStack: ItemStack
) : Donate {
    NONE(0, null, Rare.COMMON, "Без эффекта", item {
        type = Material.BEDROCK
    }),
    SPELL_INSTANT(39, Particle.SPELL_INSTANT, Rare.COMMON, "Фейерверк", item {
        type = Material.FIREWORK
    }),
    WATER_DROP(39, Particle.DRIP_WATER, Rare.COMMON, "Капли воды", item {
        type = Material.WATER_BUCKET
    }),
    VILLAGER_HAPPY(39, Particle.VILLAGER_HAPPY, Rare.COMMON, "Счастливый житель", item {
        type = Material.LIME_GLAZED_TERRACOTTA
    }),
    VILLAGER_ANGRY(99, Particle.VILLAGER_ANGRY, Rare.RARE, "Злой житель", item {
        type = Material.NETHER_STALK
    }),
    SPELL_WITCH(99, Particle.SPELL_WITCH, Rare.RARE, "Колдунья", item {
        type = Material.POTION
    }),
    SLIME(99, Particle.SLIME, Rare.RARE, "Слизь", item {
        type = Material.SLIME_BALL
    }),
    REDSTONE(99, Particle.REDSTONE, Rare.RARE, "Красный камень", item {
        type = Material.REDSTONE
        text("Красный камень")
    }),
    NOTE(199, Particle.NOTE, Rare.LEGENDARY, "Ноты", item {
        type = Material.BOOK
    }),
    HEAR(199, Particle.HEART, Rare.LEGENDARY, "Сердечки", item {
        type = Material.DIAMOND
    }),
    FALLING_DUST(199, Particle.FALLING_DUST, Rare.LEGENDARY, "Падающая пыль", item {
        type = Material.FLINT_AND_STEEL
    }),
    LAVA(199, Particle.LAVA, Rare.LEGENDARY, "Лава", item {
        type = Material.LAVA_BUCKET
    });

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
        return ""
    }

    override fun getIcon() : ItemStack = item {
        type = itemStack.getType()
        text("${getRare().getColored()}§f эффект разрушения кровати $title")
        nbt("rare", rare.ordinal)
    }

    fun getParticle() : Particle? {
        return particle
    }

    override fun give(user: User) {
        user.stat.breakBedEffects.add(this)
        user.stat.currentBreakBedEffect = this
    }
}