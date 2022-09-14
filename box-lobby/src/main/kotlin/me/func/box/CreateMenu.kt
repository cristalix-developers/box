package me.func.box

import me.func.mod.conversation.data.Sprites
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.choicer
import me.func.mod.ui.menu.choicer.Choicer
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

    val menu = Choicer.builder()
        .title("Бедроковая коробка")
        .description("Общая статистика")
        .storage(
            button {
                description("§fПобед §b${stat.wins}")
                item = dev.implario.bukkit.item.item {
                    type = Material.CLAY_BALL
                    nbt("other", "cup")
                }
            },
            button {
                description("§fУбийств §c${stat.kills}")
                item = dev.implario.bukkit.item.item {
                    type = Material.CLAY_BALL
                    nbt("other", "custom_sword")
                }
            },
            button {
                description("§fСмертей §f${stat.deaths}")
                item = dev.implario.bukkit.item.item {
                    type = Material.CLAY_BALL
                    nbt("other", "finale_kill")
                }
            },
            button {
                description("§fИгр сыграно §f${stat.games}")
                item = dev.implario.bukkit.item.item {
                    type = Material.CLAY_BALL
                    nbt("skyblock", "collections")
                }
            },
            button {
                description("§fВаш баланс §f${stat.money}")
                item = dev.implario.bukkit.item.item {
                    type = Material.CLAY_BALL
                    nbt("other", "coin5")
                }
            },
            button {
                description("§fВаши логи")
                item = dev.implario.bukkit.item.item {
                    type = Material.CLAY_BALL
                    nbt("skyblock", "info")
                }
                onClick { player, _, _ -> player.performCommand("anime:logs") }
            }
        ).build()

    menu.open(player)
}
