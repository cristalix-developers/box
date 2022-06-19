package me.func.box

import org.bukkit.Location

enum class ServerType(val title: String, val slot: Int, val origin: Location, val skin: String) {
    BOX4("§c§l1 §fx §c§l4 §f§lNORMAL", 8, Location(app.worldMeta.world, -255.0, 112.0, 36.0, 162f, 0f), "6f3f4a2e-7f84-11e9-8374-1cb72caa35fd"),
    BOXS("§c§l4 §fx §c§l4 §f§lNORMAL 1.8", 16, Location(app.worldMeta.world, -258.5, 112.0, 36.0, -174f, 0f), "7f3fea26-be9f-11e9-80c4-1cb72caa35fd"),
    BOX8("§c§l4 §fx §c§l4 §e§lLUCKY", 16, Location(app.worldMeta.world, -262.0, 112.0, 36.0, -152f, 0f), "30719b68-2c69-11e8-b5ea-1cb72caa35fd"),
    BOX5("§c§l25 §fx §c§l4 §f§lNORMAL 1.8", 100, Location(app.worldMeta.world, -265.0, 112.0, 34.0, -125f, 0f), "30392bb3-2c69-11e8-b5ea-1cb72caa35fd"),
}