package me.func.box

import clepto.bukkit.B
import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
import me.func.box.cosmetic.BreakBedEffect
import me.func.box.cosmetic.KillMessage
import me.func.box.cosmetic.Starter
import me.func.box.cosmetic.Sword
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

class User(session: KensukeSession, stat: Stat?) : IBukkitKensukeUser {

    var bed: Location? = null
    var tempKills = 0
    var compassToPlayer = true
    var finalKills = 0
    var lock = false

    var stat: Stat
    private var player: Player? = null
    override fun setPlayer(p0: Player?) {
        if (p0 != null) {
            player = p0
        }
    }

    override fun getPlayer(): Player? {
        return player
    }

    private var session: KensukeSession
    override fun getSession(): KensukeSession {
        return session
    }

    private var connection: PlayerConnection? = null

    fun sendPacket(packet: Packet<*>) {
        if (player == null)
            return
        if (connection == null)
            connection = (player as CraftPlayer).handle.playerConnection
        connection?.sendPacket(packet)
    }

    fun giveMoney(toGive: Int) {
        player!!.sendMessage("§eПолучено $toGive монет.")
        B.postpone(1) {
            player!!.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent("§e§l+$toGive монет")
            )
        }
        stat.money += toGive
    }

    init {
        if (stat == null) {
            this.stat = Stat(
                UUID.fromString(session.userId), arrayListOf("hi"), "", 0, 0, 0, 0, 0, 0, 0, null, 500, mutableListOf(
                    Starter.NONE
                ), Starter.NONE, mutableListOf(
                    Sword.NONE
                ), Sword.NONE, 0, KillMessage.NONE,
                mutableListOf(
                    KillMessage.NONE
                ),
                BreakBedEffect.NONE,
                mutableListOf(
                    BreakBedEffect.NONE
                ),
                0,
                0,
                0
            )
        } else {
            if (stat.currentStarter == null)
                stat.currentStarter = Starter.NONE
            if (stat.starters == null || stat.starters!!.isEmpty())
                stat.starters = mutableListOf(Starter.NONE)
            if (stat.currentSword == null)
                stat.currentSword = Sword.NONE
            if (stat.swords == null || stat.swords!!.isEmpty())
                stat.swords = mutableListOf(Sword.NONE)
            this.stat = stat
        }
        if (this.stat.skins == null)
            this.stat.skins = arrayListOf("hi")
        if (stat?.currentKillMessage == null)
            stat?.currentKillMessage = KillMessage.NONE
        if (stat?.killMessages == null || stat.killMessages.isEmpty())
            stat?.killMessages = mutableListOf(KillMessage.NONE)
        if (stat?.currentBreakBedEffect == null)
            stat?.currentBreakBedEffect = BreakBedEffect.NONE
        if (stat?.breakBedEffects == null || stat.breakBedEffects.isEmpty())
            stat?.breakBedEffects = mutableListOf(BreakBedEffect.NONE)
        this.session = session
    }
}