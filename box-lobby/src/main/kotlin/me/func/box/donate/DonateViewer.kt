package me.func.box.donate

import dev.implario.bukkit.item.item
import me.func.box.app
import me.func.box.battlepass.BattlePassManager
import me.func.box.cosmetic.*
import me.func.box.reward.WeekRewards
import me.func.mod.Anime
import me.func.mod.reactive.ReactiveButton
import me.func.mod.ui.Glow
import me.func.mod.ui.menu.button
import me.func.mod.ui.menu.confirmation.Confirmation
import me.func.mod.ui.menu.dailyReward
import me.func.mod.ui.menu.selection
import me.func.mod.util.command
import me.func.mod.util.nbt
import me.func.mod.world.Npc
import me.func.mod.world.Npc.location
import me.func.mod.world.Npc.onClick
import me.func.protocol.data.color.GlowColor
import me.func.protocol.data.emoji.Emoji
import me.func.protocol.world.npc.NpcBehaviour
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.invoice.IInvoiceService
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import java.util.concurrent.TimeUnit

class DonateViewer : Listener {
    fun <T : Donate> temp(
        player: Player,
        name: String,
        isDonate: Boolean,
        vararg donate: T,
        converter: (ReactiveButton, T) -> ReactiveButton = { button, _ -> button }
    ) {
        selection {
            val user = app.getUser(player)
            val stat = user.stat
            title = name
            rows = 3
            columns = 3
            if (isDonate)
                vault = Emoji.DONATE
            money = if (isDonate) {
                val balance = IInvoiceService.get().getBalanceData(player.uniqueId)[1, TimeUnit.SECONDS]
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
                        user.giveMoney(-pos.getPrice())
                        pos.give(user)
                        Glow.animate(player, 0.4, GlowColor.GREEN)
                    }
                    title = if (current) "[ Выбрано ]" else if (has) "§7Выбрать" else "§bКупить"
                    description = pos.getTitle()
                }, pos).apply { if (pos is MoneyBuy) sale(pos.percent) }
            }.toMutableList()
        }.open(player)
    }

    val donateMenu = selection {
        title = "Персонализация"
        rows = 3
        columns = 3
        hint = "Открыть"

        buttons(
            button {
                title = "BattlePass"
                description = "Забери награды!"
                item = item {
                    type = Material.CLAY_BALL
                    nbt("other", "new_lvl_rare_close")
                }
                special = true
                onClick { player, _, _ ->
                    BattlePassManager.show(player)
                }
            },
            button {
                title = "Костюмы"
                description = "скины на алмазную броню"
                item = item { type = Material.DIAMOND_HELMET }.nbt("armors", "chicken")
                onClick { player, _, _ ->
                    temp(player, "Костюмы", true, *Armor.values()) { button, armor -> button.item(armor.getIcon()) }
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
                    ) { button, message -> button.item(message.getIcon()) }
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
                    ) { button, effect -> button.item(effect.getIcon()) }
                }
            }, button {
                title = "Ежедневки"
                description = "посмотрите ежедневные награды"
                item = item { type = Material.PAPER }
                onClick { player, _, _ ->
                    dailyReward {
                        currentDay = app.getUser(player).stat.rewardStreak
                        taken = true
                        storage = WeekRewards.values().map { data ->
                            button {
                                title = data.title
                                item = data.icon
                            }
                        }.toMutableList()
                    }.open(player)
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
                donateMenu.open(it.player)
            }
        }
        command("menu") { player, _ -> donateMenu.open(player) }
        command("donate") { player, _ -> donateMenu.open(player) }
    }

}