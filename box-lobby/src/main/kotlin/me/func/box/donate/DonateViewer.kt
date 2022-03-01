package me.func.box.donate

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import implario.ListUtils
import me.func.box.ClickServer
import me.func.box.ServerType
import me.func.box.User
import me.func.box.app
import me.func.box.cosmetic.*
import me.func.box.cosmetic.SeasonKit.seasonCounter
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
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
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import ru.cristalix.npcs.data.NpcBehaviour
import ru.cristalix.npcs.server.Npc
import ru.cristalix.npcs.server.Npcs
import sun.audio.AudioPlayer
import java.util.function.Consumer

class DonateViewer : Listener {

    private val costume = ControlledInventory.builder()
        .title("Костюмы")
        .rows(4)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XOOOOOOOX",
                    "XOOOOOOOX",
                    "XXXOOOOXX",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                Armor.values().forEach { tag ->
                    val user = app.getUser(player)
                    val has = user.stat.skins!!.contains(tag.getCode())
                    val current = user.stat.currentSkin == tag.getCode()
                    contents.add('O', ClickableItem.of(tag.getItem(current, has)) {
                        if (current)
                            return@of
                        if (has) {
                            user.stat.currentSkin = tag.getCode()
                            player.closeInventory()
                            return@of
                        }
                        ControlledInventory.builder()
                            .title(tag.getRare().color + tag.getRare().title + " §f" + tag.getTitle())
                            .rows(1)
                            .columns(9)
                            .provider(object : InventoryProvider {
                                override fun init(player: Player, contents: InventoryContents) {
                                    contents.setLayout(
                                        "XXKKKKXOP",
                                    )
                                    contents.add('K', ClickableItem.empty(item {
                                        text("§bШлем")
                                        nbt("armors", tag.getCode())
                                        type = Material.DIAMOND_HELMET
                                    }.build()))
                                    contents.add('K', ClickableItem.empty(item {
                                        text("§bНагрудник")
                                        nbt("armors", tag.getCode())
                                        type = Material.DIAMOND_CHESTPLATE
                                    }.build()))
                                    contents.add('K', ClickableItem.empty(item {
                                        text("§bПоножи")
                                        nbt("armors", tag.getCode())
                                        type = Material.DIAMOND_LEGGINGS
                                    }.build()))
                                    contents.add('K', ClickableItem.empty(item {
                                        text("§bБотинки")
                                        nbt("armors", tag.getCode())
                                        type = Material.DIAMOND_BOOTS
                                    }.build()))
                                    contents.add('P', ClickableItem.of(item {
                                        text("§cВыйти")
                                        nbt("other", "cancel")
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        player.closeInventory()
                                    })
                                    contents.add('O', ClickableItem.of(item {
                                        text("§aКупить за ${tag.getPrice()} кристаликов")
                                        nbt("other", "access")
                                        enchant(Enchantment.LUCK, 1)
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        buy(user, tag.getPrice(), "Покупка сета ${tag.getCode()}", tag)
                                    })
                                    contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
                                }
                            }).build().open(player)
                    })
                }
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private val sword = ControlledInventory.builder()
        .title("Скины")
        .rows(4)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XOOOOOOOX",
                    "XOOOOOOOX",
                    "XXXXOXXXX",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                Sword.values().forEach { tag ->
                    if (tag == Sword.NONE)
                        return@forEach
                    val user = app.getUser(player)!!
                    val has = user.stat.swords!!.contains(tag)
                    val current = user.stat.currentSword == tag
                    contents.add('O', ClickableItem.of(tag.getItem(current, has)) {
                        if (current)
                            return@of
                        if (has) {
                            user.stat.currentSword = tag
                            player.closeInventory()
                            return@of
                        }
                        ControlledInventory.builder()
                            .title(tag.getRare().color + tag.getRare().title + " §f" + tag.getTitle())
                            .rows(1)
                            .columns(9)
                            .provider(object : InventoryProvider {
                                override fun init(player: Player, contents: InventoryContents) {
                                    contents.setLayout(
                                        "XXXXKXXOP",
                                    )
                                    contents.add('K', ClickableItem.empty(item {
                                        text(tag.getRare().color + tag.getRare().title + " §f" + tag.getTitle())
                                        nbt("weapons_other", tag.getCode())
                                        type = Material.DIAMOND_SWORD
                                    }.build()))
                                    contents.add('P', ClickableItem.of(item {
                                        text("§cВыйти")
                                        nbt("other", "cancel")
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        player.closeInventory()
                                    })
                                    contents.add('O', ClickableItem.of(item {
                                        text("§aКупить за ${tag.getPrice()} кристаликов")
                                        nbt("other", "access")
                                        enchant(Enchantment.LUCK, 1)
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        buy(user, tag.getPrice(), "Покупка скина на меч ${tag.getCode()}", tag)
                                    })
                                    contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
                                }
                            }).build().open(player)
                    })
                }
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private val starter = ControlledInventory.builder()
        .title("Начальные наборы")
        .rows(3)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XOOOOOOOX",
                    "XXXOOOOXX",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                Starter.values().forEach { tag ->
                    val user = app.getUser(player)!!
                    val has = user.stat.starters!!.contains(tag)
                    val current = user.stat.currentStarter == tag
                    if (tag == Starter.NONE)
                        return@forEach
                    val item = tag.getItem(current, has)
                    contents.add('O', ClickableItem.of(item) {
                        if (current)
                            return@of
                        if (has) {
                            user.stat.currentStarter = tag
                            player.closeInventory()
                            return@of
                        }
                        ControlledInventory.builder()
                            .title(tag.getRare().color + tag.getRare().title + " §f" + tag.getTitle())
                            .rows(1)
                            .columns(9)
                            .provider(object : InventoryProvider {
                                override fun init(player: Player, contents: InventoryContents) {
                                    contents.setLayout(
                                        "XXXXKXXOP",
                                    )
                                    contents.add('K', ClickableItem.empty(item))
                                    contents.add('P', ClickableItem.of(item {
                                        text("§cВыйти")
                                        nbt("other", "cancel")
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        player.closeInventory()
                                    })
                                    contents.add('O', ClickableItem.of(item {
                                        if (tag.getPrice() < 100000) text("§eКупить за ${tag.getPrice()} монет")
                                        else text("§cСезонный предмет")
                                        nbt("other", "access")
                                        enchant(Enchantment.LUCK, 1)
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        if (user.stat.money <= tag.getPrice()) {
                                            player.sendMessage(Formatting.error("Недостаточно монет!"))
                                            player.closeInventory()
                                            return@of
                                        }
                                        tag.give(user)

                                        player.closeInventory()
                                        player.sendMessage(Formatting.fine("Вы успешно купили стартовый набор!"))
                                    })
                                    contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
                                }
                            }).build().open(player)
                    })
                }
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private val money = ControlledInventory.builder()
        .title("§eЭто меню подтверждения")
        .rows(2)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                val user = app.getUser(player)
                contents.setLayout(
                    "XXFXFXFXX",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                MoneyBuy.values().forEach { money ->
                    contents.add('F', ClickableItem.of(item {
                        type = Material.CLAY_BALL
                        nbt("other", money.getCode())
                        text(money.getTitle())
                    }.build()) {
                        buy(user, money.realPrice, "Покупка ${money.money} монет", money)
                    })
                }
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private val menu = ControlledInventory.builder()
        .title("Меню")
        .rows(1)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout("MNHXOXGPP")

                val user = app.getUser(player)!!

                contents.add('M', ClickableItem.of(item {
                    text("§eСообщения об убийстве")
                    nbt("other", "info")
                    type = Material.CLAY_BALL
                }.build()) {
                    killMessages.open(player)
                })

                contents.add('P', ClickableItem.of(item {
                    text("§eЭффекты разрушения кровати")
                    type = Material.SLIME_BALL
                }.build()) {
                    breakBed.open(player)
                })

                contents.add('N', ClickableItem.of(item {
                    text("§eКостюмы")
                    nbt("armors", "chicken")
                    type = Material.DIAMOND_HELMET
                }.build()) {
                    costume.open(player)
                })
                contents.add('H', ClickableItem.of(item {
                    text("§eСкины")
                    nbt("weapons_other", 34.toString())
                    type = Material.DIAMOND_SWORD
                }.build()) {
                    sword.open(player)
                })
                contents.add('G', ClickableItem.of(item {
                    text("§eСтартовые наборы")
                    type = Material.TNT
                    amount = 16
                }.build()) {
                    starter.open(player)
                })
                contents.add('P', ClickableItem.of(item {
                    text("§eМонеты")
                    nbt("other", "coin4")
                    type = Material.CLAY_BALL
                }.build()) {
                    money.open(player)
                })
                contents.add('O', ClickableItem.of(item {
                    text("§bCезонный кит\n\n §e◉ §6Набор снежного короля\n §e◉ §6Костюм ледяного титана\n §e◉ §6Эффект замедления времени\n\n§7Скидка §a70%§7, предложение\n§7действует §aдо 31-го числа")
                    nbt("other", "new_booster_2")
                    type = Material.CLAY_BALL
                }.build()) {
                    ControlledInventory.builder()
                        .title("§bCезонный кит")
                        .rows(1)
                        .columns(9)
                        .provider(object : InventoryProvider {
                            override fun init(player: Player, contents: InventoryContents) {
                                SeasonKit.fill(contents)

                                contents.add('P', ClickableItem.of(item {
                                    text("§cВыйти")
                                    nbt("other", "cancel")
                                    type = Material.CLAY_BALL
                                }.build()) {
                                    player.closeInventory()
                                })
                                contents.add('O', ClickableItem.of(item {
                                    text("§aКупить за ${SeasonKit.getPrice()} кристаликов")
                                    nbt("other", "access")
                                    nbt("HideFlags", 63)
                                    enchant(Enchantment.LUCK, 0)
                                    type = Material.CLAY_BALL
                                }.build()) {
                                    buy(user, SeasonKit.getPrice(), "Покупка сезонного кита $seasonCounter", SeasonKit)
                                })
                            }
                        }).build().open(player)
                })

                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private val killMessages = ControlledInventory.builder()
        .title("Сообщения об смерти")
        .rows(4)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXXXXXXXX",
                    "OOOOOOOOO",
                    "OOOOOOOOO",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                KillMessage.values().forEach { tag ->
                    val user = app.getUser(player)!!
                    val has = user.stat.killMessages.contains(tag)
                    val current = user.stat.currentKillMessage == tag
                    contents.add('O', ClickableItem.of(tag.getItem(current, has)) {
                        if (current)
                            return@of
                        if (has) {
                            user.stat.currentKillMessage = tag
                            player.closeInventory()
                            return@of
                        }
                        ControlledInventory.builder()
                            .title(tag.getRare().color + tag.getRare().title + " §f" + tag.getTitle())
                            .rows(1)
                            .columns(9)
                            .provider(object : InventoryProvider {
                                override fun init(player: Player, contents: InventoryContents) {
                                    contents.setLayout(
                                        "XXXXKXXOP",
                                    )
                                    contents.add('K', ClickableItem.empty(item {
                                        text(tag.getRare().color + tag.getRare().title + " §f" + tag.getTitle())
                                        type = tag.getItemStack().getType()
                                    }.build()))
                                    contents.add('P', ClickableItem.of(item {
                                        text("§cВыйти")
                                        nbt("other", "cancel")
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        player.closeInventory()
                                    })
                                    contents.add('O', ClickableItem.of(item {
                                        text("§aКупить за ${tag.getPrice()} кристаликов")
                                        nbt("other", "access")
                                        enchant(Enchantment.LUCK, 1)
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        buy(user, tag.getPrice(), "Покупка сообщения ${tag.getTitle()}", tag)
                                    })
                                    contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
                                }
                            }).build().open(player)
                    })
                }
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private val breakBed = ControlledInventory.builder()
        .title("Эффекты разрушения кровати")
        .rows(4)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXXXXXXXX",
                    "OOOOOOOOO",
                    "OOOOOOOOO",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                BreakBedEffect.values().forEach { tag ->
                    val user = app.getUser(player)!!
                    val has = user.stat.breakBedEffects.contains(tag)
                    val current = user.stat.currentBreakBedEffect == tag
                    contents.add('O', ClickableItem.of(tag.getItem(current, has)) {
                        if (current)
                            return@of
                        if (has) {
                            user.stat.currentBreakBedEffect = tag
                            player.closeInventory()
                            return@of
                        }
                        ControlledInventory.builder()
                            .title(tag.getRare().color + tag.getRare().title + " §f" + tag.getTitle())
                            .rows(1)
                            .columns(9)
                            .provider(object : InventoryProvider {
                                override fun init(player: Player, contents: InventoryContents) {
                                    contents.setLayout(
                                        "XXXXKXXOP",
                                    )
                                    contents.add('K', ClickableItem.empty(item {
                                        text(tag.getRare().color + tag.getRare().title + " §f" + tag.getTitle())
                                        type = tag.getItemStack().getType()
                                    }.build()))
                                    contents.add('P', ClickableItem.of(item {
                                        text("§cВыйти")
                                        nbt("other", "cancel")
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        player.closeInventory()
                                    })
                                    contents.add('O', ClickableItem.of(item {
                                        text("§aКупить за ${tag.getPrice()} кристаликов")
                                        nbt("other", "access")
                                        enchant(Enchantment.LUCK, 1)
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        buy(user, tag.getPrice(), "Покупка эффекта ${tag.getTitle()}", tag)
                                    })
                                    contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
                                }
                            }).build().open(player)
                    })
                }
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private fun buy(user: User, money: Int, desc: String, donate: Donate) {
        val player = user.player!!
        if (player.isOp) {
            donate.give(user)
        }
        ISocketClient.get().writeAndAwaitResponse<MoneyTransactionResponsePackage>(
            MoneyTransactionRequestPackage(
                player.uniqueId,
                money,
                true,
                desc
            )
        ).thenAccept {
            if (it.errorMessage != null) {
                player.sendMessage(Formatting.error(it.errorMessage))
                return@thenAccept
            }
            if (!user.session.isActive) {
                player.sendMessage(Formatting.error("Что-то пошло не так... Попробуйте перезайти"))
                return@thenAccept
            }
            donate.give(user)
            player.closeInventory()
            player.sendMessage(Formatting.fine("Спасибо за поддержку разработчиков!"))
        }
    }

    private val servers = ControlledInventory.builder()
        .title("Выбор игры")
        .rows(1)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XNXNNNXNX",
                )

                contents.add('N', ClickableItem.of(item {
                    text("§c§l4§fx§c§l4 §eПВП 1.9")
                    type = Material.DIAMOND_PICKAXE
                }.build()) {
                    ClickServer("BOXN", 16).accept(player)
                })
                contents.add('N', ClickableItem.of(item {
                    text("§c§l1§fx§c§l4 §eLUCKY")
                    type = Material.IRON_PICKAXE
                }.build()) {
                    ClickServer("BOX4", 8).accept(player)
                })
                contents.add('N', ClickableItem.of(item {
                    text("§c§l4§fx§c§l4 §eПВП 1.8")
                    type = Material.ENDER_PEARL
                }.build()) {
                    ClickServer("BOXS", 16).accept(player)
                })
                contents.add('N', ClickableItem.of(item {
                    text("§c§l4§fx§c§l4 §eLUCKY")
                    type = Material.IRON_SWORD
                }.build()) {
                    ClickServer("BOX8", 16).accept(player)
                })
                contents.add('N', ClickableItem.of(item {
                    text("§c§l25§fx§c§l4 §eПВП 1.8")
                    type = Material.WOOD_PICKAXE
                }.build()) {
                    ClickServer("BOX5", 100).accept(player)
                })

                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private val stat = ControlledInventory.builder()
        .title("Статистика")
        .rows(1)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXXXNXXXX",
                )

                val stat = app.getUser(player)!!.stat
                contents.add('N', ClickableItem.empty(item {
                    text(
                        "§bВаша статистика\n\n" +
                                "§fПобед: §b${stat.wins}\n" +
                                "§fУбийств: §c${stat.kills}\n" +
                                "§fСмертей: §f${stat.deaths}\n" +
                                "§fК/Д: §c${((stat.kills / (stat.deaths + 1)) * 100 % 100) / 100.0}\n" +
                                "§fДенег: §e${stat.money} монет\n" +
                                "§fЛакиблоков: §e${stat.luckyOpened}\n" +
                                "§fИгр сыграно: §f${stat.games}\n"
                    )
                    type = Material.PAPER
                }.build()))

                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    init {
        Npcs.spawn(
            Npc.builder()
                .location(Location(app.worldMeta.world, -251.0, 110.0, 30.0, 115f, 0f))
                .name("§e§lМеню")
                .behaviour(NpcBehaviour.STARE_AT_PLAYER)
                .skinUrl("https://webdata.c7x.dev/textures/skin/479cb4df-7024-11ea-acca-1cb72caa35fd")
                .skinDigest("479cb4df-702411eaacca1cb72caa35fd")
                .type(EntityType.PLAYER)
                .onClick { player -> menu.open(player) }.build()
        )

        B.regCommand({ player: Player, strings: Array<String> ->
            menu.open(player)
            null
        }, "menu", "donate")
    }

    private val menuItem = item {
        type = Material.EMERALD
        text("§aКосметика")
    }.build()

    private val serversItem = item {
        type = Material.COMPASS
        text("§bБыстрая игра")
    }.build()

    private val paperItem = item {
        type = Material.PAPER
        text("§bСтатистика")
    }.build()

    @EventHandler
    fun PlayerJoinEvent.handle() {
        player.inventory.setItem(0, serversItem)
        player.inventory.setItem(4, menuItem)
        player.inventory.setItem(8, paperItem)
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (player.itemInHand.getType() == Material.COMPASS) {
            val realm = ServerType.BOXS
            ClickServer(realm.name, realm.slot).accept(player)
        }
        if (player.itemInHand.getType() == Material.EMERALD)
            menu.open(player)
        if (player.itemInHand.getType() == Material.PAPER)
            stat.open(player)
    }
}