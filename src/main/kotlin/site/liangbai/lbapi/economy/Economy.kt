package site.liangbai.lbapi.economy

data class Economy(val name: String)

fun economyOf(name: String): Economy { return Economy(name) }

enum class EconomyType(private val type: String) {
    NYE("nye"),
    PLACEHOLDER("placeholder"),
    PLAYER_POINTS("player_points"),
    VAULT("vault"),
    ITEM("item"),
    MULTI_ITEM("multi_item");

    fun instance(nyE: String? = null, placeholder: String? = null): String {
        return if (this == NYE) {
            "${type}_${nyE!!}"
        } else if (this == PLACEHOLDER) {
            "${type}_${placeholder!!}"
        } else type
    }
}