package me.func.box

import org.bukkit.Location
import org.bukkit.Material

object Generator {

    fun generateCube(start: Location, xSize: Int, ySize: Int, zSize: Int, roomSize: Int, wholeSize: Int) {
        val originalX = start.blockX
        val originalY = start.blockY
        val originalZ = start.blockZ
        for (x in 1..xSize) {
            for (y in 1..ySize) {
                for (z in 1..zSize) {
                    start.set((originalX + x).toDouble(), (originalY + y).toDouble(), (originalZ + z).toDouble())
                    if (Math.random() > 0.02)
                        start.block.type = Material.STONE
                    else
                        start.block.type = Material.EMERALD_ORE
                }
            }
        }
        start.set(originalX.toDouble(), originalY.toDouble(), originalZ.toDouble())
        makeBox(start, Material.BEDROCK, xSize, ySize, zSize)
        start.set(0.0, ySize * Math.random() / 2 + ySize / 4, zSize * Math.random() / 2 + zSize / 4)
        generateRoom(start, -1, 0, 0, roomSize, wholeSize)
        start.set(
            xSize.toDouble() - roomSize,
            ySize * Math.random() / 2 + ySize / 4,
            zSize * Math.random() / 2 + zSize / 4
        )
        generateRoom(start, 1, 0, 0, roomSize, wholeSize)
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

    private fun generateRoom(start: Location, x: Int, y: Int, z: Int, size: Int, wholeSize: Int) {
        val originalX = start.x
        val originalY = start.y
        val originalZ = start.z
        start.set(originalX + x * size.toDouble(), originalY + y * size.toDouble(), originalZ + z * size.toDouble())
        makeBox(start, Material.BEDROCK, size, size, size)
        start.set(originalX, originalY, originalZ)
        if (x != 0) {
            for (currentZ in 1..wholeSize) {
                for (currentY in 1..wholeSize) {
                    start.set(originalX, originalY, originalZ + (size - wholeSize) / 2)
                    iterate(
                        start,
                        Material.AIR,
                        Triple(if (x < 0) 0 else x * size, currentY, currentZ),
                    )
                }
            }
        }
        if (z != 0) {
            for (currentX in 1..wholeSize) {
                for (currentY in 1..wholeSize) {
                    start.set(originalX + (size - wholeSize) / 2 * z, originalY, originalZ)
                    iterate(
                        start,
                        Material.AIR,
                        Triple(currentX, currentY, if (z < 0) 0 else z * size),
                    )
                }
            }
        }
    }

    private fun makeBox(start: Location, type: Material, xSize: Int, ySize: Int, zSize: Int) {
        val originalX = start.blockX
        val originalY = start.blockY
        val originalZ = start.blockZ
        for (x in 1..xSize) {
            for (z in 1..zSize) {
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