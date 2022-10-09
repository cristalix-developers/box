package me.func.box.reward

import dev.implario.bukkit.item.item
import me.func.box.User
import me.func.box.cosmetic.Armor
import me.func.box.cosmetic.BreakBedEffect
import me.func.box.cosmetic.Donate
import me.func.mod.util.nbt
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

enum class WeekRewards(val title: String, val icon: ItemStack, val give: (User) -> Any) {
    ONE("§e250 монет", item { type = Material.GOLD_INGOT }, { it.giveMoney(250) }),
    TWO("§aКостюм кактуса", Armor.CACTUS.getIcon(), { it.stat.skins!!.add(Armor.CACTUS.getCode()) }),
    THREE("§dСообщ. о убийстве - Галактический", ItemStack(Material.ENDER_PEARL),
        {
            withDuplicate(it, 1000, me.func.box.cosmetic.KillMessage.GALACTIC) { user, donate ->
                user.stat.killMessages.contains(donate)
            }
        }
    ),
    FOUR("§dЭффект разрушения Колдунья", BreakBedEffect.SPELL_WITCH.getIcon(), {
        withDuplicate(it, 2000, BreakBedEffect.SPELL_WITCH) { user, donate ->
            user.stat.breakBedEffects.contains(donate)
        }
    }),
    FIVE("§bЛутбокс", item { type = Material.CLAY_BALL }.nbt("other", "enderchest1"),
        { me.func.box.donate.Lootbox.open(it) }
    ),
    SIX("§e5`000 монет", item {
        type = Material.GOLD_INGOT
        enchant(Enchantment.DAMAGE_ALL, 1)
    }, { it.giveMoney(5000) }),
    SEVEN("§bЛутбокс §f+ §e5`000 монет", item {
        type = Material.CLAY_BALL
        enchant(Enchantment.DAMAGE_ALL, 1)
    }.nbt("other", "enderchest1"),
        {
            me.func.box.donate.Lootbox.open(it)
            it.giveMoney(5000)
        }
    ), ;

    companion object {
        fun withDuplicate(user: User, reward: Int, donate: Donate, contains: (User, Donate) -> Boolean) {
            if (contains(user, donate)) user.giveMoney(reward)
            else donate.give(user)
        }
    }
}