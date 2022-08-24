package me.func.box

import me.func.mod.data.Sprites
import me.func.mod.selection.Choicer
import me.func.mod.selection.button
import me.func.mod.selection.choicer
import org.bukkit.Material
import org.bukkit.entity.Player


val startGameMenu = choicer {
    title = "Бедроковая коробка"
    description = "Находи чужие кровати и уничтожай врагов!"
    buttons(
        button {
            hint = "Играть!"
            texture = Sprites.SOLO.path()
            title = "Solo"
            description = "Онлайн: " + realmService.getOnlineOnRealms("BOX4")
            onClick { it, _, _ -> ClickServer("BOX4", 4).accept(it) }
        },
        button {
            hint = "Играть!"
            texture = Sprites.SQUAD.path()
            title = "Squad"
            description = "Онлайн: " + realmService.getOnlineOnRealms("BOXS")
            onClick { it, _, _ -> ClickServer("BOXS", 16).accept(it) }
        },
        button {
            hint = "Играть!"
            texture = Sprites.SPECIAL.path()
            title = "Lucky"
            description = "Онлайн: " + realmService.getOnlineOnRealms("BOX8")
            onClick { it, _, _ -> ClickServer("BOX8", 16).accept(it) }
        },
    )
}

fun statisticMenu(player: Player) {
    val user = app.getUser(player)
    val stat = user.stat

    val menu = Choicer("Бедроковая коробка", "Общая статистика")
    menu.add(
        button {
            description("§fПобед §b${stat.wins}")
            item = dev.implario.bukkit.item.item {
                type = Material.CLAY_BALL
                nbt("other", "cup")
            }
        })
    menu.add(
        button {
            description("§fУбийств §c${stat.kills}")
            item = dev.implario.bukkit.item.item {
                type = Material.CLAY_BALL
                nbt("other", "custom_sword")
            }
        })
    menu.add(
        button {
            description("§fСмертей §f${stat.deaths}")
            item = dev.implario.bukkit.item.item {
                type = Material.CLAY_BALL
                nbt("other", "finale_kill")
            }
        })
    menu.add(
        button {
            description("§fИгр сыграно §f${stat.games}")
            item = dev.implario.bukkit.item.item {
                type = Material.CLAY_BALL
                nbt("skyblock", "collections")
            }
        })
    menu.add(
        button {
            description("§fВаш баланс §f${stat.money}")
            item = dev.implario.bukkit.item.item {
                type = Material.CLAY_BALL
                nbt("other", "coin5")
            }
        }
    )

    menu.open(player)
}
