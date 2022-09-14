package me.func.box

import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
import me.func.box.cosmetic.BreakBedEffect
import me.func.box.cosmetic.KillMessage
import me.func.box.cosmetic.Starter
import me.func.box.cosmetic.Sword
import me.func.box.quest.QuestGenerator
import me.func.protocol.ui.battlepass.BattlePassUserData
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import net.minecraft.server.v1_12_R1.MinecraftServer
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

class User(session: KensukeSession, stat: Stat?, oldStat: Stat?) : IBukkitKensukeUser {

    var bed: Location? = null
    var tempKills = 0
    var compassToPlayer = true
    var finalKills = 0
    var lock = false
    var bedDestroy = 0
    var buyItems = 0
    var blockDestroy = 0

    var stat: Stat
    private var player: Player? = null
    override fun setPlayer(p0: Player?) {
        if (p0 != null) {
            player = p0
        }
    }

    override fun getPlayer() = player
    private var session: KensukeSession
    override fun getSession() = session
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
        MinecraftServer.SERVER.postToNextTick {
            player!!.spigot().sendMessage(
                ChatMessageType.ACTION_BAR,
                TextComponent("§e§l+$toGive монет")
            )
        }
        stat.money += toGive
    }

    init {
        if (stat == null) {
            this.stat = oldStat?.apply {
                kills = 0
                deaths = 0
                wins = 0
                games = 0
                starters?.clear()
                currentStarter = null
                progress = BattlePassUserData(0, false)
                claimedRewards = mutableListOf()
                data = QuestGenerator.generate()
            } ?: Stat(
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
                0,
                BattlePassUserData(0, false),
                mutableListOf(),
                QuestGenerator.generate(),
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
            if (stat.data == null || stat.data!!.isEmpty())
                stat.data = QuestGenerator.generate()
            if (stat.progress == null)
                stat.progress = BattlePassUserData(0, false)
            if (stat.claimedRewards == null)
                stat.claimedRewards = mutableListOf()
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
        if (stat?.data == null || stat.data!!.isEmpty())
            stat?.data = QuestGenerator.generate()
        if (stat?.progress == null)
            stat?.progress = BattlePassUserData(0, false)
        if (stat?.claimedRewards == null) {
            stat?.claimedRewards = mutableListOf()
        }
        this.session = session
    }
}