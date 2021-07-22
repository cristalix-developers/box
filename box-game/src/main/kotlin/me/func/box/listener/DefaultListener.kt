package me.func.box.listener

import clepto.bukkit.B
import clepto.cristalix.Cristalix
import dev.implario.bukkit.item.item
import io.netty.buffer.Unpooled
import me.func.box.app
import me.func.box.data.Status
import me.func.box.info.Starter
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_12_R1.*
import org.bukkit.*
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockGrowEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import ru.cristalix.core.display.DisplayChannels
import ru.cristalix.core.display.messages.Mod
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.item.Items
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.realm.RealmStatus
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.math.min


class DefaultListener : Listener {

    private val visible = PotionEffect(PotionEffectType.NIGHT_VISION, 100000, 1, true)
    private val regen = PotionEffect(PotionEffectType.REGENERATION, 100000, 0, true)
    private val back = item {
        type = Material.CLAY_BALL
        nbt("other", "cancel")
        text("§cВернуться")
    }.build()

    private var modList = try {
        File("./mods/").listFiles()!!
            .filter { it.name.contains("bundle") }
            .map {
                val buffer = Unpooled.buffer()
                buffer.writeBytes(Mod.serialize(Mod(Files.readAllBytes(it.toPath()))))
                buffer
            }.toList()
    } catch (exception: Exception) {
        throw RuntimeException(exception)
    }

    @EventHandler
    fun PlayerJoinEvent.handle() {
        app.waitingBar.addViewer(player.uniqueId)

        B.postpone(1) {
            player.teleport(app.spawn)
            modList.forEach {
                app.getUser(player)!!.sendPacket(
                    PacketPlayOutCustomPayload(
                        DisplayChannels.MOD_CHANNEL,
                        PacketDataSerializer(it.retainedSlice())
                    )
                )
            }
        }
        player.addPotionEffect(visible, true)
        player.addPotionEffect(regen, true)

        player.inventory.clear()

        (player as CraftPlayer).handle.playerConnection.sendPacket(
            PacketPlayOutCustomPayload(
                "xdark:pvp", PacketDataSerializer(
                    Unpooled.wrappedBuffer("{\"renderSwordAsShield\": true}".toByteArray(StandardCharsets.UTF_8))
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
            val starter = app.getUser(player)!!.stat.currentStarter
            player.sendMessage(" ")
            player.sendMessage("§b―――――――――――――――――")
            player.sendMessage("      §eБедроковая коробка    ")
            player.sendMessage("§7Добывайте §eкамень§7, обменивайте")
            player.sendMessage("§7его на §eинструменты и оружие§7,")
            player.sendMessage("§7сломайте вражеские §cкровати§7 и")
            player.sendMessage("§7убейте §cврагов§7!")
            player.sendMessage(
                "§7Начальный набор: " +
                        if (starter == null || starter == Starter.NONE) "§cОтсутсвует"
                        else "§a${starter.title}"
            )
            player.sendMessage("§b―――――――――――――――――")
            player.sendMessage(" ")
        }
    }

    @EventHandler
    fun PlayerQuitEvent.handle() {
        app.teams.filter { it.players.contains(player.uniqueId) }
            .forEach { it.players.remove(player.uniqueId) }
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
        Winner.tryGetWinner()
    }

    @EventHandler
    fun PlayerBedEnterEvent.handle() {
        if (app.status == Status.STARTING)
            cancel = true
        if (app.teams.any { it.location != null && it.location!!.distanceSquared(bed.location) < 10 }) {
            player.sendMessage(Formatting.fine("Вы уже привязаны к этой кровати или это кровать врага."))
            return
        }
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
        if (app.status == Status.STARTING && entityType != EntityType.PLAYER) {
            damage = 0.0
            playDamageEffect(entity.location)
            if (Math.random() < 0.04 && damager is CraftPlayer)
                app.getUser(damager as CraftPlayer)!!.giveMoney(1)
        } else if (app.status == Status.STARTING) {
            cancelled = true
        }
    }

    @EventHandler
    fun EntityDamageEvent.handle() {
        if (app.status == Status.STARTING && entityType == EntityType.PLAYER)
            cancelled = true
    }

    @EventHandler
    fun PlayerInteractEvent.handle() {
        if (item != null && item.getType() == Material.TNT && action == Action.LEFT_CLICK_AIR) {
            val cloned = item.clone()
            cloned.setAmount(1)
            player.inventory.removeItem(cloned)
            val tnt = player.world.spawnEntity(player.location, EntityType.PRIMED_TNT) as TNTPrimed
            tnt.setMetadata("shooter", FixedMetadataValue(app, player.uniqueId.toString()))
            tnt.velocity = player.location.direction.normalize().multiply(2)
        }
        if (app.status == Status.STARTING && material == Material.CLAY_BALL)
            Cristalix.transfer(listOf(player.uniqueId), RealmId.of(app.hub))
        if (material == Material.COMPASS) {
            val user = app.getUser(player)!!
            user.compassToPlayer = !user.compassToPlayer

            player.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent(if (user.compassToPlayer) "§aКомпас нацелен на врагов" else "Компас указывает на кровать врага")
            )
        }
        if (app.status == Status.STARTING && material == Material.WOOL) {
            app.teams.filter {
                !it.players.contains(player.uniqueId) && it.color.woolData.toByte() == player.itemInHand.getData().data
            }.forEach { team ->
                if (team.players.size >= app.slots / app.teams.size) {
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
            user.tempKills++
        }
        app.getUser(player)!!.stat.deaths++

        B.postpone(1) {
            player.inventory.forEach {
                if (it != null && it.getType() != Material.WOOD_PICKAXE)
                    player.world.dropItemNaturally(player.location, it)
            }
            if (player.openInventory != null && player.openInventory.topInventory != null)
                player.openInventory.topInventory.clear()
            player.inventory.clear()
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 255, false, false))
            app.teams.filter { it.players.contains(entity.uniqueId) }
                .forEach { team ->
                    var message = "" + team.color.chatColor + player.name + " §fубит"
                    if (player.killer != null) {
                        app.getUser(player.killer)!!.giveMoney(app.killMoney)
                        message += " игроком " + player.killer.name
                    }
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
                        // Скрытие врагов
                        app.teams.filter { !it.players.contains(player.uniqueId) }
                            .forEach { boxTeam ->
                                boxTeam.players.mapNotNull { Bukkit.getPlayer(it) }
                                    .forEach { player.hidePlayer(app, it) }
                            }
                        player.sendMessage(Formatting.fine("Враги были скрыты."))
                        // Создание могилы
                        val grove = player.world.spawnEntity(
                            player.location.clone().subtract(0.0, 1.0, 0.0),
                            EntityType.ARMOR_STAND
                        )
                        val nmsGrove = (grove as CraftArmorStand).handle
                        grove.isInvulnerable = true
                        grove.customName = "§fМогила §b§l${player.name}"
                        nmsGrove.isMarker = true
                        nmsGrove.isInvisible = true
                        nmsGrove.isNoGravity = true
                        nmsGrove.isSmall = true
                        nmsGrove.customNameVisible = true
                        nmsGrove.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(item {
                            type = Material.CLAY_BALL
                            nbt("other", "g4")
                        }.build()))

                        team.players.remove(player.uniqueId)
                        Winner.tryGetWinner()
                        player.gameMode = GameMode.SPECTATOR
                        player.sendTitle("Вы проиграли!", "Наблюдение...")
                        message = "§e§lФИНАЛЬНОЕ УБИЙСТВО! $message"
                        if (player.killer != null) {
                            val killer = app.getUser(player.killer)!!
                            killer.finalKills++
                            killer.giveMoney(app.finalMoney)
                            killer.stat.kills++
                        }
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
        val team = app.teams.filter { boxTeam -> boxTeam.players.contains(player.uniqueId) }
        if (team.isNotEmpty()) {
            cancel = true
            if (!message.startsWith("!")) {
                team[0].players.mapNotNull { Bukkit.getPlayer(it) }.forEach {
                    it.sendMessage("" + team[0].color.chatColor + "${player.name} >§7 $message")
                }
            } else {
                Bukkit.getOnlinePlayers().forEach {
                    it.sendMessage(
                        "§f[" + team[0].color.chatColor + team[0].color.teamName.substring(
                            0,
                            1
                        ) + "§f] ${player.name} > " + message.drop(1)
                    )
                }
            }
        }
    }

    private fun playDamageEffect(location: Location) {
        location.getWorld().playSound(location, Sound.ENTITY_PLAYER_HURT, 0.1f, 1f)
        val packet = PacketPlayOutWorldParticles(
            EnumParticle.BLOCK_CRACK,
            false,
            location.getX().toFloat(), location.getY().toFloat(), location.getZ().toFloat(),
            .35f, 1.25f, .35f, .5f, 50, 152
        )
        location.getNearbyPlayers(24.0).forEach { (it as CraftPlayer).handle.playerConnection.sendPacket(packet) }
    }
}

object Winner {
    fun tryGetWinner() {
        val list = app.teams.filter { it.players.size > 0 }
        if (app.status != Status.GAME)
            return
        if (list.size == 1) {

            B.bc(" ")
            B.bc("§b―――――――――――――――――")
            B.bc("" + list[0].color.chatColor + list[0].color.teamName + " §f победили!")
            B.bc(" ")
            B.bc("§e§lТОП УБИЙСТВ")
            Bukkit.getOnlinePlayers().map { app.getUser(it) }.sortedBy { -it!!.tempKills }
                .subList(0, min(3, Bukkit.getOnlinePlayers().size))
                .forEachIndexed { index, user ->
                    B.bc(" §l${index + 1}. §e" + user!!.player?.name + " §с" + user.tempKills + " убийств")
                }
            B.bc("§b―――――――――――――――――")
            B.bc(" ")
            list[0].players.forEach {
                val user = app.getUser(it)
                if (user?.stat != null) {
                    user.stat.wins++
                    user.giveMoney(app.winMoney)
                    user.player?.sendTitle("§aПОБЕДА", "§aвы выиграли!")
                    val firework = user.player?.world!!.spawn(user.player!!.location, Firework::class.java)
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
            app.status = Status.END
        }
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            app.status = Status.END
            return
        }
    }
}