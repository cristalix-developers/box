package me.func.box

import dev.implario.kensuke.KensukeSession
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
import me.func.box.donate.Starter
import me.func.box.donate.Sword
import net.minecraft.server.v1_12_R1.Packet
import net.minecraft.server.v1_12_R1.PlayerConnection
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.*

class User(session: KensukeSession, stat: Stat?) : IBukkitKensukeUser {

    var looked = false
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
        if (connection == null)
            connection = (player as CraftPlayer).handle.playerConnection
        connection?.sendPacket(packet)
    }

    init {
        if (stat == null) {
            this.stat = Stat(
                UUID.fromString(session.userId), arrayListOf("hi"), "", 0, 0, 0, 0, 0, 0, 0, null, 500, mutableListOf(
                    Starter.NONE
                ), Starter.NONE, mutableListOf(
                    Sword.NONE
                ), Sword.NONE
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
        this.session = session
    }

}