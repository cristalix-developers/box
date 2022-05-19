package me.func.box.cosmetic

import me.func.box.User

enum class MoneyBuy(val money: Int, val realPrice: Int, val percent: Int, val icon: String) : Donate {

    SMALL(1000, 19, 0, "coin2"),
    NORMAL(10000, 190, 20, "coin3"),
    BIG(50000, 190 * 5, 30, "coin4"),;

    override fun getPrice(): Int {
        return realPrice
    }

    override fun getTitle(): String {
        return "$money монет"
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