package me.func.box

import org.bukkit.Location
import org.bukkit.Material

object Generator {

    fun generateCube(start: Location, xSize: Int, ySize: Int, zSize: Int) {
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
        for (x in 1..xSize) {
            for (z in 1..zSize) {
                start.set((originalX + x).toDouble(), originalY.toDouble(), (originalZ + z).toDouble())
                start.block.type = Material.BEDROCK
                start.set((originalX + x).toDouble(), (originalY + ySize).toDouble(), (originalZ + z).toDouble())
                start.block.type = Material.BEDROCK
                start.set(originalX.toDouble(), (originalY + x).toDouble(), (originalZ + z).toDouble())
                start.block.type = Material.BEDROCK
                start.set((originalX + xSize).toDouble(), (originalY + x).toDouble(), (originalZ + z).toDouble())
                start.block.type = Material.BEDROCK
                start.set((originalX + x).toDouble(), (originalY + z).toDouble(), originalZ.toDouble())
                start.block.type = Material.BEDROCK
                start.set((originalX + x).toDouble(), (originalY + z).toDouble(), (originalZ + zSize).toDouble())
                start.block.type = Material.BEDROCK
            }
        }
    }

}