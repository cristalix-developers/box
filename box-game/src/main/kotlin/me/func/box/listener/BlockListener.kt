package me.func.box.listener

import clepto.bukkit.B
import me.func.box.User
import me.func.box.app
import me.func.box.data.Status
import me.func.box.map.LuckyGet
import me.func.box.mod.ModHelper
import net.minecraft.server.v1_12_R1.ItemStack
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.metadata.FixedMetadataValue
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.util.UtilEntity
import java.io.ByteArrayInputStream
import java.util.*

class BlockListener : Listener {

    private val luckBlock: org.bukkit.inventory.ItemStack

    init {
        // Десерелизация предмета лакиблока
        val stream = ByteArrayInputStream(
            Base64.getDecoder()
                .decode("H4sIAAAAAAAAAE2OPVPCMBzG/4AodnF0ZXDQoXdBoFy8Y1DwIBwpAuEl3UIboDQpXGmF8IVc/Qx+Muvmsz0vv7vHAqhAMQzgToex9BOxTl+OUaZUAcqdfRanBQtKqdhYYE3/4tEplklOkAAeUAM1sI+RHdQlthvOc83Gvljb6E9CiFoL4Zz7SPYHmaShPN5CJZXnNEvk0QKAQgXKc6EyCd/SDJC33KJgOVC+IU7u2RSpEdkdWiSem1WHOETnff/VGRr8b9tMxaKpeH2w9eJxttJzNKxPlOxPar6efbo90qTaDd0uvdCLu+WMG7qgddqbK283UVxzM2Iq5Gx8ppogt+tpj70bj1FEF+6OX2YNqvmFsshQtmmSuIbX43Y7f2/BTRAeD0qYCly5Qku4//nCw8yPTPVN7f2o+mikUvvTE0ARrrtCi42EEvwCE+bPt3IBAAA=")
        )
        val compound: NBTTagCompound = NBTCompressedStreamTools.a(stream)
        luckBlock = CraftItemStack.asBukkitCopy(ItemStack(compound))
    }

    @EventHandler
    fun BlockBreakEvent.handle() {
        if (app.status == Status.STARTING || block.type == Material.WOOD || block.type == Material.WORKBENCH) {
            cancel = true
            return
        }
        val user = app.getUser(player)!!

        if (block.type == Material.GOLD_BLOCK) {
            block.type = Material.AIR
            user.giveMoney(100)
            ModHelper.glow(user, 255, 131, 237)
            cancel = true
            return
        } else if (block.type == Material.BED_BLOCK) {
            cancel = true
            app.teams.filter { it.location!!.distanceSquared(block.location) < 25 && !it.players.contains(player.uniqueId) }
                .forEach {
                    it.bed = false
                    cancel = false
                    dropItems = false
                    if (user.stat.currentBreakBedEffect.getParticle() != null) {
                        repeat(200) { time ->
                            block.world.spawnParticle(
                                user.stat.currentBreakBedEffect.getParticle(),
                                block.location.x - kotlin.math.sin(Math.toRadians(time * 10.0)) * time / 100.0,
                                block.location.y + 1,
                                block.location.z - kotlin.math.cos(Math.toRadians(time * 10.0)) * time / 100.0,
                                1
                            )
                        }
                    }

                    ModHelper.glow(user, 42, 189, 102)
                    B.bc(Formatting.fine(player.name + " сломал кровать команды " + it.color.chatColor + it.color.teamName))
                    it.players.forEach { uid ->
                        Bukkit.getPlayer(uid)?.playSound(player.location, "entity.enderdragon.ambient", 1f, 1f)
                        Bukkit.getPlayer(uid)?.sendTitle("§cКровать уничтожена!", "§eВы больше не оживете")
                    }
                    user.bedDestroy += 1
                    return
                }
            val bed = Bukkit.getOnlinePlayers().mapNotNull { app.getUser(it) }.filter {
                it.bed != null && it.bed == block.location
            }
            if (bed.isNotEmpty()) {
                if (block.drops.isNotEmpty())
                    player.inventory.addItem(block.drops.first())
                bed.forEach {
                    it.bed = null
                    ModHelper.glow(it, 255, 0, 0)
                    it.player?.sendMessage(Formatting.error("${player.displayName} сломал вашу кровать!"))
                    it.player?.sendTitle("§cКровать уничтожена!", "§eВы больше не оживете")
                }
                cancel = false
                dropItems = false
                block.drops.clear()
                player.sendMessage(Formatting.fine("Вы сломали кровать!"))
            }
            return
        }

        breakBlock(user, block.location, 0, 0, 0)
        block.drops.clear()
        dropItems = false
        user.blockDestroy += 1

        if (player.itemInHand.hasItemMeta()) {
            val item = CraftItemStack.asNMSCopy(player.itemInHand)

            if (item.hasTag() && item.tag.hasKey("extra") && item.tag.getString("extra") == "pickaxe") {
                if (player.isSneaking)
                    return

                val yaw = player.location.yaw
                val pitch = player.location.pitch

                if (pitch < -45 || pitch > 45) {
                    breakBlock(user, block.location, 1, 0, 0)
                    breakBlock(user, block.location, 1, 0, 1)
                    breakBlock(user, block.location, 1, 0, -1)
                    breakBlock(user, block.location, 0, 0, 1)
                    breakBlock(user, block.location, 0, 0, -1)
                    breakBlock(user, block.location, -1, 0, -1)
                    breakBlock(user, block.location, -1, 0, 0)
                    breakBlock(user, block.location, -1, 0, 1)
                } else if ((yaw > -45 && yaw < 45) || (yaw > 135 && yaw < 225) || yaw < -135) {
                    breakBlock(user, block.location, -1, 1, 0)
                    breakBlock(user, block.location, 0, 1, 0)
                    breakBlock(user, block.location, 1, 1, 0)
                    breakBlock(user, block.location, -1, -1, 0)
                    breakBlock(user, block.location, 0, -1, 0)
                    breakBlock(user, block.location, 1, -1, 0)
                    breakBlock(user, block.location, -1, 0, 0)
                    breakBlock(user, block.location, 1, 0, 0)
                } else {
                    breakBlock(user, block.location, 0, 1, -1)
                    breakBlock(user, block.location, 0, 1, 0)
                    breakBlock(user, block.location, 0, 1, 1)
                    breakBlock(user, block.location, 0, -1, -1)
                    breakBlock(user, block.location, 0, -1, 0)
                    breakBlock(user, block.location, 0, -1, 1)
                    breakBlock(user, block.location, 0, 0, -1)
                    breakBlock(user, block.location, 0, 0, 1)
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
            return
        }
        if (blockPlaced.type == Material.GOLD_BLOCK) {
            app.getUser(player)!!.giveMoney(5)
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
            ModHelper.glow(user, 42, 189, 102)
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
                        it.type == Material.WORKBENCH ||
                        it.hasMetadata("lucky")
            }
            if (entity.hasMetadata("shooter")) {
                val shooter = Bukkit.getPlayer(UUID.fromString(entity.getMetadata("shooter")[0].asString()))
                if (shooter != null) {
                    blockList().forEach { it.drops.forEach { item -> shooter.inventory.addItem(item) } }
                    cancel = true
                    blockList().forEach { it.setTypeAndDataFast(0, 0) }
                }
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

    @EventHandler
    fun PlayerInteractAtEntityEvent.handle() {
        if (clickedEntity.type == EntityType.ARMOR_STAND && app.isLuckyGame && app.status == Status.GAME)
            LuckyGet.removeBlock(
                clickedEntity.location.clone().add(0.0, 3 - 1.0 / 16, 0.0),
                clickedEntity.uniqueId,
                app.getUser(player)!!
            )
    }

    private fun breakBlock(user: User, location: Location, x: Int, y: Int, z: Int) {
        val player = user.player!!
        val clone = location.clone().add(x.toDouble(), y.toDouble(), z.toDouble())
        val type = clone.block.type
        if (type == Material.BEDROCK || type == Material.WOOD || type == Material.BED_BLOCK || type == Material.GOLD_BLOCK || type == Material.WORKBENCH)
            return
        if (type == Material.EMERALD_ORE)
            player.giveExp(5)
        clone.block.drops.forEach { player.inventory.addItem(it) }
        val beforeType = clone.block.type
        clone.block.type = Material.AIR
        if (app.isLuckyGame && beforeType == Material.STONE) {
            if (clone.block.hasMetadata("lucky")) {
                LuckyGet.removeBlock(clone, UUID.fromString(clone.block.getMetadata("lucky")[0].asString()), user)
                ModHelper.glow(user, 255, 131, 237)
            }
            BlockFace.values().forEach {
                val nextBlock = clone.block.getRelative(it)
                if (nextBlock != null && nextBlock.type == Material.STONE && Math.random() < 0.008) {
                    generateLuckyBlock(nextBlock.location)
                }
            }
        }
    }

    private fun generateLuckyBlock(blockLocation: Location) {
        val location = blockLocation.clone().toCenterLocation()
        val stand: ArmorStand = location.getWorld().spawnEntity(
            location.clone().subtract(0.0, 3.0 - 1.0 / 16, 0.0),
            EntityType.ARMOR_STAND
        ) as ArmorStand
        stand.helmet = luckBlock
        stand.isInvulnerable = true
        stand.setGravity(false)
        stand.isMarker = true
        stand.isVisible = false
        stand.setMetadata("lucky", FixedMetadataValue(app, true))

        blockLocation.block.setMetadata("lucky", FixedMetadataValue(app, stand.uniqueId.toString()))

        UtilEntity.setScale(stand, 1.7, 1.7, 1.7)
    }
}