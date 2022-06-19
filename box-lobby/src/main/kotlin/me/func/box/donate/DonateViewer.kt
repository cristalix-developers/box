package me.func.box.donate

import dev.implario.bukkit.item.item
import me.func.box.app
import me.func.box.compass
import me.func.box.cosmetic.*
import me.func.mod.Anime
import me.func.mod.Glow
import me.func.mod.Npc
import me.func.mod.Npc.location
import me.func.mod.Npc.onClick
import me.func.mod.selection.Button
import me.func.mod.selection.Confirmation
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.command
import me.func.mod.util.nbt
import me.func.protocol.GlowColor
import me.func.protocol.npc.NpcBehaviour
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.inventory.ClickableItem
import ru.cristalix.core.inventory.ControlledInventory
import ru.cristalix.core.inventory.InventoryContents
import ru.cristalix.core.inventory.InventoryProvider
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.GetAccountBalancePackage
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import sun.audio.AudioPlayer.player
import java.util.concurrent.TimeUnit

class DonateViewer : Listener {
    fun <T : Donate> temp(
        player: Player,
        name: String,
        isDonate: Boolean,
        vararg donate: T,
        converter: (Button, T) -> Button = { button, _ -> button }
    ) {
        selection {
            val user = app.getUser(player)
            val stat = user.stat
            title = name
            rows = 3
            columns = 3
            if (isDonate)
                vault = "donate"
            money = if (isDonate) {
                val balance = app.socketClient.writeAndAwaitResponse<GetAccountBalancePackage>(
                    GetAccountBalancePackage(player.uniqueId)
                ).get(1, TimeUnit.SECONDS).balanceData
                "Кристаликов ${balance.coins + balance.crystals}"
            } else {
                "Монет " + stat.money
            }
            storage = donate.map { pos ->
                converter(button {
                    val sale = if (pos is MoneyBuy) pos.percent else 0
                    val has = when (pos) {
                        is Armor -> stat.skins?.contains(pos.getCode())
                        is BreakBedEffect -> stat.breakBedEffects.contains(pos)
                        is Starter -> stat.starters?.contains(pos)
                        is KillMessage -> stat.killMessages.contains(pos)
                        is Sword -> stat.swords?.contains(pos)
                        else -> false
                    } == true
                    if (!has) price = pos.getPrice().toLong()
                    else hint = "Выбрать"
                    val current = has && when (pos) {
                        is Armor -> stat.currentSkin == pos.getCode()
                        is BreakBedEffect -> stat.currentBreakBedEffect == pos
                        is Starter -> stat.currentStarter == pos
                        is KillMessage -> stat.currentKillMessage == pos
                        is Sword -> stat.currentSword == pos
                        else -> false
                    }
                    if (pos is Starter) hover = pos.lore
                    onClick { player, _, _ ->
                        if (current)
                            return@onClick
                        Anime.close(player)
                        if (has) {
                            when (pos) {
                                is Armor -> stat.currentSkin = pos.getCode()
                                is BreakBedEffect -> stat.currentBreakBedEffect = pos
                                is Starter -> stat.currentStarter = pos
                                is KillMessage -> stat.currentKillMessage = pos
                                is Sword -> stat.currentSword = pos
                            }
                            Anime.title(player, "Выбрано!")
                            return@onClick
                        }
                        if (isDonate) {
                            buy(player, (pos.getPrice() * (100.0 - sale) / 100.0).toInt(), pos)
                            return@onClick
                        }
                        if (stat.money < pos.getPrice()) {
                            Anime.killboardMessage(player, Formatting.error("Недостаточно монет!"))
                            Glow.animate(player, 0.4, GlowColor.RED)
                            return@onClick
                        }
                        stat.money -= pos.getPrice()
                        pos.give(user)
                        Glow.animate(player, 0.4, GlowColor.GREEN)
                    }
                    title = if (current) "[ Выбрано ]" else if (has) "§7Выбрать" else "§bКупить"
                    description = pos.getTitle()
                }, pos).apply { if (pos is MoneyBuy) sale(pos.percent) }
            }.toMutableList()
        }.open(player)
    }

    private val all = selection {
        title = "Персонализация"
        rows = 3
        columns = 3
        hint = "Открыть"

        buttons(
            button {
                title = "Костюмы"
                description = "скины на алмазную броню"
                item = item { type = Material.DIAMOND_HELMET }.nbt("armors", "chicken")
                onClick { player, _, _ ->
                    temp(player, "Костюмы", true, *Armor.values()) { button, armor -> button.item(armor.getItem()) }
                }
            }, button {
                title = "Послания"
                description = "сообщение после смерти"
                item = item { type = Material.CLAY_BALL }.nbt("other", "info")
                onClick { player, _, _ ->
                    temp(
                        player,
                        "Послания",
                        true,
                        *KillMessage.values()
                    ) { button, message -> button.item(message.getItemStack()) }
                }
            }, button {
                title = "Мечи"
                description = "скины на алмазный меч"
                item = item { type = Material.DIAMOND_SWORD }.nbt("weapons_other", "info")
                onClick { player, _, _ ->
                    temp(player, "Мечи", true, *Sword.values()) { button, sword -> button.item(sword.getItem()) }
                }
            }, button {
                title = "Монеты"
                description = "валюта игры"
                item = item { type = Material.CLAY_BALL }.nbt("other", "coin4")
                onClick { player, _, _ ->
                    temp(player, "Монеты", true, *MoneyBuy.values()) { button, donate ->
                        button.item(item { type = Material.CLAY_BALL }.nbt("other", donate.getCode()))
                    }
                }
            }, button {
                title = "Киты"
                description = "Стартовые наборы"
                item = item { type = Material.TNT }
                onClick { player, _, _ ->
                    temp(player, "Киты", false, *Starter.values()) { button, starter -> button.item(starter.getItem()) }
                }
            }, button {
                title = "Разрушения"
                description = "эффекты при ломании кровати"
                item = item { type = Material.BED }
                onClick { player, _, _ ->
                    temp(
                        player,
                        "Разрушения",
                        true,
                        *BreakBedEffect.values()
                    ) { button, effect -> button.item(effect.getItemStack()) }
                }
            }
        )
    }

    private fun buy(player: Player, money: Int, donate: Donate) {
        val user = app.getUser(player)

        if (!user.session.isActive) {
            Anime.killboardMessage(
                player,
                Formatting.error("Что-то пошло не так... Попробуйте перезайти")
            )
            return
        }
        Confirmation("Купить §a${donate.getTitle()}\n§fза §b$money кристалик(а)") {
            ISocketClient.get().writeAndAwaitResponse<MoneyTransactionResponsePackage>(
                MoneyTransactionRequestPackage(player.uniqueId, money, true, donate.getTitle())
            ).thenAccept {
                if (it.errorMessage != null) {
                    Anime.killboardMessage(player, Formatting.error(it.errorMessage))
                    Glow.animate(player, 0.4, GlowColor.RED)
                    return@thenAccept
                }
                Anime.title(player, Formatting.fine("Успешно!"))
                Anime.close(player)
                Glow.animate(player, 0.4, GlowColor.GREEN)
                donate.give(user)
                player.sendMessage(Formatting.fine("Спасибо за поддержку разработчиков!"))
            }
        }.open(player)
    }

    init {
        Npc.npc {
            location(Location(app.worldMeta.world, -251.0, 110.0, 30.0, 115f, 0f))
            name = "§e§lМеню"
            behaviour = NpcBehaviour.STARE_AT_PLAYER
            skinUrl =
                "https://webdata.c7x.dev/textures/skin/479cb4df-7024-11ea-acca-1cb72caa35fd"
            skinDigest = "479cb4df-702411eaacca1cb72caa35fd"
            onClick {
                Anime.close(it.player)
                all.open(it.player)
            }
        }
        command("menu") { player, _ -> all.open(player) }
        command("donate") { player, _ -> all.open(player) }
    }

    private val menuItem = item {
        type = Material.EMERALD
        text("§aКосметика")
    }
    private val serversItem = item {
        type = Material.COMPASS
        text("§bБыстрая игра")
    }

    @EventHandler
    fun PlayerJoinEvent.handle() {
        player.inventory.setItem(0, serversItem)
        player.inventory.setItem(4, menuItem)
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        val type = player.itemInHand.getType()
        if (type == Material.COMPASS) compass.open(player)
        else if (type == Material.EMERALD) all.open(player)
    }
}