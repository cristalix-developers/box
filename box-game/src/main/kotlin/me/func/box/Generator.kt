package me.func.box

import clepto.bukkit.B
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.block.BlockFace
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftVillager
import org.bukkit.entity.EntityType


object Generator {

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
                    if (Math.random() > 0.05)
                        start.block.type = Material.STONE
                    else
                        start.block.type = Material.EMERALD_ORE
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
        start.set(0.0, ySize * Math.random() / 2 + ySize / 4, zSize * Math.random() / 2 + zSize / 4)
        locations.add(generateRoom(start, -1, 0, 0, roomSize, wholeSize))
        start.set(
            xSize.toDouble() - roomSize,
            ySize * Math.random() / 2 + ySize / 4,
            zSize * Math.random() / 2 + zSize / 4
        )
        locations.add(generateRoom(start, 1, 0, 0, roomSize, wholeSize))
        return locations
    }

    private fun iterate(location: Location, type: Material, vararg triple: Triple<Int, Int, Int>) {
        val originalX = location.blockX
        val originalY = location.blockY
        val originalZ = location.blockZ
        for (node in triple) {
            location.set(
                (originalX + node.first).toDouble(),
                (originalY + node.second).toDouble(),
                (originalZ + node.third).toDouble()
            )
            location.block.type = type
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
                        start.set(originalX + (size - wholeSize) / 2 * z, originalY + 1, originalZ)
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
            originalX + x * size * (if (x > 0) 2 else 1) + (if (x < 0) 3 else -1),
            originalY + 2,
            originalZ + z * size * (if (z > 0) 2 else 1) + (if (z < 0) 3 else 2)
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
        return location
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