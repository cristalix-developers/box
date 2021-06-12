package me.func.box

import clepto.bukkit.B
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import ru.cristalix.core.party.IPartyService
import ru.cristalix.core.party.PartySnapshot
import ru.cristalix.core.realm.IRealmService
import ru.cristalix.core.realm.RealmId
import ru.cristalix.core.realm.RealmInfo
import ru.cristalix.core.realm.RealmStatus
import ru.cristalix.core.transfer.ITransferService
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.function.Consumer

class ClickServer(private val server: String, private val maxPlayers: Int) : Consumer<Player> {

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun sendToServer(player: Player) {
        playerJoinRandomGAme(player)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    private fun playerJoinRandomGAme(p: Player) {
        val party: Optional<*> = IPartyService.get().getPartyByMember(p.uniqueId).get()
        if (party.isPresent) {
            val party1 = party.get() as PartySnapshot
            if (party1.leader == p.uniqueId) {
                val realm = getRealm(server, party1.members.size)
                if (realm.isPresent) {
                    val realmInfo = IRealmService.get().getRealmById(realm.get())
                    if (realmInfo.currentPlayers + party1.members.size <= realmInfo
                            .maxPlayers
                    ) {
                        for (uuid in party1.members) {
                            ITransferService.get().transfer(uuid, realm.get())
                        }
                    } else {
                        p.spigot().sendMessage(
                            ChatMessageType.ACTION_BAR,
                            TextComponent("§3Не найдено свободных серверов")
                        )
                    }
                } else {
                    p.spigot().sendMessage(
                        ChatMessageType.ACTION_BAR,
                        TextComponent("§3Не найдено свободных серверов")
                    )
                }
            } else {
                p.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent("§3Вы должны быть лидером пати")
                )
            }
        } else {
            val realmId = getRealm(server, 1)
            if (realmId.isPresent) {
                val realmInfo = IRealmService.get().getRealmById(realmId.get())
                ITransferService.get().transfer(p.uniqueId, realmId.get())
            } else {
                p.spigot().sendMessage(
                    ChatMessageType.ACTION_BAR,
                    TextComponent("§3Не найдено свободных серверов")
                )
            }
        }
    }

    private fun getRealm(realm: String, mintoJoin: Int): Optional<RealmId> {
        var maxRealm: RealmInfo? = null
        var minRealm: RealmInfo? = null
        for (realmInfo in IRealmService.get().getRealmsOfType(realm)) {
            if (realmInfo.status != RealmStatus.WAITING_FOR_PLAYERS
                || realmInfo.currentPlayers + mintoJoin > realmInfo.maxPlayers
            ) {
                continue
            }
            if (realmInfo.currentPlayers + mintoJoin <= maxPlayers) {
                if (maxRealm == null) {
                    maxRealm = realmInfo
                } else if (maxRealm.currentPlayers < realmInfo.currentPlayers) {
                    maxRealm = realmInfo
                }
            } else {
                if (minRealm == null) {
                    minRealm = realmInfo
                } else if (minRealm.currentPlayers > realmInfo.currentPlayers) {
                    minRealm = realmInfo
                }
            }
        }
        return if (maxRealm != null) Optional.of(maxRealm.realmId) else Optional.empty()
    }

    override fun accept(player: Player) {
        val user = app.getUser(player)!!
        if (user.looked)
            return
        user.looked = true
        B.postpone(1) { user.looked = false }
        B.postpone(2) {
            try {
                sendToServer(player)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }
}