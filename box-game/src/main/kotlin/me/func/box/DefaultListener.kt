package me.func.box

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import dev.implario.bukkit.item.item
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.item.Items
import ru.cristalix.core.realm.RealmId

class DefaultListener : Listener {

    private val visible = PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, true)
    private val regen = PotionEffect(PotionEffectType.REGENERATION, 100000, 0, true)
    private val back = item {
        type = Material.CLAY_BALL
        nbt("other", "cancel")
        text("§cВернуться")
    }.build()

    @EventHandler
    fun PlayerJoinEvent.handle() {
        player.sendMessage("Привет! Игра на стадии теста, если вы нашли ошибку пишите сюда https://vk.com/funcid, и номер сервера снизу справа")

        app.waitingBar.addViewer(player.uniqueId)

        B.postpone(1) { player.teleport(app.spawn) }
        player.addPotionEffect(visible, true)
        player.addPotionEffect(regen, true)

        if (app.status == Status.STARTING) {
            player.inventory.setItem(8, back)
            app.teams.forEach {
                player.inventory.addItem(
                    Items.builder()
                        .displayName("Выбрать команду: " + it.color.chatFormat + it.color.teamName)
                        .type(Material.WOOL)
                        .color(it.color)
                        .build()
                )
            }
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        if (app.status == Status.STARTING) {
            app.waitingBar.removeViewer(player.uniqueId)
            return
        }
        if (player.gameMode == GameMode.SURVIVAL) {
            player.sendMessage(Formatting.error("Очень жаль! Надеемся ваша команда сильная без вас..."))
            player.inventory.forEach {
                if (it != null)
                    player.world.dropItemNaturally(player.location, it)
            }
        }
        app.teams.filter { it.players.contains(player.uniqueId) }
            .forEach { it.players.remove(player.uniqueId) }
        Winner.tryGetWinner()
    }

    @EventHandler
    fun BlockPlaceEvent.handle() {
        if (app.status == Status.STARTING)
            cancel = true
        if (blockPlaced.type == Material.BED_BLOCK) {
            player.sendMessage(Formatting.fine("Вы установили личную кровать!"))
            app.getUser(player)!!.bed = blockPlaced.location
        }
    }

    @EventHandler
    fun BlockGrowEvent.handle() {
        if (app.status == Status.STARTING)
            cancelled = true
    }

    @EventHandler
    fun PlayerDropItemEvent.handle() {
        if (app.status == Status.STARTING)
            cancel = true
    }

    @EventHandler
    fun FoodLevelChangeEvent.handle() {
        foodLevel = 20
    }

    @EventHandler
    fun EntityDamageByEntityEvent.handle() {
        if (entityType == EntityType.VILLAGER)
            cancelled = true
        if (app.status == Status.STARTING)
            cancelled = true
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        if (app.status == Status.STARTING)
            cancelled = true
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (app.status == Status.STARTING && material == Material.CLAY_BALL)
            Cristalix.transfer(listOf(player.uniqueId), RealmId.of(app.hub))
        if (app.status == Status.STARTING && material == Material.WOOL) {
            app.teams.filter {
                !it.players.contains(player.uniqueId) && it.color.woolData.toByte() == player.itemInHand.getData().data
            }.forEach { team ->
                if (team.players.size == app.slots / app.teams.size) {
                    player.sendMessage(Formatting.error("Ошибка! Команда заполена."))
                    return@forEach
                }
                app.teams.forEach { it.players.remove(player.uniqueId) }
                team.players.add(player.uniqueId)
                player.sendMessage(Formatting.fine("Вы выбрали команду: " + team.color.chatFormat + team.color.teamName))
            }
        }
    }

    @EventHandler
    fun PlayerDeathEvent.handle() {
        (entity as LivingEntity).health = 20.0
        cancelled = true
        val player = entity as CraftPlayer
        player.itemOnCursor = null

        droppedExp = 0
        deathMessage = null

        if (player.killer != null)
            app.getUser(player.killer)!!.stat!!.kills++
        app.getUser(player)!!.stat!!.deaths++

        B.postpone(1) {
            player.inventory.forEach {
                if (it != null)
                    player.world.dropItemNaturally(player.location, it)
            }
            if (player.openInventory != null && player.openInventory.topInventory != null)
                player.openInventory.topInventory.clear()
            player.inventory.clear()
            app.teams.filter { it.players.contains(entity.uniqueId) }
                .forEach { team ->
                    if (app.getUser(player)!!.bed != null) {
                        player.teleport(app.getUser(player)!!.bed)
                        player.inventory.addItem(app.woodPickaxe)
                        return@postpone
                    }
                    if (team.bed) {
                        player.bedSpawnLocation = team.location
                        entity.teleport(team.location)
                        player.inventory.addItem(app.woodPickaxe)
                    } else {
                        team.players.remove(player.uniqueId)
                        Winner.tryGetWinner()
                        player.gameMode = GameMode.SPECTATOR
                        player.sendMessage(Formatting.error("Вы проиграли."))
                    }
                    return@postpone
                }
        }
    }
}

object Winner {
    fun tryGetWinner() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            app.status = Status.END
            return
        } else {
            app.teams.forEach { it.players.removeIf { player -> Bukkit.getPlayer(player) == null } }
        }

        val list = app.teams.filter { it.players.size > 0 }
        if (list.size == 1) {
            B.bc(" ")
            B.bc(" ")
            B.bc("" + list[0].color.chatColor + list[0].color.teamName + " §f победили!")
            B.bc(" ")
            B.bc(" ")
            app.status = Status.END
            list[0].players.forEach {
                val user = app.getUser(it)
                if (user?.stat != null) {
                    user.stat!!.wins++
                }
            }
        }
    }
}

