package me.func.box.map

import clepto.bukkit.B
import com.mojang.brigadier.arguments.StringArgumentType.string
import dev.implario.bukkit.item.item
import me.func.box.app
import net.minecraft.server.v1_12_R1.ItemStack
import net.minecraft.server.v1_12_R1.MinecraftServer
import net.minecraft.server.v1_12_R1.NBTCompressedStreamTools
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftVillager
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.metadata.FixedMetadataValue
import ru.cristalix.core.util.UtilEntity
import java.io.ByteArrayInputStream
import java.security.SecureRandom
import java.util.*


object Generator {

    private val random = SecureRandom()

    private val luckBlock: org.bukkit.inventory.ItemStack

    init {
        // Десерелизация предмета лакиблока
        val stream = ByteArrayInputStream(Base64.getDecoder().decode("H4sIAAAAAAAAAE2OPVPCMBzG/4AodnF0ZXDQoXdBoFy8Y1DwIBwpAuEl3UIboDQpXGmF8IVc/Qx+Muvmsz0vv7vHAqhAMQzgToex9BOxTl+OUaZUAcqdfRanBQtKqdhYYE3/4tEplklOkAAeUAM1sI+RHdQlthvOc83Gvljb6E9CiFoL4Zz7SPYHmaShPN5CJZXnNEvk0QKAQgXKc6EyCd/SDJC33KJgOVC+IU7u2RSpEdkdWiSem1WHOETnff/VGRr8b9tMxaKpeH2w9eJxttJzNKxPlOxPar6efbo90qTaDd0uvdCLu+WMG7qgddqbK283UVxzM2Iq5Gx8ppogt+tpj70bj1FEF+6OX2YNqvmFsshQtmmSuIbX43Y7f2/BTRAeD0qYCly5Qku4//nCw8yPTPVN7f2o+mikUvvTE0ARrrtCi42EEvwCE+bPt3IBAAA="))
        val compound: NBTTagCompound = NBTCompressedStreamTools.a(stream)
        luckBlock = CraftItemStack.asBukkitCopy(ItemStack(compound))
    }

    fun generateContentOfCube(
        start: Location,
        xSize: Int,
        ySize: Int,
        zSize: Int,
    ) {
        val originalX = start.blockX
        val originalY = start.blockY
        val originalZ = start.blockZ
        for (x in 1..xSize) {
            for (y in 1..ySize) {
                for (z in 1..zSize) {
                    start.set((originalX + x).toDouble(), (originalY + y).toDouble(), (originalZ + z).toDouble())
                    val random = random.nextDouble()
                    when {
                        random > 0.1 -> start.block.setTypeAndDataFast(1, 0)
                        random > 0.0002 -> {
                            if (!app.isLuckyGame || random > 0.01) {
                                start.block.setTypeAndDataFast(Material.EMERALD_ORE.id, 0)
                            } else {
                                start.block.setTypeAndDataFast(0, 0)

                                val location = start.toCenterLocation()
                                val stand: ArmorStand = location.getWorld().spawnEntity(
                                    location.clone().subtract(-1.0, 2.0 - 1.0 / 16, -1.0),
                                    EntityType.ARMOR_STAND
                                ) as ArmorStand
                                stand.helmet = luckBlock
                                stand.isInvulnerable = true
                                stand.setGravity(false)
                                stand.setCanMove(false)
                                stand.isVisible = false
                                stand.setMetadata("lucky", FixedMetadataValue(app, true))

                                UtilEntity.setScale(stand, 1.7, 1.7, 1.7)
                            }
                        }
                        else -> start.block.setTypeAndDataFast(Material.GOLD_BLOCK.id, 0)
                    }
                }
            }
        }
    }

    fun generateCube(
        start: Location,
        xSize: Int,
        ySize: Int,
        zSize: Int,
    ) {
        val originalX = start.blockX
        val originalY = start.blockY
        val originalZ = start.blockZ
        start.set(originalX.toDouble(), originalY.toDouble(), originalZ.toDouble())
        makeBox(start, Material.BEDROCK, xSize, ySize, zSize, false)
    }

    fun generateRooms(
        start: Location,
        xSize: Int,
        ySize: Int,
        zSize: Int,
        roomSize: Int,
        wholeSize: Int
    ): List<Location> {
        val locations = arrayListOf<Location>()
        if (app.teams.isNotEmpty()) {
            start.set(0.0, ySize * random.nextDouble() / 2 + ySize / 4, zSize * random.nextDouble() / 2 + zSize / 4)
            locations.add(generateRoom(start, -1, 0, 0, roomSize, wholeSize))
        }
        if (app.teams.size > 1) {
            start.set(
                xSize.toDouble() - roomSize,
                ySize * random.nextDouble() / 3 + ySize / 4 + roomSize,
                zSize * random.nextDouble() / 2 + zSize / 4
            )
            locations.add(generateRoom(start, 1, 0, 0, roomSize, wholeSize))
        }
        if (app.teams.size > 2) {
            start.set(
                xSize * random.nextDouble() / 2 + xSize / 4,
                ySize * random.nextDouble() / 3 + ySize / 4 + roomSize,
                zSize.toDouble() - roomSize
            )
            locations.add(generateRoom(start, 0, 0, 1, roomSize, wholeSize))
        }
        if (app.teams.size > 3) {
            start.set(
                xSize * random.nextDouble() / 2 + xSize / 4,
                ySize * random.nextDouble() / 3 + ySize / 4 + roomSize,
                0.0
            )
            locations.add(generateRoom(start, 0, 0, -1, roomSize, wholeSize))
        }
        return locations
    }

    private fun iterate(location: Location, type: Material, vararg triple: Triple<Int, Int, Int>) {
        val originalX = location.blockX
        val originalY = location.blockY
        val originalZ = location.blockZ
        MinecraftServer.SERVER.postToMainThread {
            for (node in triple) {
                location.set(
                    (originalX + node.first).toDouble(),
                    (originalY + node.second).toDouble(),
                    (originalZ + node.third).toDouble()
                )
                location.block.setTypeAndDataFast(type.id, 0)
            }
        }
    }

    private fun generateRoom(start: Location, x: Int, y: Int, z: Int, size: Int, wholeSize: Int): Location {
        val originalX = start.x
        val originalY = start.y
        val originalZ = start.z
        start.set(originalX + x * size.toDouble(), originalY + y * size.toDouble(), originalZ + z * size.toDouble())
        makeBox(start, Material.BEDROCK, size, size, size, true)
        for (currentX in 0 until size) {
            for (currentZ in 0 until size) {
                start.set(
                    originalX + x * size.toDouble() + 1 + currentX,
                    originalY + y * size.toDouble() + 1,
                    originalZ + z * size.toDouble() + 1 + currentZ
                )
                start.block.type = Material.WOOD
                start.block.data = 1
            }
        }
        B.postpone(100) {
            start.set(originalX, originalY, originalZ)
            if (x != 0) {
                for (currentZ in 1..wholeSize) {
                    for (currentY in 1..wholeSize) {
                        start.set(originalX, originalY + 1, originalZ + (size - wholeSize) / 2)
                        iterate(
                            start,
                            Material.AIR,
                            Triple(if (x < 0) 0 else x * size, currentY, currentZ),
                        )
                    }
                }
            } else if (z != 0) {
                for (currentX in 1..wholeSize) {
                    for (currentY in 1..wholeSize) {
                        start.set(originalX + (size - wholeSize) / 2, originalY + 1, originalZ)
                        iterate(
                            start,
                            Material.AIR,
                            Triple(currentX, currentY, if (z < 0) 0 else z * size),
                        )
                    }
                }
            }
        }
        val location = Location(
            app.getWorld(),
            originalX + x * size * (if (x > 0) 2 else 1) + (if (x < 0) 3 else -1) + (if (x == 0) 4 else 0),
            originalY + 2,
            originalZ + z * size * (if (z > 0) 2 else 1) + (if (z > 0) -3 else 2)
        )
        val center = Location(
            app.getWorld(),
            originalX + x * size.toDouble() + size / 2,
            originalY + y * size.toDouble() + 2,
            originalZ + z * size.toDouble() + size / 2
        )
        val trader = location.world.spawnEntity(center, EntityType.VILLAGER) as CraftVillager
        center.clone().add(0.0, 0.0, 2.0).block.type = Material.WORKBENCH
        trader.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).baseValue = 0.0

        val bedFoot = location.block.getRelative(BlockFace.WEST).state
        val bedHead = bedFoot.block.getRelative(BlockFace.SOUTH).state
        bedFoot.type = Material.BED_BLOCK
        bedHead.type = Material.BED_BLOCK
        bedFoot.rawData = 0x0.toByte()
        bedHead.rawData = 0x8.toByte()
        bedFoot.update(true, false)
        bedHead.update(true, true)
        return location.clone().add(0.0, 1.5, 0.0)
    }

    private fun makeBox(start: Location, type: Material, xSize: Int, ySize: Int, zSize: Int, moment: Boolean) {
        val originalX = start.blockX
        val originalY = start.blockY
        val originalZ = start.blockZ
        for (x in 0..xSize) {
            for (z in 0..zSize) {
                start.set(originalX.toDouble(), originalY.toDouble(), originalZ.toDouble())
                iterate(
                    start,
                    type,
                    Triple(x, 0, z),
                    Triple(0, x, z),
                    Triple(x, z, 0),
                    Triple(x, ySize, z),
                    Triple(xSize, x, z),
                    Triple(x, z, zSize),
                )
            }
        }
    }
}