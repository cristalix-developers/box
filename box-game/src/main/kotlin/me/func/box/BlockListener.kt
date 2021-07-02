package me.func.box

import clepto.bukkit.B
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import ru.cristalix.core.formatting.Formatting

class BlockListener : Listener {

    @EventHandler
    fun BlockBreakEvent.handle() {
        if (app.status == Status.STARTING || block.type == Material.WOOD || block.type == Material.WORKBENCH) {
            cancel = true
            return
        }
        val user = app.getUser(player)!!
        if (block.type == Material.GOLD_BLOCK) {
            block.type = Material.AIR
            user.stat.money += 100
            player.sendMessage(Formatting.fine("Вы добыли §e100 монет§f!"))
            cancel = true
            return
        }
        if (block.type == Material.BED_BLOCK) {
            cancel = true
            app.teams.filter { it.location!!.distanceSquared(block.location) < 25 && !it.players.contains(player.uniqueId) }
                .forEach {
                    it.bed = false
                    cancel = false
                    dropItems = false
                    B.bc(Formatting.fine(player.name + " сломал кровать команды " + it.color.chatColor + it.color.teamName))
                    it.players.forEach { uid ->
                        Bukkit.getPlayer(uid)?.playSound(player.location, "entity.enderdragon.ambient", 1f, 1f)
                        Bukkit.getPlayer(uid)?.sendTitle("§cКровать уничтожена!", "§eВы больше не оживете")
                    }
                    return
                }
            val bed = Bukkit.getOnlinePlayers().filter {
                app.getUser(it)!!.bed != null && app.getUser(it)!!.bed!!.distanceSquared(it.location) < 25
            }
            if (bed.isNotEmpty()) {
                if (block.drops.isNotEmpty())
                    player.inventory.addItem(block.drops.first())
                app.getUser(bed[0])!!.bed = null
                cancel = false
                dropItems = false
                block.drops.clear()
                player.sendMessage(Formatting.fine("Вы сломали кровать!"))
                bed[0].sendMessage(Formatting.error("Ваша кровать сломана!"))
            }
            return
        }

        block.drops.forEach { player.inventory.addItem(it) }
        block.drops.clear()
        dropItems = false

        if (player.itemInHand.hasItemMeta()) {
            val item = CraftItemStack.asNMSCopy(player.itemInHand)

            if (item.hasTag() && item.tag.hasKey("extra") && item.tag.getString("extra") == "pickaxe") {
                if (player.isSneaking)
                    return

                val yaw = player.location.yaw
                val pitch = player.location.pitch

                if (pitch < -45 || pitch > 45) {
                    breakBlock(player, block.location, 1, 0, 0)
                    breakBlock(player, block.location, 1, 0, 1)
                    breakBlock(player, block.location, 1, 0, -1)
                    breakBlock(player, block.location, 0, 0, 1)
                    breakBlock(player, block.location, 0, 0, -1)
                    breakBlock(player, block.location, -1, 0, -1)
                    breakBlock(player, block.location, -1, 0, 0)
                    breakBlock(player, block.location, -1, 0, 1)
                } else if ((yaw > -45 && yaw < 45) || (yaw > 135 && yaw < 225) || yaw < -135) {
                    breakBlock(player, block.location, -1, 1, 0)
                    breakBlock(player, block.location, 0, 1, 0)
                    breakBlock(player, block.location, 1, 1, 0)
                    breakBlock(player, block.location, -1, -1, 0)
                    breakBlock(player, block.location, 0, -1, 0)
                    breakBlock(player, block.location, 1, -1, 0)
                    breakBlock(player, block.location, -1, 0, 0)
                    breakBlock(player, block.location, 1, 0, 0)
                } else {
                    breakBlock(player, block.location, 0, 1, -1)
                    breakBlock(player, block.location, 0, 1, 0)
                    breakBlock(player, block.location, 0, 1, 1)
                    breakBlock(player, block.location, 0, -1, -1)
                    breakBlock(player, block.location, 0, -1, 0)
                    breakBlock(player, block.location, 0, -1, 1)
                    breakBlock(player, block.location, 0, 0, -1)
                    breakBlock(player, block.location, 0, 0, 1)
                }
            }
        }
    }

    @EventHandler
    fun BlockPlaceEvent.handle() {
        if (app.status == Status.STARTING)
            cancel = true
        if (blockPlaced.location.subtract(0.0, 1.0, 0.0).block.type == Material.BED_BLOCK) {
            player.sendMessage(Formatting.error("Над кроватью нельзя ставить блоки!"))
            cancel = true
        }
        if (blockPlaced.type == Material.GOLD_BLOCK) {
            player.sendMessage(Formatting.fine("Рукотворный?! Пойдет, заберу за §e5 монет"))
            app.getUser(player)!!.stat.money += 5
            blockPlaced.type = Material.AIR
            return
        }
        if (blockPlaced.type == Material.BED_BLOCK) {
            if (blockPlaced.location.clone().add(0.0, 1.0, 0.0).block.type != Material.AIR ||
                blockPlaced.location.clone().add(0.0, 2.0, 0.0).block.type != Material.AIR
            ) {
                player.sendMessage(Formatting.error("Над кроватью должна быть пустота."))
                cancel = true
                return
            }
            val user = app.getUser(player)!!
            if (user.bed != null)
                return
            player.sendMessage(Formatting.fine("Вы установили личную кровать!"))
            user.bed = blockPlaced.location
        }
    }

    @EventHandler
    fun EntityExplodeEvent.handle() {
        if (entityType == EntityType.PRIMED_TNT) {
            blockList().removeIf {
                it.type == Material.WOOD ||
                        it.type == Material.BED_BLOCK ||
                        it.type == Material.GOLD_BLOCK ||
                        it.type == Material.CHEST ||
                        it.type == Material.WORKBENCH
            }
        }
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        if (cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION ||
            cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
        ) {
            damage = 0.0
        }
    }

    private fun breakBlock(player: Player, location: Location, x: Int, y: Int, z: Int) {
        val clone = location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
        val type = clone.block.type
        if (type == Material.BEDROCK || type == Material.WOOD || type == Material.BED_BLOCK || type == Material.GOLD_BLOCK)
            return
        if (type == Material.EMERALD_ORE) {
            val exp = clone.world.spawnEntity(clone, EntityType.EXPERIENCE_ORB) as ExperienceOrb
            exp.experience = 5
        }
        clone.block.drops.forEach { player.inventory.addItem(it) }
        clone.block.type = Material.AIR
    }
}