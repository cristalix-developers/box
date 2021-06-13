package me.func.box

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import dev.implario.bukkit.item.item
import io.netty.buffer.Unpooled
import net.minecraft.server.v1_12_R1.PacketDataSerializer
import net.minecraft.server.v1_12_R1.PacketPlayOutCustomPayload
import org.bukkit.*
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.item.Items
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.realm.RealmStatus
import java.nio.charset.StandardCharsets


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
        app.waitingBar.addViewer(player.uniqueId)

        B.postpone(1) { player.teleport(app.spawn) }
        player.addPotionEffect(visible, true)
        player.addPotionEffect(regen, true)

        player.inventory.clear()

        (player as CraftPlayer).handle.playerConnection.sendPacket(
            PacketPlayOutCustomPayload(
                "xdark:pvp", PacketDataSerializer(
                    Unpooled.wrappedBuffer("{\"renderSwordAsShield\": false}".toByteArray(StandardCharsets.UTF_8))
                )
            )
        )

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
    fun PlayerBedEnterEvent.handle() {
        if (app.status == Status.STARTING)
            cancel = true
        player.sendMessage(Formatting.fine("Точка возраждения установлена!"))
        app.getUser(player)!!.bed = bed.location
    }

    @EventHandler
    fun BlockGrowEvent.handle() {
        if (app.status == Status.STARTING)
            cancelled = true
    }

    @EventHandler
    fun PlayerDropItemEvent.handle() {
        if (app.status == Status.STARTING || itemDrop.itemStack.getType() == Material.WOOD_PICKAXE)
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

        if (player.killer != null) {
            val user = app.getUser(player.killer)!!
            user.stat!!.kills++
            user.tempKills++
        }
        app.getUser(player)!!.stat!!.deaths++

        B.postpone(1) {
            player.inventory.forEach {
                if (it != null && it.getType() != Material.WOOD_PICKAXE)
                    player.world.dropItemNaturally(player.location, it)
            }
            if (player.openInventory != null && player.openInventory.topInventory != null)
                player.openInventory.topInventory.clear()
            player.inventory.clear()
            app.teams.filter { it.players.contains(entity.uniqueId) }
                .forEach { team ->
                    var message = "" + team.color.chatColor + player.name + " §fубит"
                    if (player.killer != null)
                        message += " игроком " + player.killer.name
                    val user = app.getUser(player)!!
                    if (user.bed != null && user.bed!!.block.type == Material.BED_BLOCK) {
                        player.teleport(app.getUser(player)!!.bed)
                        player.inventory.addItem(app.woodPickaxe)
                        B.bc(Formatting.fine(message))
                        return@postpone
                    } else
                        user.bed = null
                    if (team.bed) {
                        entity.teleport(team.location)
                        player.inventory.addItem(app.woodPickaxe)
                    } else {
                        team.players.remove(player.uniqueId)
                        Winner.tryGetWinner()
                        player.gameMode = GameMode.SPECTATOR
                        player.sendTitle("Вы проиграли!", "Наблюдение...")
                        message = "§e§lФИНАЛЬНОЕ УБИЙСТВО! $message"
                    }
                    B.bc(Formatting.fine(message))
                    return@postpone
                }
        }
    }

    @EventHandler
    fun AsyncPlayerPreLoginEvent.handle() {
        playerProfile.properties.forEach { profileProperty ->
            if (profileProperty.value == "PARTY_WARP") {
                if (IRealmService.get().currentRealmInfo.status != RealmStatus.WAITING_FOR_PLAYERS) {
                    disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Сейчас нельзя зайти на этот сервер")
                    loginResult = AsyncPlayerPreLoginEvent.Result.KICK_OTHER
                }
            }
        }
    }

    @EventHandler
    fun AsyncPlayerChatEvent.handle() {
        if (player.gameMode == GameMode.SPECTATOR) {
            Bukkit.getOnlinePlayers().forEach {
                if (it.gameMode == GameMode.SPECTATOR)
                    it.sendMessage(player.name + " >§7 " + ChatColor.stripColor(message))
            }
            cancel = true
            return
        }
        if (message.startsWith("!")) {
            val team = app.teams.filter { boxTeam -> boxTeam.players.contains(player.uniqueId) }
            if (team.isNotEmpty()) {
                team[0].players.mapNotNull { Bukkit.getPlayer(it) }.forEach {
                    it.sendMessage("" + team[0].color.chatColor + "${player.name} > ${message.drop(1)}")
                }
                cancel = true
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
            B.bc("§e§lТОП УБИЙСТВ")
            Bukkit.getOnlinePlayers().map { app.getUser(it) }.sortedBy { -it!!.tempKills }.subList(0, 3)
                .forEachIndexed { index, user ->
                    B.bc(" §l${index + 1}. §e" + user!!.player.name + " §с" + user.tempKills + " убийств")
                }
            B.bc(" ")
            B.bc(" ")
            app.status = Status.END
            list[0].players.forEach {
                val user = app.getUser(it)
                if (user?.stat != null) {
                    user.stat!!.wins++
                    user.player.sendTitle("§aПОБЕДА", "§aвы выиграли!")
                    val firework = user.player.world.spawn(user.player.location, Firework::class.java)
                    val meta = firework.fireworkMeta
                    meta.addEffect(
                        FireworkEffect.builder()
                            .flicker(true)
                            .trail(true)
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .with(FireworkEffect.Type.BALL)
                            .with(FireworkEffect.Type.BALL_LARGE)
                            .withColor(Color.AQUA)
                            .withColor(Color.YELLOW)
                            .withColor(Color.RED)
                            .withColor(Color.WHITE)
                            .build()
                    )
                    meta.power = 0
                    firework.fireworkMeta = meta
                }
            }
            Bukkit.getOnlinePlayers().forEach {
                if (list[0].players.contains(it.uniqueId))
                    return@forEach
                it.sendTitle("§cПОРАЖЕНИЕ", "§cвы проиграли")
            }
        }
    }
}

