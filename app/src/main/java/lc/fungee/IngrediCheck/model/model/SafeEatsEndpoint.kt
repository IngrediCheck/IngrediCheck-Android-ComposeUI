package lc.fungee.IngrediCheck.model.model

enum class SafeEatsEndpoint(private val pathFormat: String) {
    DELETEME("deleteme"),
    INVENTORY("inventory/%s"),
    ANALYZE("analyze"),
    EXTRACT("extract"),
    FEEDBACK("feedback"),
    HISTORY("history"),
    LIST_ITEMS("lists/%s"),
    LIST_ITEMS_ITEM("lists/%s/%s"),
    PREFERENCE_LISTS_GRANDFATHERED("preferencelists/grandfathered"),
    PREFERENCE_LISTS_DEFAULT("preferencelists/default"),
    PREFERENCE_LISTS_DEFAULT_ITEMS("preferencelists/default/%s");

    fun format(vararg args: String): String = if (args.isEmpty()) pathFormat else String.format(pathFormat, *args)
}
