package me.func.box.donate

interface Donate {

    fun getPrice(): Int

    fun getTitle(): String

    fun getCode(): String

    fun getRare(): Rare

}