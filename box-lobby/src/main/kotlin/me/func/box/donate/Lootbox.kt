package me.func.box.donate

import clepto.bukkit.B
import dev.implario.bukkit.item.item
import implario.ListUtils
import me.func.box.User
import me.func.box.app
import me.func.box.cosmetic.Armor
import me.func.box.cosmetic.Donate
import me.func.box.cosmetic.Rare
import me.func.box.cosmetic.Sword
import me.func.mod.conversation.ModTransfer
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.inventory.ClickableItem
import ru.cristalix.core.inventory.ControlledInventory
import ru.cristalix.core.inventory.InventoryContents
import ru.cristalix.core.inventory.InventoryProvider

object Lootbox : Listener {

    private val coin = CraftItemStack.asNMSCopy(item {
        nbt("other", "coin4")
        type = Material.CLAY_BALL
    }.build())

    private val dropList = Armor.values().map { it }
        .plus(Sword.values().filter { it != Sword.NONE }.map { it })

    private val lootbox = ControlledInventory.builder()
        .title("Покупка ящика")
        .rows(1)
        .columns(9)
        .provider(object : InventoryProvider {
            override fun init(player: Player, contents: InventoryContents) {
                val user = app.getUser(player)

                contents.setLayout(
                    "XXXXOXXXX",
                )
                contents.add('O', ClickableItem.of(item {
                    text("§6Открыть ящик\n\n§7Цена: §e5`000 монет\n\n§7Выпадают монеты и костюм/меч\n§aЕсли предмет уже есть,\n§aвы получите монеты!")
                    type = Material.ENDER_CHEST
                }.build()) {
                    if (user.stat.money <= 5000) {
                        player.sendMessage(Formatting.error("Недостаточно средств!"))
                        player.closeInventory()
                        return@of
                    }

                    user.stat.money -= 5000

                    open(user)

                    contents.fillMask('X', ClickableItem.empty(ItemStack(Material.AIR)))
                })
            }
        }).build()

    init {
        B.regCommand({ player, _ ->
            player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1f, 2f)
            null
        }, "lootboxsound")
    }

    fun open(user: User) {
        val moneyDrop = (Math.random() * 300).toInt() + 100

        user.stat.money += moneyDrop

        var drop = ListUtils.random(dropList) as Donate

        repeat(2) {
            if (drop.getRare() == Rare.LEGENDARY)
                drop = ListUtils.random(dropList) as Donate
        }

        val item = if (drop is Sword) {
            if (user.stat.swords == null) user.stat.swords = arrayListOf(drop as Sword)
            else {
                if (user.stat.swords!!.contains(drop)) {
                    user.player!!.sendMessage(Formatting.fine("У вас уже есть §b" + drop.getTitle() + "§f, замена на §e2000§f монет!"))
                    user.stat.money += 2000
                } else
                    user.stat.swords!!.add(drop as Sword)
            }
            item {
                nbt("weapons_other", drop.getCode())
                type = Material.DIAMOND_SWORD
            }.build()
        } else {
            if (user.stat.skins == null) user.stat.skins = arrayListOf(drop.getCode())
            else {
                if (user.stat.skins!!.contains(drop.getCode())) {
                    user.player!!.sendMessage(Formatting.fine("У вас уже есть §b" + drop.getTitle() + "§f, замена на §e2000§f монет!"))
                    user.stat.money += 2000
                } else
                    user.stat.skins!!.add(drop.getCode())
            }
            item {
                nbt("armors", drop.getCode())
                type = Material.DIAMOND_HELMET
            }.build()
        }

        B.bc(Formatting.fine("§e" + user.name + " §fнашел ${drop.getRare().color + drop.getRare().title.toLowerCase()} предмет! ${drop.getRare().color}" + drop.getTitle()))

        user.player!!.closeInventory()

        ModTransfer()
            .integer(2)
            .item(CraftItemStack.asNMSCopy(item))
            .string(drop.getTitle())
            .string(drop.getRare().name)
            .item(coin)
            .string("§e$moneyDrop монет")
            .string("")
            .send("lootbox", user.player)
    }

    @EventHandler
    fun InventoryOpenEvent.handle() {
        if (inventory.type == InventoryType.ENDER_CHEST) {
            B.postpone(1) { lootbox.open(player as Player) }
        }
    }
}
