package me.func.box

import dev.implario.kensuke.Session
import dev.implario.kensuke.impl.bukkit.IBukkitKensukeUser
import org.bukkit.entity.Player
import java.util.*

class User(session: Session, stat: Stat?) : IBukkitKensukeUser {

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

    private var session: Session
    override fun getSession(): Session {
        return session
    }

    init {
        if (stat == null) {
            this.stat = Stat(UUID.fromString(session.userId), arrayListOf("hi"), "", 0, 0, 0, 0, 0, 0, 0, null, 500, Starter.NONE, mutableListOf(Starter.NONE))
        } else {
            if(stat.currentStarter == null)
                stat.currentStarter = Starter.NONE
            if(stat.starters == null || stat.starters!!.isEmpty())
                stat.starters = mutableListOf(Starter.NONE)
            this.stat = stat
        }
        if (this.stat.skins == null)
            this.stat.skins = arrayListOf("hi")
        this.session = session
    }

}