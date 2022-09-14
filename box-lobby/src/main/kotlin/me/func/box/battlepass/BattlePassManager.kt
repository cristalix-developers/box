package me.func.box.battlepass

import dev.implario.bukkit.item.item
import me.func.box.BattlePassUtil
import me.func.box.User
import me.func.box.app
import me.func.box.cosmetic.*
import me.func.mod.Anime
import me.func.mod.battlepass.BattlePass
import me.func.mod.battlepass.BattlePass.onBuyAdvanced
import me.func.mod.battlepass.BattlePass.onBuyPage
import me.func.mod.battlepass.BattlePass.sale
import me.func.mod.battlepass.BattlePassPageAdvanced
import me.func.mod.conversation.ModTransfer
import me.func.protocol.ActionLog
import me.func.protocol.DropRare
import me.func.protocol.LogPacket
import me.func.protocol.battlepass.BattlePassUserData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import pw.lach.p13n.network.tower.GiveModelToUserPackage
import ru.cristalix.core.CoreApi
import ru.cristalix.core.formatting.Formatting
import ru.cristalix.core.network.ISocketClient
import ru.cristalix.core.network.packages.MoneyTransactionRequestPackage
import ru.cristalix.core.network.packages.MoneyTransactionResponsePackage
import java.util.*
import java.util.function.Consumer
import java.util.function.Function

const val BATTLEPASS_PRICE = 299

object BattlePassManager {

    private val premium = item {
        type = Material.CLAY_BALL
        nbt("other", "achievements_many")
    }

    private val premiumPers = item {
        type = Material.CLAY_BALL
        text("§6Персонализация премиальный беброкич")
        nbt("p13nModelId", "0de94acf-61b2-4dd8-b015-b7bb552321ab")
        nbt("rare", DropRare.LEGENDARY.ordinal)
    }

    private val classicPers = item {
        type = Material.CLAY_BALL
        text("§bПерсонализация беброкич")
        nbt("p13nModelId", "ebfa2220-f39d-44e3-b71c-60de39b15c70")
        nbt("rare", DropRare.LEGENDARY.ordinal)
    }

    val rewards = listOf(
        listOf(
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.SMALL,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.BIG,
            BattlePassKit.SMALL,
            BreakBedEffect.REDSTONE,
            BattlePassKit.EPIC,
            Sword.E
        ) to listOf(
            BattlePassKit.BIG,
            BattlePassKit.MEDIUM,
            KillMessage.GALACTIC,
            BattlePassKit.MEDIUM,
            Starter.HEAL,
            BattlePassKit.BIG,
            BreakBedEffect.REDSTONE,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            Sword.B
        ),
        listOf(
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.SMALL,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            KillMessage.BANANA,
            BattlePassKit.SMALL,
            BreakBedEffect.VILLAGER_ANGRY,
            BattlePassKit.EPIC,
            Sword.B
        ) to listOf(
            BattlePassKit.MEDIUM,
            BattlePassKit.EPIC,
            Sword.E,
            BattlePassKit.BIG,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BreakBedEffect.REDSTONE,
            BattlePassKit.MEDIUM,
            Sword.G
        ),
        listOf(
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.SMALL,
            Armor.RENEGATE,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.BIG,
            BreakBedEffect.FALLING_DUST,
            BattlePassKit.SMALL,
            BattlePassKit.EPIC
        ) to listOf(
            BattlePassKit.EPIC,
            BattlePassKit.MEDIUM,
            KillMessage.INSECT,
            BattlePassKit.MEDIUM,
            Starter.TITAN,
            BattlePassKit.BIG,
            BattlePassKit.MEDIUM,
            BreakBedEffect.REDSTONE,
            BattlePassKit.MEDIUM,
            Sword.F
        ),
        listOf(
            BreakBedEffect.SPELL_WITCH,
            BattlePassKit.MEDIUM,
            BattlePassKit.SMALL,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.BIG,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            Starter.DEFENDER,
            BattlePassKit.EPIC,
        ) to listOf(
            BattlePassKit.BIG,
            KillMessage.COMPUTER,
            BattlePassKit.MEDIUM,
            Starter.MINER,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.BIG,
            BreakBedEffect.VILLAGER_ANGRY,
            BattlePassKit.MEDIUM,
            Sword.M
        ),
        listOf(
            BreakBedEffect.WATER_DROP,
            BattlePassKit.MEDIUM,
            BattlePassKit.SMALL,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.BIG,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.EPIC,
            Sword.C
        ) to listOf(
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            Starter.BLOCKER,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.BIG,
            BattlePassKit.MEDIUM,
            BreakBedEffect.SLIME,
            BattlePassKit.MEDIUM,
            Sword.J
        ),
        listOf(
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.SMALL,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.SMALL,
            Starter.DEFENDER,
            BattlePassKit.EPIC,
            Sword.B
        ) to listOf(
            BattlePassKit.BIG,
            BattlePassKit.MEDIUM,
            Starter.BLOCKER,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.BIG,
            BattlePassKit.MEDIUM,
            BreakBedEffect.SLIME,
            BattlePassKit.EPIC,
            Sword.M
        ),
        listOf(
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.SMALL,
            BattlePassKit.SMALL,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BreakBedEffect.REDSTONE,
            BattlePassKit.MEDIUM,
            BattlePassKit.EPIC,
            object : Donate{
                override fun getIcon() = classicPers
                override fun getPrice() = 999
                override fun getRare() = Rare.LEGENDARY
                override fun getTitle() = classicPers.itemMeta.displayName
                override fun getCode(): String = ""
                override fun give(user: User) {
                    CoreApi.get().socketClient.write(
                        GiveModelToUserPackage(
                            user.stat.id, UUID.fromString("ebfa2220-f39d-44e3-b71c-60de39b15c70")
                        )
                    )
                }
            }
        ) to listOf(
            BattlePassKit.MEDIUM,
            KillMessage.COMPUTER,
            BattlePassKit.MEDIUM,
            Starter.MINER,
            BattlePassKit.MEDIUM,
            BattlePassKit.MEDIUM,
            BattlePassKit.BIG,
            BreakBedEffect.VILLAGER_ANGRY,
            BattlePassKit.EPIC,
            object : Donate{
                override fun getIcon() = premiumPers
                override fun getPrice() = 999
                override fun getRare() = Rare.LEGENDARY
                override fun getTitle() = premiumPers.itemMeta.displayName
                override fun getCode(): String = ""
                override fun give(user: User) {
                    CoreApi.get().socketClient.write(
                      GiveModelToUserPackage(
                          user.stat.id, UUID.fromString("0de94acf-61b2-4dd8-b015-b7bb552321ab")
                      )
                    )
                }
            }
        )
    )

    private val battlePass = BattlePass.new(BATTLEPASS_PRICE) {
        pages = rewards.mapIndexed { index, drop ->
            BattlePassPageAdvanced(
                100 + 25 * index,
                10 + index * 10,
                drop.first.map { (it as Donate).getIcon() },
                drop.second.map { (it as Donate).getIcon() }
            )
        }.toMutableList()
        sale(0.0)
        onBuyAdvanced { player ->
            val uuid = player.uniqueId
            app.getUser(player).let { data ->
                player.closeInventory()

                if (data.stat.progress!!.advanced) {
                    return@onBuyAdvanced
                }

                buy(
                    player,
                    BATTLEPASS_PRICE,
                    "Покупка премиум адркадного BattlePass'а."
                ) {
                    data.stat.progress!!.advanced = true
                    Anime.itemTitle(player, premium, "§bУспешно", "Собирайте награды!", 3.5)

                    ISocketClient.get().write(
                        LogPacket(
                            player.uniqueId,
                            ActionLog.BATTLEPASS,
                            "Игрок купил BattlePass"
                        ))

                    Bukkit.getOnlinePlayers().forEach {
                        Anime.topMessage(
                            it,
                            Formatting.fine("§e${player.name} §fкупил §bПремиум §6BattlePass§f!")
                        )
                        it.sendMessage("")
                        it.sendMessage(Formatting.fine("§7Игрок §e${player.name} §7купил §bпремиум §6BattlePass§7!"))
                        it.sendMessage("")
                    }
                }
            }

        }
        onBuyPage { player, cost ->
            player.closeInventory()

            app.getUser(player).stat.progress?.let { data ->
                pages.firstOrNull { it.skipPrice == cost }
                    ?.let { page ->
                        buy(player, cost, "Пропуск уровня аркадного BattlePass.") {
                            data.exp += page.requiredExp

                            ISocketClient.get().write(
                                LogPacket(
                                    player.uniqueId,
                                    ActionLog.SKIPLEVEL,
                                    "Игрок пропустил уровень BattlePass"
                                ))

                            Anime.itemTitle(player, premium, "§bУспешно", "Новый уровень", 2.6)
                            Bukkit.getOnlinePlayers().forEach {
                                Anime.topMessage(
                                    it,
                                    Formatting.fine("§e${player.name} §fпропустил страницу §6BattlePass§f!")
                                )
                            }
                        }
                    }
            }
        }
        facade.tags.add("Выполняйте квесты - получайте призы!")
        //facade.tags.add("BattlePass завершится в 00.00.0000")
        questStatusUpdater = Function<Player, List<String>> { player ->
            BattlePassUtil.getQuestLore(app.getUser(player))
        }

    }

    init {
        Anime.createReader("bp:reward") { player, buffer ->
            val advanced = buffer.readBoolean()
            val page = buffer.readInt()
            val index = buffer.readInt()
            val data = app.getUser(player)

            data.stat.progress?.let {
                if (advanced && !it.advanced)
                    return@createReader
                var exp = it.exp
                var level = 1

                for (current in battlePass.pages) {
                    for (item in current.items) {
                        if (exp >= current.requiredExp) {
                            exp -= current.requiredExp
                            level++
                        } else break
                    }
                }

                val position =
                    (if (advanced) battlePass.pages.size * battlePass.pages.first().items.size else 0) + page * 10 + index

                data.stat.claimedRewards?.let { claimed ->
                    if (claimed.contains(position))
                        return@createReader

                    if (level > page * 10 + index) {
                        val reward = (if (advanced) rewards[page].second else rewards[page].first)[index]

                        (reward as Donate).give(data)
                        claimed.add(position)
                        Anime.killboardMessage(player, Formatting.fine("Награда: " + reward.getTitle()))
                        ISocketClient.get().write(
                            LogPacket(
                                player.uniqueId,
                                ActionLog.REWARD,
                                "Игрок собрал награду: ${reward.getTitle()}"
                            ))
                    }
                }
            }
        }
    }

    fun buy(player: Player, price: Int, desc: String, successfully: Consumer<Player>) {
        ISocketClient.get().writeAndAwaitResponse<MoneyTransactionResponsePackage>(
            MoneyTransactionRequestPackage(
                player.uniqueId,
                price,
                true,
                desc
            )
        ).thenAccept {
            if (it.errorMessage.isNullOrEmpty()) {
                successfully.accept(player)
            } else {
                Anime.killboardMessage(player, "Ошибка! " + it.errorMessage)
            }
        }
    }

    fun show(player: Player) {
        BattlePass.send(player, battlePass)

        val user = app.getUser(player)
        var progress = user.stat.progress

        if (progress == null)
            progress = BattlePassUserData(0, false)

        ModTransfer(battlePass.uuid.toString()).apply {
            integer(user.stat.claimedRewards?.size ?: 0)
            user.stat.claimedRewards?.forEach { integer(it) }
        }.send("bp:claimed", player)

        BattlePass.show(player, battlePass, progress)

    }
}
