package me.func.box.listener.lucky

import me.func.box.User
import org.bukkit.Material
import org.bukkit.inventory.ItemStack;

enum class LuckEvent(val luckyConsumer: (User) -> Any) {
    GOLDEN_APPLE({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.GOLDEN_APPLE
        }.build())
    }),
    TNT_PRIMED({
        val tnt = it.player!!.world.spawn(it.player!!.location, org.bukkit.entity.TNTPrimed::class.java)
        tnt.fuseTicks = 200
        tnt
    }),
    GIVE_TNT({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.TNT
            amount(2)
        }.build())
    }),
    BLOCK_BREAK({
        val block = it.player!!.location.subtract(0.0, 10.0, 0.0).block
        if (block?.type != Material.BEDROCK)
            block.breakNaturally(ItemStack(Material.AIR))
    }),
    GIVE_EMERALD({
        val drop = kotlin.collections.listOf(
            org.bukkit.inventory.ItemStack(org.bukkit.Material.EMERALD_BLOCK),
            org.bukkit.inventory.ItemStack(org.bukkit.Material.GOLD_BLOCK),
            org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND_BLOCK)
        )
        val spawnPoint = it.player!!.location.clone().add(0.0, 0.4, 0.0)
        repeat(7) { time ->
            clepto.bukkit.B.postpone(13 * time) {
                val item = me.func.box.app.getWorld()
                    .spawnEntity(spawnPoint, org.bukkit.entity.EntityType.DROPPED_ITEM) as org.bukkit.entity.Item
                item.velocity =
                    org.bukkit.util.Vector(java.lang.Math.random() - 0.5, 0.2, java.lang.Math.random() - 0.5)
                item.itemStack = drop.random()
            }
        }
    }),
    GIVE_EFFECTS({
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.BLINDNESS,
                8 * 20,
                0
            )
        )
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.INCREASE_DAMAGE,
                8 * 20,
                0
            )
        )
        it.player!!.sendTitle("§bЭффекты", "§7Слепота (8 секунд.) Сила I (8 секунд.)", 10, 35, 20)
    }),
    LEVITATION({
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.LEVITATION,
                6 * 20,
                0
            )
        )
        it.player!!.sendTitle("§bЭффект", "§7Левитация (6 секунд.)", 10, 35, 20)
    }),
    SPEED({
        it.player!!.addPotionEffect(org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SPEED, 5 * 20, 0))
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.INVISIBILITY,
                8 * 20,
                0
            )
        )
        it.player!!.sendTitle("§bЭффект", "§7Невидимость (8 секунд.)", 10, 35, 20)
    }),
    ARROW({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.ARROW
            amount(8)
        }.build())
    }),
    THREADS_STICKS({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.STICK
            amount(6)
        }.build())
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.WEB
            amount(6)
        }.build())
    }),
    ANVIL({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.ANVIL
        }.build())
    }),
    WATER({
        it.player!!.location.block.type = Material.WATER
        it.player!!
    }),
    LAVA({
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.FIRE_RESISTANCE,
                9 * 20,
                3
            )
        )
        it.player!!.location.block.type = Material.LAVA
        it.player!!
    }),
    SNOWBALL({
        it.player!!.inventory.addItem(dev.implario.bukkit.item.item {
            type = Material.SNOW_BALL
            amount(16)
        }.build())
    }),
    FAST_DIGGING({
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.FAST_DIGGING,
                10 * 20,
                3
            )
        )
        it.player!!.sendTitle("§bЭффект", "§7Быстрое вскапывание (10 секунд.)", 10, 35, 20)
    }),
    VERY_FAST_DIGGING({
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.FAST_DIGGING,
                6 * 20,
                8
            )
        )
        it.player!!.sendTitle("§bЭффект", "§7Быстрое вскапывание (6 секунд.)", 10, 35, 20)
    }),
    STRIKE_LIGHTNING({ it.player!!.world.strikeLightning(it.player!!.location) }),
    WEB({
        it.player!!.location.block.type = Material.WEB
        it.player!!
    }),
    WITHER({
        it.player!!.addPotionEffect(
            org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.WITHER,
                3 * 20,
                1
            )
        )
        it.player!!.sendTitle("§bЭффект", "§7Иссушение (3 секунды.)", 10, 35, 20)
    }),
    ANVIL_DROP({
        it.player!!.location.subtract(0.0, 2.0, 0.0).block.type = Material.ANVIL
        it.player!!
    }),
    TELEPORT({ it ->
        it.player!!.teleport(
            org.bukkit.Bukkit.getOnlinePlayers().filter { it.gameMode != org.bukkit.GameMode.SPECTATOR }.random()
        )
    }),
    SWORD({ SuperSword.values().random().give(it) }), ;

    fun accept(user: User) {
        luckyConsumer(user)
    }
}