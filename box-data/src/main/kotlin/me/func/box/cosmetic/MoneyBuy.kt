package me.func.box.cosmetic

import me.func.box.User

enum class MoneyBuy(val money: Int, val realPrice: Int, val percent: Int, val icon: String) : Donate {

    SMALL(1000, 19, 0, "coin2"),
    NORMAL(10000, 159, 20, "coin3"),
    BIG(50000, 799, 30, "coin4"),;

    override fun getPrice(): Int {
        return realPrice
    }

    override fun getTitle(): String {
        return "§aКупить $money монет\n\n§7Цена: §b$realPrice кристаликов\n§7Скидка: ${if (percent == 0) "§cнет" else "§a$percent%"}"
    }

    override fun getCode(): String {
        return icon
    }

    override fun getRare(): Rare {
        TODO("Not yet implemented")
    }

    override fun give(user: User) {
        user.stat.money += money
    }
}