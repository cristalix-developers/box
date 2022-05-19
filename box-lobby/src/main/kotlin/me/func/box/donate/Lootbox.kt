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
import me.func.mod.Anime
import me.func.mod.conversation.ModTransfer
import me.func.mod.selection.button
import me.func.mod.selection.selection
import me.func.mod.util.after
import me.func.mod.util.nbt
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import ru.cristalix.core.formatting.Formatting

object Lootbox : Listener {

    private val coin = item { type = Material.CLAY_BALL }.nbt("other", "coin4")
    private val dropList = Armor.values().map { it }.plus(Sword.values().filter { it != Sword.NONE }.map { it })
    private val lootboxMenu = selection {
        title = "Покупка ящика"
        rows = 3
        columns = 3
        storage = MutableList(9) {
            button {
                title = "§bЛутбокс"
                description = "мечи, костюмы, монеты"
                price = 5000
                material(Material.ENDER_CHEST)
                onClick { player, _, _ ->
                    Anime.close(player)
                    val user = app.getUser(player)
                    if (user.stat.money <= 5000) {
                        Anime.killboardMessage(player, Formatting.error("Недостаточно средств!"))
                        return@onClick
                    }
                    user.stat.money -= 5000
                    open(user)
                }
            }
        }
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
            }
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
            }
        }

        B.bc(Formatting.fine("§e" + user.name + " §fнашел ${drop.getRare().color + drop.getRare().title.toLowerCase()} предмет! ${drop.getRare().color}" + drop.getTitle()))

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
            isCancelled = true
            after(2) {
                val player = player as Player
                val user = app.getUser(player)
                lootboxMenu.apply {
                    money = "Монет " + user.stat.money
                }.open(player)
            }
        }
    }
}
