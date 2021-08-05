package me.func.box.donate

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import implario.ListUtils
import me.func.box.ClickServer
import me.func.box.ServerType
import me.func.box.User
import me.func.box.app
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
                    "XXXOXOXXX",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                me.func.box.Armor.values().forEach { tag ->
                    val user = app.getUser(player)!!
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
        .rows(3)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XOOOOOOOX",
                    "XOOOOOOOX",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                me.func.box.Sword.values().forEach { tag ->
                    if (tag == me.func.box.Sword.NONE)
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
        .rows(3)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout(
                    "XOOOOOOOX",
                    "XXXOXOXXX",
                    "XXXXLXXXX"
                )
                contents.add('L', ClickableItem.of(item {
                    type = Material.BARRIER
                    text("§cНазад")
                }.build()) {
                    player.performCommand("menu")
                })
                me.func.box.Starter.values().forEach { tag ->
                    val user = app.getUser(player)!!
                    val has = user.stat.starters!!.contains(tag)
                    val current = user.stat.currentStarter == tag
                    if (tag == me.func.box.Starter.NONE)
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
        .rows(2)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                val user = app.getUser(player)!!
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

    private val menu = ControlledInventory.builder()
        .title("Меню")
        .rows(1)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                contents.setLayout("XNHXOXGPX")

                val user = app.getUser(player)!!

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
                    text("§bCезонный кит\n\n §e◉ §6Набор титана\n §e◉ §6Одеяния титана\n §e◉ §6Коса титана\n\n§7Скидка §a70%§7, предложение\n§7действует §aдо 6-го числа")
                    nbt("armors", "titans")
                    type = Material.DIAMOND_HELMET
                }.build()) {
                    ControlledInventory.builder()
                        .title("§bCезонный кит")
                        .rows(1)
                        .columns(9)
                        .provider(object : InventoryProvider {
                            override fun init(player: Player, contents: InventoryContents) {
                                contents.setLayout(
                                    "XXHHHXXOP",
                                )

                                val starter = me.func.box.Starter.TITAN
                                val armor = me.func.box.Armor.TITAN
                                val sword = me.func.box.Sword.M
                                val seasonCounter = 3

                                contents.add('H', ClickableItem.empty(starter.getItem()))
                                contents.add('H', ClickableItem.empty(armor.getItem()))
                                contents.add('H', ClickableItem.empty(sword.getItem()))
                                contents.add('P', ClickableItem.of(item {
                                    text("§cВыйти")
                                    nbt("other", "cancel")
                                    type = Material.CLAY_BALL
                                }.build()) {
                                    player.closeInventory()
                                })
                                contents.add('O', ClickableItem.of(item {
                                    text("§aКупить за 149 кристаликов")
                                    nbt("other", "access")
                                    enchant(Enchantment.LUCK, 1)
                                    type = Material.CLAY_BALL
                                }.build()) {
                                    buy(user, 149, "Покупка сезонного кита $seasonCounter") {
                                        if (user.stat.swords == null) user.stat.swords = arrayListOf(sword)
                                        else user.stat.swords!!.add(sword)
                                        user.stat.currentSword = sword
                                        if (user.stat.starters == null) user.stat.starters = arrayListOf(starter)
                                        else user.stat.starters!!.add(starter)
                                        user.stat.currentStarter = starter
                                        if (user.stat.skins == null) user.stat.skins = arrayListOf(armor.getCode())
                                        else user.stat.skins!!.add(armor.getCode())
                                        user.stat.currentSkin = armor.getCode()
                                    }
                                })
                                contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
                            }
                        }).build().open(player)
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
            if (!user.session.isActive) {
                player.sendMessage(Formatting.error("Что-то пошло не так... Попробуйте перезайти"))
                return@thenAccept
            }
            accept.accept(user)
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
                    text("§c§l2§fx§c§l4 §eLUCKY")
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
                                "§fК/Д: §c${((stat.kills / (stat.deaths + 1)) * 100 % 1) / 100}\n" +
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
        text("§bВыбор игры")
    }.build()

    private val randomItem = item {
        type = Material.DIAMOND_SWORD
        nbt("weapons_other", 41)
        text("§eСлучайная игра")
    }.build()

    private val paperItem = item {
        type = Material.PAPER
        text("§bСтатистика")
    }.build()

    @EventHandler
    fun PlayerJoinEvent.handle() {
        player.inventory.setItem(0, serversItem)
        player.inventory.setItem(1, randomItem)
        player.inventory.setItem(4, menuItem)
        player.inventory.setItem(8, paperItem)
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (player.itemInHand.getType() == Material.COMPASS)
            servers.open(player)
        if (player.itemInHand.getType() == Material.DIAMOND_SWORD) {
            val realm = ListUtils.random(ServerType.values())
            player.sendMessage(Formatting.fine("Случайный сервер - " + realm.title))
            player.sendMessage(Formatting.fine("Ожидайте 3 секунды..."))
            player.sendTitle("§eПриятной игры", "§lУДАЧИ!")
            B.postpone(60) { ClickServer(realm.name, realm.slot).accept(player) }
        }
        if (player.itemInHand.getType() == Material.EMERALD)
            menu.open(player)
        if (player.itemInHand.getType() == Material.PAPER)
            stat.open(player)
    }
}