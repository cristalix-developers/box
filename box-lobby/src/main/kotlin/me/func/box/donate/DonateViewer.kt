package me.func.box.donate

import dev.implario.bukkit.item.item
import me.func.box.User
import me.func.box.app
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
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
import java.util.function.Consumer

class DonateViewer {

    private val costume = ControlledInventory.builder()
        .title("Костюмы")
        .rows(2)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XOOOOOOOX",
                    "XOOOOOOOX",
                )

                Armor.values().forEach { tag ->
                    val user = app.getUser(player)!!
                    val has = user.stat.skins!!.contains(tag.getCode())
                    val current = user.stat.currentSkin == tag.getCode()
                    contents.add('O', ClickableItem.of(item {
                        text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + tag.getRare().color + tag.getRare().title)
                        nbt("armors", tag.getCode())
                        if (current)
                            enchant(Enchantment.LUCK, 1)
                        type = Material.DIAMOND_HELMET
                    }.build()) {
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
                                        buy(user, tag.getPrice(), "Покупка сета ${tag.getCode()}") {
                                            if (user.stat.skins == null)
                                                user.stat.skins = arrayListOf(tag.getCode())
                                            else
                                                user.stat.skins!!.add(tag.getCode())
                                            user.stat.currentSkin = tag.getCode()
                                        }
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
        .rows(2)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXOOOOOXX",
                    "XXOOOOOXX",
                )

                Sword.values().forEach { tag ->
                    if (tag == Sword.NONE)
                        return@forEach
                    val user = app.getUser(player)!!
                    val has = user.stat.swords!!.contains(tag)
                    val current = user.stat.currentSword == tag
                    contents.add('O', ClickableItem.of(item {
                        text((if (current) "§aВЫБРАНО" else if (has) "§eВыбрать" else "§bПосмотреть") + "\n§7Редкость: " + tag.getRare().color + tag.getRare().title)
                        nbt("weapons_other", tag.getCode())
                        if (current)
                            enchant(Enchantment.LUCK, 1)
                        type = Material.DIAMOND_SWORD
                    }.build()) {
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
                                        buy(user, tag.getPrice(), "Покупка скина на меч ${tag.getCode()}") {
                                            if (user.stat.swords == null)
                                                user.stat.swords = arrayListOf(tag)
                                            else
                                                user.stat.swords!!.add(tag)
                                            user.stat.currentSword = tag
                                        }
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
        .rows(1)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XOOOOOOOO",
                )

                Starter.values().forEach { tag ->
                    if (tag == Starter.NONE)
                        return@forEach
                    val user = app.getUser(player)!!
                    val has = user.stat.starters!!.contains(tag)
                    val current = user.stat.currentStarter == tag
                    contents.add('O', ClickableItem.of(tag.lore) {
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
                                    contents.add('K', ClickableItem.empty(tag.lore))
                                    contents.add('P', ClickableItem.of(item {
                                        text("§cВыйти")
                                        nbt("other", "cancel")
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        player.closeInventory()
                                    })
                                    contents.add('O', ClickableItem.of(item {
                                        text("§eКупить за ${tag.getPrice()} монет")
                                        nbt("other", "access")
                                        enchant(Enchantment.LUCK, 1)
                                        type = Material.CLAY_BALL
                                    }.build()) {
                                        if (user.stat.money <= tag.getPrice()) {
                                            player.sendMessage(Formatting.error("Недостаточно монет!"))
                                            player.closeInventory()
                                            return@of
                                        }
                                        if (user.stat.starters == null)
                                            user.stat.starters = arrayListOf(tag)
                                        else
                                            user.stat.starters!!.add(tag)
                                        user.stat.currentStarter = tag
                                        user.stat.money -= tag.getPrice()

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
        .rows(1)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                val user = app.getUser(player)!!
                contents.setLayout(
                    "XXFXFXFXX",
                )

                contents.add('F', ClickableItem.of(item {
                    type = Material.CLAY_BALL
                    nbt("other", "coin2")
                    text("§aКупить 1`000 монет\n\n§7Цена: §b19 кристаликов\n§7Скидка: §cнет")
                }.build()) {
                    buy(user, 19, "Покупка 1`000 монет") {
                        it.stat.money += 1000
                    }
                })
                contents.add('F', ClickableItem.of(item {
                    type = Material.CLAY_BALL
                    nbt("other", "coin3")
                    text("§aКупить 10`000 монет\n\n§7Цена: §b159 кристаликов\n§7Скидка: §a20%")
                }.build()) {
                    buy(user, 159, "Покупка 10`000 монет") {
                        it.stat.money += 10000
                    }
                })
                contents.add('F', ClickableItem.of(item {
                    type = Material.CLAY_BALL
                    nbt("other", "coin4")
                    text("§aКупить 50`000 монет\n\n§7Цена: §b799 кристаликов\n§7Скидка: §a30%")
                }.build()) {
                    buy(user, 799, "Покупка 50`000 монет") {
                        it.stat.money += 50000
                    }
                })
                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
            }
        }).build()

    private fun buy(user: User, money: Int, desc: String, accept: Consumer<User>) {
        val player = user.player!!
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
            accept.accept(user)
            player.closeInventory()
            player.sendMessage(Formatting.fine("Спасибо за поддержку разработчиков!"))
        }
    }

    private val menu = ControlledInventory.builder()
        .title("Меню")
        .rows(3)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XXXXXXXXX",
                    "XNXHXGXPX",
                    "XXXXXXXXX",
                )

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
    }
}