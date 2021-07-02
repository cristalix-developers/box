package me.func.box.donate

enum class Sword(private val price: Int, private val rare: Rare, private val title: String, private val code: Int) :
    Donate {

    NONE(0, Rare.COMMON, "Алмазный меч", 0),
    A(29, Rare.COMMON, "Эндер меч", 1),
    B(29, Rare.COMMON, "Раздвижной меч", 47),
    C(29, Rare.COMMON, "Дьявольский меч", 33),
    D(29, Rare.COMMON, "Бисквитный меч", 7),
    E(39, Rare.RARE, "Ледяная дубина", 29),
    F(39, Rare.RARE, "Нефриловый клинок", 39),
    G(39, Rare.RARE, "Клинок вампира", 43),
    H(79, Rare.LEGENDARY, "Коса смерти", 42),
    J(79, Rare.LEGENDARY, "Кровавая сабля", 41),
    K(79, Rare.LEGENDARY, "Топор разрушения", 34);

    override fun getPrice(): Int {
        return price
    }

    override fun getTitle(): String {
        return title
    }

    override fun getCode(): String {
        return code.toString()
    }

    override fun getRare(): Rare {
        return rare
    }
}